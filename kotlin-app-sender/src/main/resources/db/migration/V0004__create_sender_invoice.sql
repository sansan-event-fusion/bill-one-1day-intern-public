CREATE TABLE sender_invoice (
    sender_invoice_uuid UUID PRIMARY KEY ,
    recipient_uuid UUID NOT NULL REFERENCES recipient (recipient_uuid),
    sender_uuid UUID NOT NULL,
    registered_at timestamptz NOT NULL,
    created_at timestamptz NOT NULL
);