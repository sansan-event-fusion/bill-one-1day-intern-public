CREATE TABLE domain_event
(
    domain_event_uuid uuid PRIMARY KEY,
    call_uuid         uuid         NOT NULL,
    domain_event_name varchar(255) NOT NULL,
    message           jsonb        NOT NULL,
    deployed          boolean      NOT NULL,
    created_at        timestamptz  NOT NULL
);
