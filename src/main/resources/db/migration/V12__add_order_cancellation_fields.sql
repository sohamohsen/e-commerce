ALTER TABLE orders
    ADD COLUMN cancelled_by INT NULL,
    ADD COLUMN cancellation_reason VARCHAR(500) NULL,
    ADD COLUMN cancelled_at DATETIME NULL;
