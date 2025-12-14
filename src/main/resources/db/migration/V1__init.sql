-----------------------------------------------------------------------
-- 1. TÁBLÁK LÉTREHOZÁSA
-----------------------------------------------------------------------

CREATE TABLE app_user (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    user_lname VARCHAR(255) NOT NULL,
    user_fname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    password_hash VARCHAR(255) NOT NULL,

    -- JPA EnumType.STRING -> VARCHAR kell
    user_type VARCHAR(10) NOT NULL
);

CREATE TABLE stable (
    stable_id BIGSERIAL PRIMARY KEY,
    stable_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE item (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,

    -- ENUM helyett VARCHAR
    item_type VARCHAR(50) NOT NULL,
    item_category VARCHAR(20) NOT NULL
);

CREATE TABLE feed_sched (
    id BIGSERIAL PRIMARY KEY,

    -- ENUM helyett VARCHAR
    feed_time VARCHAR(50) NOT NULL,

    description TEXT
);

CREATE TABLE farrier_app (
    farrier_app_id BIGSERIAL PRIMARY KEY,
    farrier_name VARCHAR(255),
    farrier_phone VARCHAR(50),
    date DATE NOT NULL,
    shoes BOOLEAN NOT NULL
);

CREATE TABLE treatment (
    treatment_id BIGSERIAL PRIMARY KEY,
    treatment_name VARCHAR(255) NOT NULL,
    description TEXT,
    date DATE NOT NULL
);

CREATE TABLE shot (
    shot_id BIGSERIAL PRIMARY KEY,
    shot_name VARCHAR(255) NOT NULL,
    frequency_value INTEGER,
    frequency_unit VARCHAR(50),
    date DATE NOT NULL
);

CREATE TABLE horse (
    id BIGSERIAL PRIMARY KEY,
    horse_name VARCHAR(255) NOT NULL,
    dob DATE,

    -- ENUM helyett VARCHAR
    sex VARCHAR(2) NOT NULL,

    passport_num VARCHAR(255) UNIQUE,
    microchip_num VARCHAR(255) UNIQUE,
    additional TEXT,

    stable_id BIGINT NOT NULL REFERENCES stable (stable_id),
    user_id BIGINT NOT NULL REFERENCES app_user (user_id)
);

CREATE TABLE feed_sched_item (
    id BIGSERIAL PRIMARY KEY,
    feed_sched_id BIGINT NOT NULL REFERENCES feed_sched (id),
    item_id BIGINT NOT NULL REFERENCES item (id)
);

CREATE INDEX idx_feed_sched_item_feed_sched_id ON feed_sched_item (feed_sched_id);
CREATE INDEX idx_feed_sched_item_item_id ON feed_sched_item (item_id);

CREATE TABLE horse_feed_sched (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horse (id),
    feed_sched_id BIGINT NOT NULL REFERENCES feed_sched (id)
);

CREATE INDEX idx_horse_feed_sched_horse_id ON horse_feed_sched (horse_id);
CREATE INDEX idx_horse_feed_sched_feed_sched_id ON horse_feed_sched (feed_sched_id);

CREATE TABLE horse_farrier_app (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horse (id),
    farrier_app_id BIGINT NOT NULL REFERENCES farrier_app (farrier_app_id)
);

CREATE INDEX idx_horse_farrier_app_horse_id ON horse_farrier_app (horse_id);
CREATE INDEX idx_horse_farrier_app_farrier_app_id ON horse_farrier_app (farrier_app_id);

CREATE TABLE horse_treatment (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horse (id),
    treatment_id BIGINT NOT NULL REFERENCES treatment (treatment_id)
);

CREATE INDEX idx_horse_treatment_horse_id ON horse_treatment (horse_id);
CREATE INDEX idx_horse_treatment_treatment_id ON horse_treatment (treatment_id);

CREATE TABLE horse_shot (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horse (id),
    shot_id BIGINT NOT NULL REFERENCES shot (shot_id)
);

CREATE INDEX idx_horse_shot_horse_id ON horse_shot (horse_id);
CREATE INDEX idx_horse_shot_shot_id ON horse_shot (shot_id);

CREATE TABLE storage (
    storage_id BIGSERIAL PRIMARY KEY,
    amount_in_use DOUBLE PRECISION NOT NULL,
    amount_stored DOUBLE PRECISION NOT NULL,
    item_id BIGINT NOT NULL REFERENCES item (id)
);

CREATE INDEX idx_storage_item_id ON storage (item_id);

-----------------------------------------------------------------------
-- 3. ALAPÉRTELMEZETT ADMIN FELHASZNÁLÓ LÉTREHOZÁSA
-----------------------------------------------------------------------

INSERT INTO app_user (username, user_lname, user_fname, email, phone, password_hash, user_type)
VALUES (
    'admin',
    'Admin',
    'Felhasználó',
    'admin@example.com',
    NULL,
    '$2b$12$6dScWm9USZE7vVcZmfbC5ePGbMdk5G1bp/LiDMmYhCxZdqmvqw3j.', -- admin123
    'ADMIN'
)
ON CONFLICT (username) DO NOTHING;
