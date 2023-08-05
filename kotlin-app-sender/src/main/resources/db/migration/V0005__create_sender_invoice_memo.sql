CREATE TABLE sender_invoice_memo (
    sender_invoice_uuid UUID PRIMARY KEY REFERENCES sender_invoice (sender_invoice_uuid),
    memo VARCHAR(255)
);