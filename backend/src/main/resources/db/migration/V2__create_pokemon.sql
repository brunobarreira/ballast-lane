CREATE TABLE IF NOT EXISTS pokemon (
    id          UUID PRIMARY KEY,
    pokeapi_id  INTEGER      NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    local_name  VARCHAR(100),
    category    VARCHAR(100),
    mass_hg     INTEGER,
    sprite_url  TEXT,
    region      VARCHAR(100),
    tags        JSONB,
    skills      JSONB,
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
