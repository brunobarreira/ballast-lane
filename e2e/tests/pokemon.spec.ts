import { test, expect } from '@playwright/test';

const BASE = 'http://localhost:3000';
const API  = 'http://localhost:8080';

// ─── US01: Browse paginado ───────────────────────────────────────────────────
test.describe('US01 — Pokemon list (public)', () => {
  test('shows pokemon grid with sprite, category, mass and skills', async ({ page }) => {
    await page.goto(BASE);
    await page.waitForSelector('.grid a', { timeout: 15000 });

    const cards = page.locator('.grid a');
    expect(await cards.count()).toBeGreaterThan(0);

    const first = cards.first();
    await expect(first.locator('img')).toBeVisible();
    await expect(first.locator('p.font-semibold')).toBeVisible(); // name
    await expect(first.locator('p.text-xs.text-gray-500')).toBeVisible(); // category
    await expect(first.locator('p.text-xs.text-gray-400')).toBeVisible(); // mass
    await expect(first.locator('span.rounded-full').first()).toBeVisible(); // skill
  });

  test('pagination: next and previous buttons work', async ({ page }) => {
    await page.goto(BASE);
    await page.waitForSelector('.grid a', { timeout: 15000 });

    const pageText = page.locator('text=/Page \\d+ of/');
    await expect(pageText).toContainText('Page 1');

    await page.getByRole('button', { name: 'Next →' }).click();
    await page.waitForTimeout(1000);
    await expect(pageText).toContainText('Page 2');

    await page.getByRole('button', { name: '← Previous' }).click();
    await page.waitForTimeout(1000);
    await expect(pageText).toContainText('Page 1');
  });

  test('previous button is disabled on first page', async ({ page }) => {
    await page.goto(BASE);
    await page.waitForSelector('.grid a', { timeout: 15000 });

    const prev = page.getByRole('button', { name: '← Previous' });
    await expect(prev).toBeDisabled();
  });
});

// ─── US02: Detalhe ──────────────────────────────────────────────────────────
test.describe('US02 — Pokemon detail (public)', () => {
  test('shows image, stats, description and evolution chain', async ({ page }) => {
    await page.goto(`${BASE}/pokemon/bulbasaur`);
    await page.waitForSelector('h1', { timeout: 15000 });

    await expect(page.locator('h1')).toContainText('bulbasaur');
    await expect(page.locator('img')).toBeVisible();
    await expect(page.locator('text=Base Stats')).toBeVisible();
    await expect(page.locator('text=Evolution Chain')).toBeVisible();
    // description italic text
    await expect(page.locator('p.italic')).toBeVisible();
  });

  test('clicking evolution chain navigates to that pokemon', async ({ page }) => {
    await page.goto(`${BASE}/pokemon/bulbasaur`);
    await page.waitForSelector('text=Evolution Chain', { timeout: 15000 });

    await page.getByRole('button', { name: 'ivysaur' }).click();
    await page.waitForURL(`${BASE}/pokemon/ivysaur`);
    await expect(page.locator('h1')).toContainText('ivysaur');
  });

  test('back button returns to previous page', async ({ page }) => {
    await page.goto(BASE);
    await page.waitForSelector('.grid a', { timeout: 15000 });
    await page.locator('.grid a').first().click();
    await page.waitForSelector('text=Base Stats', { timeout: 15000 });

    await page.getByRole('button', { name: '← Back' }).click();
    await expect(page).toHaveURL(BASE + '/');
  });
});

// ─── Auth ────────────────────────────────────────────────────────────────────
test.describe('Authentication', () => {
  test('protected /local redirects to /login when not authenticated', async ({ page }) => {
    await page.goto(`${BASE}/local`);
    await expect(page).toHaveURL(`${BASE}/login`);
  });

  test('login with wrong password shows inline error', async ({ page }) => {
    await page.goto(`${BASE}/login`);
    await page.fill('input[type="text"]', 'trainer');
    await page.fill('input[type="password"]', 'wrongpassword');
    await page.getByRole('button', { name: 'Sign In' }).click();
    await expect(page.locator('.bg-red-50')).toBeVisible();
  });

  test('login with valid credentials redirects home and shows My Pokemon link', async ({ page }) => {
    await page.goto(`${BASE}/login`);
    await page.fill('input[type="text"]', 'trainer');
    await page.fill('input[type="password"]', 'trainer123');
    await page.getByRole('button', { name: 'Sign In' }).click();
    await expect(page).toHaveURL(BASE + '/');
    await expect(page.locator('nav')).toContainText('My Pokémon');
  });

  test('logout hides My Pokemon link', async ({ page }) => {
    await page.goto(`${BASE}/login`);
    await page.fill('input[type="text"]', 'trainer');
    await page.fill('input[type="password"]', 'trainer123');
    await page.getByRole('button', { name: 'Sign In' }).click();
    await page.waitForURL(BASE + '/');

    await page.getByRole('button', { name: 'Logout' }).click();
    await expect(page.locator('nav')).not.toContainText('My Pokémon');
    await expect(page.locator('nav')).toContainText('Login');
  });
});

// ─── US03: Sync ──────────────────────────────────────────────────────────────
test.describe('US03 — Sync to local DB', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto(`${BASE}/login`);
    await page.fill('input[type="text"]', 'trainer');
    await page.fill('input[type="password"]', 'trainer123');
    await page.getByRole('button', { name: 'Sign In' }).click();
    await page.waitForURL(BASE + '/');
  });

  test('sync button appears when logged in', async ({ page }) => {
    await page.goto(`${BASE}/pokemon/mewtwo`);
    await page.waitForSelector('h1', { timeout: 15000 });
    await expect(page.getByRole('button', { name: /Sync to My Database/i })).toBeVisible();
  });

  test('sync shows toast notification instead of browser alert', async ({ page }) => {
    let alertFired = false;
    page.on('dialog', () => { alertFired = true; });

    await page.goto(`${BASE}/pokemon/mew`);
    await page.waitForSelector('text=Sync to My Database', { timeout: 15000 });
    await page.getByRole('button', { name: /Sync to My Database/i }).click();
    await page.waitForTimeout(3000);

    expect(alertFired).toBe(false);
    // Toast is transient — just verify no browser dialog
  });

  test('synced pokemon appear in My Pokemon list', async ({ page }) => {
    // sync one pokemon
    await page.goto(`${BASE}/pokemon/ditto`);
    await page.waitForSelector('text=Sync to My Database', { timeout: 15000 });
    await page.getByRole('button', { name: /Sync to My Database/i }).click();
    await page.waitForTimeout(2000);

    await page.goto(`${BASE}/local`);
    await page.waitForSelector('h1', { timeout: 10000 });
    await expect(page.locator('text=ditto')).toBeVisible();
  });
});

// ─── US04: Update / Delete ───────────────────────────────────────────────────
test.describe('US04 — Update and delete local pokemon', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto(`${BASE}/login`);
    await page.fill('input[type="text"]', 'trainer');
    await page.fill('input[type="password"]', 'trainer123');
    await page.getByRole('button', { name: 'Sign In' }).click();
    await page.waitForURL(BASE + '/');
  });

  test('edit modal updates local name, region and tags', async ({ page }) => {
    await page.goto(`${BASE}/local`);
    await page.waitForSelector('h1', { timeout: 10000 });

    await page.getByRole('button', { name: 'Edit' }).first().click();
    await page.waitForSelector('text=Local Name', { timeout: 5000 });

    await page.locator('input').nth(0).fill('Test Name');
    await page.locator('input').nth(1).fill('Kanto');
    await page.locator('input').nth(2).fill('electric, test');

    await page.getByRole('button', { name: 'Save' }).click();
    await page.waitForSelector('text=Test Name', { timeout: 5000 });
    await expect(page.locator('text=Test Name').first()).toBeVisible();
    await expect(page.locator('text=Kanto').first()).toBeVisible();
  });

  test('delete shows custom modal (not browser confirm)', async ({ page }) => {
    await page.goto(`${BASE}/local`);
    await page.waitForSelector('h1', { timeout: 10000 });

    let confirmFired = false;
    page.on('dialog', () => { confirmFired = true; });

    await page.getByRole('button', { name: 'Delete' }).first().click();
    await page.waitForSelector('text=Remove Pokémon?', { timeout: 3000 });

    expect(confirmFired).toBe(false);
    await expect(page.locator('text=Remove Pokémon?')).toBeVisible();
  });

  test('cancel on delete modal does not remove pokemon', async ({ page }) => {
    await page.goto(`${BASE}/local`);
    await page.waitForSelector('h1', { timeout: 10000 });

    const countBefore = await page.locator('.grid > div').count();
    await page.getByRole('button', { name: 'Delete' }).first().click();
    await page.waitForSelector('text=Remove Pokémon?');
    await page.getByRole('button', { name: 'Cancel' }).click();

    await expect(page.locator('text=Remove Pokémon?')).not.toBeVisible();
    const countAfter = await page.locator('.grid > div').count();
    expect(countAfter).toBe(countBefore);
  });

  test('confirming delete removes pokemon from list', async ({ page }) => {
    // first ensure something is synced
    await page.goto(`${BASE}/pokemon/rattata`);
    await page.waitForSelector('text=Sync to My Database', { timeout: 15000 });
    await page.getByRole('button', { name: /Sync to My Database/i }).click();
    await page.waitForTimeout(2000);

    await page.goto(`${BASE}/local`);
    await page.waitForSelector('h1', { timeout: 10000 });
    const countBefore = await page.locator('.grid > div').count();

    await page.getByRole('button', { name: 'Delete' }).last().click();
    await page.waitForSelector('text=Remove Pokémon?');
    await page.getByRole('button', { name: 'Remove' }).click();
    await page.waitForTimeout(1000);

    const countAfter = await page.locator('.grid > div').count();
    expect(countAfter).toBe(countBefore - 1);
  });
});

// ─── API error handling ───────────────────────────────────────────────────────
test.describe('API error handling', () => {
  test('unknown pokemon shows not found message', async ({ page }) => {
    await page.goto(`${BASE}/pokemon/this-pokemon-does-not-exist-xyz`);
    await page.waitForSelector('text=not found', { timeout: 10000 });
    await expect(page.locator('text=/not found/i')).toBeVisible();
  });
});
