BEGIN;

INSERT INTO recipient (recipient_uuid, full_name, created_at)
VALUES
       ('015bdd8a-9aee-4545-8e4b-95b48a482559', '受領アカウント1', now()),
       ('c60fb756-31f5-4e38-9c50-f59f5c8091a8', '受領アカウント2', now());

INSERT INTO sender (sender_uuid, full_name, created_at)
VALUES
('a309eff9-aea5-4eb1-b74a-17cb9f2bb019', '送付アカウント', now()),
('553707cc-72d5-4634-b660-6f541102418a', '送付アカウント2', now());

INSERT INTO sender_invoice (sender_invoice_uuid, recipient_uuid, sender_uuid, registered_at, created_at)
VALUES
('a0a678d9-9fdd-4acb-9d89-e2345c69a016', '015bdd8a-9aee-4545-8e4b-95b48a482559', 'a309eff9-aea5-4eb1-b74a-17cb9f2bb019', now(), now()),
('8c9f66c5-0308-4f12-a444-dcf4836d50c9', 'c60fb756-31f5-4e38-9c50-f59f5c8091a8', 'a309eff9-aea5-4eb1-b74a-17cb9f2bb019', now(), now());

COMMIT;
