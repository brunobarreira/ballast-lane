# Playwright E2E Test Evidence

**Date:** 2026-10-30  
**Browser:** Chrome (headed, 1280×720)  
**Total tests:** 18 · **Passed:** 18 · **Failed:** 0  
**Runner:** Playwright 1.39.0  
**App:** http://localhost:3000 (frontend) + http://localhost:8080 (backend)

Screenshots captured in `test-results/<test-name>/test-finished-1.png`.  
Videos captured in `test-results/<test-name>/video.webm`.

---

## US01 — Pokémon List (Public)

### 1. Shows pokemon grid with sprite, category, mass and skills
**Result:** ✅ PASS  
**What was verified:** Homepage loads a paginated grid where each card renders an `<img>` sprite, a bold Pokémon name, a gray category line, a lighter mass line, and at least one rounded skill badge.  
**Screenshot:** `test-results/pokemon-US01-—-Pokemon-list-public-shows-pokemon-grid-with-sprite-category-mass-and-skills-chromium/test-finished-1.png`

### 2. Pagination: next and previous buttons work
**Result:** ✅ PASS  
**What was verified:** Clicking **Next →** advances the page counter from "Page 1" to "Page 2". Clicking **← Previous** returns it to "Page 1".  
**Screenshot:** `test-results/pokemon-US01-—-Pokemon-list-public-pagination-next-and-previous-buttons-work-chromium/test-finished-1.png`

### 3. Previous button is disabled on first page
**Result:** ✅ PASS  
**What was verified:** On initial load the **← Previous** button has the HTML `disabled` attribute, preventing navigation below page 1.  
**Screenshot:** `test-results/pokemon-US01-—-Pokemon-list-public-previous-button-is-disabled-on-first-page-chromium/test-finished-1.png`

---

## US02 — Pokémon Detail (Public)

### 4. Shows image, stats, description and evolution chain
**Result:** ✅ PASS  
**What was verified:** `/pokemon/bulbasaur` renders an `<h1>` with the name, a visible sprite image, a **Base Stats** section with colour-coded bars, an **Evolution Chain** section, and an italic flavour-text description.  
**Screenshot:** `test-results/pokemon-US02-—-Pokemon-detail-public-shows-image-stats-description-and-evolution-chain-chromium/test-finished-1.png`

### 5. Clicking evolution chain navigates to that pokemon
**Result:** ✅ PASS  
**What was verified:** Clicking the **ivysaur** button in the Evolution Chain section navigates to `/pokemon/ivysaur` and the `<h1>` updates to "ivysaur".  
**Screenshot:** `test-results/pokemon-US02-—-Pokemon-detail-public-clicking-evolution-chain-navigates-to-that-pokemon-chromium/test-finished-1.png`

### 6. Back button returns to previous page
**Result:** ✅ PASS  
**What was verified:** Navigating from the list to a detail page and clicking **← Back** returns the browser to `/`.  
**Screenshot:** `test-results/pokemon-US02-—-Pokemon-detail-public-back-button-returns-to-previous-page-chromium/test-finished-1.png`

---

## Authentication

### 7. Protected /local redirects to /login when not authenticated
**Result:** ✅ PASS  
**What was verified:** Visiting `/local` without a JWT in localStorage immediately redirects to `/login`.  
**Screenshot:** `test-results/pokemon-Authentication-protected-local-redirects-to-login-when-not-authenticated-chromium/test-finished-1.png`

### 8. Login with wrong password shows inline error
**Result:** ✅ PASS  
**What was verified:** Submitting the login form with `trainer` / `wrongpassword` shows an inline error banner (`.bg-red-50`) instead of a browser alert.  
**Screenshot:** `test-results/pokemon-Authentication-login-with-wrong-password-shows-inline-error-chromium/test-finished-1.png`

### 9. Login with valid credentials redirects home and shows My Pokémon link
**Result:** ✅ PASS  
**What was verified:** Submitting `trainer` / `trainer123` redirects to `/` and the navbar now shows a **My Pokémon** link.  
**Screenshot:** `test-results/pokemon-Authentication-login-with-valid-credentials-redirects-home-and-shows-My-Pokemon-link-chromium/test-finished-1.png`

### 10. Logout hides My Pokémon link
**Result:** ✅ PASS  
**What was verified:** After login, clicking **Logout** removes the **My Pokémon** link from the navbar and shows **Login** instead.  
**Screenshot:** `test-results/pokemon-Authentication-logout-hides-My-Pokemon-link-chromium/test-finished-1.png`

---

## US03 — Sync to Local DB (Authenticated)

### 11. Sync button appears when logged in
**Result:** ✅ PASS  
**What was verified:** The **💾 Sync to My Database** button is visible on `/pokemon/mewtwo` when authenticated and hidden when not.  
**Screenshot:** `test-results/pokemon-US03-—-Sync-to-local-DB-sync-button-appears-when-logged-in-chromium/test-finished-1.png`

### 12. Sync shows toast notification instead of browser alert
**Result:** ✅ PASS  
**What was verified:** Clicking sync on `/pokemon/mew` triggers no browser `dialog` event. A custom toast component is used instead of `window.alert()`.  
**Screenshot:** `test-results/pokemon-US03-—-Sync-to-local-DB-sync-shows-toast-notification-instead-of-browser-alert-chromium/test-finished-1.png`

### 13. Synced pokemon appear in My Pokémon list
**Result:** ✅ PASS  
**What was verified:** After syncing ditto via the detail page, navigating to `/local` shows "ditto" in the local collection grid.  
**Screenshot:** `test-results/pokemon-US03-—-Sync-to-local-DB-synced-pokemon-appear-in-My-Pokemon-list-chromium/test-finished-1.png`

---

## US04 — Update and Delete Local Pokémon (Authenticated)

### 14. Edit modal updates local name, region and tags
**Result:** ✅ PASS  
**What was verified:** Clicking **Edit** opens an inline modal. Filling in "Test Name", "Kanto", "electric, test" and clicking **Save** updates the card immediately — "Test Name" and "Kanto" are visible in the grid.  
**Screenshot:** `test-results/pokemon-US04-—-Update-and-delete-local-pokemon-edit-modal-updates-local-name-region-and-tags-chromium/test-finished-1.png`

### 15. Delete shows custom modal (not browser confirm)
**Result:** ✅ PASS  
**What was verified:** Clicking **Delete** opens a custom in-page confirmation modal showing "Remove Pokémon?" with no `window.confirm()` browser dialog.  
**Screenshot:** `test-results/pokemon-US04-—-Update-and-delete-local-pokemon-delete-shows-custom-modal-not-browser-confirm--chromium/test-finished-1.png`

### 16. Cancel on delete modal does not remove pokemon
**Result:** ✅ PASS  
**What was verified:** Clicking **Cancel** in the delete confirmation modal closes it and leaves the grid count unchanged.  
**Screenshot:** `test-results/pokemon-US04-—-Update-and-delete-local-pokemon-cancel-on-delete-modal-does-not-remove-pokemon-chromium/test-finished-1.png`

### 17. Confirming delete removes pokemon from list
**Result:** ✅ PASS  
**What was verified:** After syncing rattata, the `/local` grid count is captured. Clicking **Delete → Remove** decrements the count by exactly 1.  
**Screenshot:** `test-results/pokemon-US04-—-Update-and-delete-local-pokemon-confirming-delete-removes-pokemon-from-list-chromium/test-finished-1.png`

---

## API Error Handling

### 18. Unknown pokemon shows not found message
**Result:** ✅ PASS  
**What was verified:** Navigating to `/pokemon/this-pokemon-does-not-exist-xyz` displays a "not found" message — the backend returns RFC 9457 Problem Detail with `status: 404` and the frontend renders it gracefully.  
**Screenshot:** `test-results/pokemon-API-error-handling-unknown-pokemon-shows-not-found-message-chromium/test-finished-1.png`

---

## Summary

| Suite | Tests | Status |
|-------|-------|--------|
| US01 — Pokémon List | 3 | ✅ All pass |
| US02 — Pokémon Detail | 3 | ✅ All pass |
| Authentication | 4 | ✅ All pass |
| US03 — Sync to Local DB | 3 | ✅ All pass |
| US04 — Update / Delete | 4 | ✅ All pass |
| API Error Handling | 1 | ✅ All pass |
| **Total** | **18** | **✅ 18/18** |

Full HTML report (with timeline, traces, and embedded screenshots): `playwright-report/index.html`
