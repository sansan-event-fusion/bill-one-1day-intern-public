CREATE TABLE invoice_memo
(
    invoice_uuid UUID         PRIMARY KEY REFERENCES invoice (invoice_uuid),
    memo         VARCHAR(255) NOT NULL
);
