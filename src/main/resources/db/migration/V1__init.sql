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
    user_type VARCHAR(10) NOT NULL
);

CREATE TABLE stable (
    stable_id BIGSERIAL PRIMARY KEY,
    stable_name VARCHAR(255) NOT NULL UNIQUE,
    straw_usage_kg NUMERIC
);

CREATE TABLE item (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    item_type VARCHAR(50) NOT NULL,
    item_category VARCHAR(20) NOT NULL,
    feed_unit_amount NUMERIC
);

CREATE TABLE feed_sched (
    id BIGSERIAL PRIMARY KEY,
    feed_morning BOOLEAN NOT NULL DEFAULT FALSE,
    feed_noon BOOLEAN NOT NULL DEFAULT FALSE,
    feed_evening BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT
);

CREATE TABLE farrier_app (
    farrier_app_id BIGSERIAL PRIMARY KEY,
    farrier_name VARCHAR(255),
    farrier_phone VARCHAR(50),
    frequency_value INTEGER,
    frequency_unit VARCHAR(50),
    date DATE NOT NULL,
    shoes BOOLEAN NOT NULL
);

CREATE TABLE treatment (
    treatment_id BIGSERIAL PRIMARY KEY,
    treatment_name VARCHAR(255) NOT NULL,
    description TEXT,
    frequency_value INTEGER,
    frequency_unit VARCHAR(50),
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
    sex VARCHAR(2) NOT NULL,
    passport_num VARCHAR(255) UNIQUE,
    microchip_num VARCHAR(255) UNIQUE,
    additional TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_pending BOOLEAN NOT NULL DEFAULT FALSE,
    stable_id BIGINT REFERENCES stable (stable_id),
    user_id BIGINT NOT NULL REFERENCES app_user (user_id)
);

CREATE TABLE calendar_event (
    event_id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horse (id),
    event_type VARCHAR(30) NOT NULL,
    event_date DATE NOT NULL,
    related_entity_id BIGINT,
    description TEXT
);

CREATE INDEX idx_calendar_event_horse_id ON calendar_event (horse_id);
CREATE INDEX idx_calendar_event_event_date ON calendar_event (event_date);
CREATE INDEX idx_calendar_event_type_related ON calendar_event (event_type, related_entity_id);

CREATE TABLE feed_sched_item (
    id BIGSERIAL PRIMARY KEY,
    feed_sched_id BIGINT NOT NULL REFERENCES feed_sched (id),
    item_id BIGINT NOT NULL REFERENCES item (id),
    amount DOUBLE PRECISION NOT NULL
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
    farrier_app_id BIGINT NOT NULL REFERENCES farrier_app (farrier_app_id),
    shoe_count SMALLINT NOT NULL DEFAULT 0,
    note TEXT
);

CREATE INDEX idx_horse_farrier_app_horse_id ON horse_farrier_app (horse_id);
CREATE INDEX idx_horse_farrier_app_farrier_app_id ON horse_farrier_app (farrier_app_id);

CREATE TABLE stable_item (
    id BIGSERIAL PRIMARY KEY,
    stable_id BIGINT NOT NULL REFERENCES stable (stable_id),
    item_id BIGINT NOT NULL REFERENCES item (id),
    usage_kg NUMERIC NOT NULL
);

CREATE INDEX idx_stable_item_stable_id ON stable_item (stable_id);
CREATE INDEX idx_stable_item_item_id ON stable_item (item_id);

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
    last_reduced_date DATE,
    item_id BIGINT NOT NULL REFERENCES item (id)
);

CREATE INDEX idx_storage_item_id ON storage (item_id);

CREATE TABLE feed_sched_change_request (
    id BIGSERIAL PRIMARY KEY,
    feed_sched_id BIGINT NOT NULL REFERENCES feed_sched (id),
    requested_by_user_id BIGINT NOT NULL REFERENCES app_user (user_id),
    requested_at TIMESTAMP NOT NULL,
    requested_morning BOOLEAN,
    requested_noon BOOLEAN,
    requested_evening BOOLEAN,
    requested_description TEXT,
    requested_horse_ids TEXT,
    requested_item_ids TEXT,
    requested_item_amounts TEXT
);

CREATE TABLE notification_log (
    notification_id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    horse_id BIGINT,
    days_before INTEGER NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_notification_unique UNIQUE (event_type, entity_id, horse_id, days_before)
);

CREATE INDEX idx_feed_sched_change_request_feed_sched ON feed_sched_change_request (feed_sched_id);
CREATE INDEX idx_feed_sched_change_request_requested_at ON feed_sched_change_request (requested_at);

CREATE TABLE app_setting (
    setting_key VARCHAR(100) PRIMARY KEY,
    bool_value BOOLEAN NOT NULL
);

-----------------------------------------------------------------------
-- 3. ALAPÉRTELMEZETT ADMIN FELHASZNÁLÓ LÉTREHOZÁSA
-----------------------------------------------------------------------

INSERT INTO app_setting (setting_key, bool_value) VALUES
    ('EMPLOYEE_VIEW_SHOTS', FALSE),
    ('EMPLOYEE_VIEW_TREATMENTS', FALSE),
    ('EMPLOYEE_VIEW_FARRIER_APPS', FALSE)
ON CONFLICT (setting_key) DO NOTHING;

INSERT INTO app_user (username, user_lname, user_fname, email, phone, password_hash, user_type)
VALUES (
    'admin',
    'Admin',
    'Felhasználó',
    'admin@example.com',
    NULL,
    '$2b$12$6dScWm9USZE7vVcZmfbC5ePGbMdk5G1bp/LiDMmYhCxZdqmvqw3j.',
    'ADMIN'
)
ON CONFLICT (username) DO NOTHING;
