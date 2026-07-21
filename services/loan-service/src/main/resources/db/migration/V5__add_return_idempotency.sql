ALTER TABLE loans
    ADD COLUMN return_idempotency_key VARCHAR(255);

CREATE UNIQUE INDEX uk_loans_return_idempotency_key
    ON loans (return_idempotency_key)
    WHERE return_idempotency_key IS NOT NULL;
