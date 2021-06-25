CREATE TABLE recipient
(
    recipient_uuid UUID PRIMARY KEY,
    full_name      VARCHAR(50) NOT NULL,
    created_at     timestamptz NOT NULL
);
