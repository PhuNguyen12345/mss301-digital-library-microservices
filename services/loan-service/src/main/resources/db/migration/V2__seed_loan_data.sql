-- Seed data aligned with member-service V3 and catalog-service V2.
-- Cross-service identifiers intentionally have no database foreign keys.
--
-- Members used below:
--   e06c1a8e-cfbe-4290-95b8-d218dc60d4b2 - John Doe (READER, 14 days)
--   bc0e2270-b745-4b36-a192-d6ffb480c541 - Jane Smith (READER, 14 days)
--   a5840d21-f938-4e87-9bb3-5e7e923e20e8 - Bob Johnson (PREMIUM, 30 days)
--   d3b07384-d113-4ec6-a5cc-918d30e012cf - Alice Brown (READER, locked)
--   f47ac10b-58cc-4372-a567-0e02b2c3d479 - Sarah Connor (LIBRARIAN)
--
-- Catalog copy 2 (book 1) and copy 10 (book 7) are currently BORROWED.

-- Member Service owns member identifiers as VARCHAR UUID values. Align the
-- reservation reference created in V1 with the same cross-service ID contract.
ALTER TABLE reservations
    ALTER COLUMN member_id TYPE VARCHAR(255)
    USING member_id::VARCHAR;

INSERT INTO loans (
    loan_id,
    member_id,
    book_id,
    copy_id,
    book_type,
    status,
    borrowed_at,
    due_date,
    returned_at,
    renewal_count,
    max_renewals,
    idempotency_key,
    created_at,
    updated_at,
    version
) VALUES
    -- Active physical loan: John Doe, READER policy = 14 days.
    (1, 'e06c1a8e-cfbe-4290-95b8-d218dc60d4b2', 1, 2, 'PHYSICAL', 'BORROWED',
     '2026-07-15 09:00:00', '2026-07-29 09:00:00', NULL,
     0, 3, 'seed-borrow-001', '2026-07-15 09:00:00', '2026-07-15 09:00:00', 0),

    -- Overdue physical loan: Jane Smith, READER policy = 14 days.
    (2, 'bc0e2270-b745-4b36-a192-d6ffb480c541', 7, 10, 'PHYSICAL', 'OVERDUE',
     '2026-06-20 10:30:00', '2026-07-04 10:30:00', NULL,
     0, 3, 'seed-borrow-002', '2026-06-20 10:30:00', '2026-07-05 00:05:00', 0),

    -- Returned physical loan: John Doe, READER policy = 14 days.
    (3, 'e06c1a8e-cfbe-4290-95b8-d218dc60d4b2', 2, 3, 'PHYSICAL', 'RETURNED',
     '2026-06-10 08:15:00', '2026-06-24 08:15:00', '2026-06-22 16:20:00',
     0, 3, 'seed-borrow-003', '2026-06-10 08:15:00', '2026-06-22 16:20:00', 0),

    -- Renewed then returned late: Bob Johnson, PREMIUM policy = 30 days.
    (4, 'a5840d21-f938-4e87-9bb3-5e7e923e20e8', 5, 8, 'PHYSICAL', 'RETURNED',
     '2026-05-01 14:00:00', '2026-05-31 14:00:00', '2026-06-02 09:10:00',
     1, 3, 'seed-borrow-004', '2026-05-01 14:00:00', '2026-06-02 09:10:00', 0),

    -- Active digital loan: Bob Johnson, PREMIUM policy = 30 days.
    (5, 'a5840d21-f938-4e87-9bb3-5e7e923e20e8', 3, NULL, 'DIGITAL', 'BORROWED',
     '2026-07-12 13:45:00', '2026-08-11 13:45:00', NULL,
     0, 3, 'seed-borrow-005', '2026-07-12 13:45:00', '2026-07-12 13:45:00', 0),

    -- Historical loan for Alice; it was completed before her account was locked.
    (6, 'd3b07384-d113-4ec6-a5cc-918d30e012cf', 6, NULL, 'DIGITAL', 'RETURNED',
     '2026-04-10 11:00:00', '2026-04-24 11:00:00', '2026-04-20 18:00:00',
     0, 3, 'seed-borrow-006', '2026-04-10 11:00:00', '2026-04-20 18:00:00', 0);

INSERT INTO loan_status_history (
    id,
    loan_id,
    from_status,
    to_status,
    changed_by,
    reason,
    changed_at
) VALUES
    (1, 1, NULL, 'BORROWED', 'SYSTEM', 'Loan created', '2026-07-15 09:00:00'),
    (2, 2, NULL, 'BORROWED', 'SYSTEM', 'Loan created', '2026-06-20 10:30:00'),
    (3, 2, 'BORROWED', 'OVERDUE', 'SYSTEM', 'Due date exceeded', '2026-07-05 00:05:00'),
    (4, 3, NULL, 'BORROWED', 'SYSTEM', 'Loan created', '2026-06-10 08:15:00'),
    (5, 3, 'BORROWED', 'RETURNED', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Book returned', '2026-06-22 16:20:00'),
    (6, 4, NULL, 'BORROWED', 'SYSTEM', 'Loan created', '2026-05-01 14:00:00'),
    (7, 4, 'BORROWED', 'BORROWED', 'a5840d21-f938-4e87-9bb3-5e7e923e20e8', 'Loan renewed', '2026-05-20 09:00:00'),
    (8, 4, 'BORROWED', 'RETURNED', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Book returned late', '2026-06-02 09:10:00'),
    (9, 5, NULL, 'BORROWED', 'SYSTEM', 'Digital loan created', '2026-07-12 13:45:00'),
    (10, 6, NULL, 'BORROWED', 'SYSTEM', 'Digital loan created', '2026-04-10 11:00:00'),
    (11, 6, 'BORROWED', 'RETURNED', 'd3b07384-d113-4ec6-a5cc-918d30e012cf', 'Digital loan completed', '2026-04-20 18:00:00');

INSERT INTO saga_outbox (
    event_id,
    loan_id,
    event_type,
    payload,
    status,
    retry_count,
    created_at,
    processed_at
) VALUES
    (1, 1, 'BookBorrowedEvent', '{"loanId":1,"memberId":"e06c1a8e-cfbe-4290-95b8-d218dc60d4b2","bookId":1,"copyId":2}',
     'PROCESSED', 0, '2026-07-15 09:00:00', '2026-07-15 09:00:05'),
    (2, 2, 'LoanOverdueEvent', '{"loanId":2,"memberId":"bc0e2270-b745-4b36-a192-d6ffb480c541","bookId":7,"copyId":10}',
     'PROCESSED', 0, '2026-07-05 00:05:00', '2026-07-05 00:05:05'),
    (3, 3, 'BookReturnedEvent', '{"loanId":3,"memberId":"e06c1a8e-cfbe-4290-95b8-d218dc60d4b2","bookId":2,"copyId":3}',
     'PROCESSED', 0, '2026-06-22 16:20:00', '2026-06-22 16:20:04'),
    (4, 4, 'LoanRenewedEvent', '{"loanId":4,"memberId":"a5840d21-f938-4e87-9bb3-5e7e923e20e8","renewalCount":1}',
     'PROCESSED', 0, '2026-05-20 09:00:00', '2026-05-20 09:00:03'),
    (5, 4, 'BookReturnedLateEvent', '{"loanId":4,"memberId":"a5840d21-f938-4e87-9bb3-5e7e923e20e8","bookId":5,"copyId":8}',
     'PROCESSED', 0, '2026-06-02 09:10:00', '2026-06-02 09:10:05'),
    (6, 5, 'BookBorrowedEvent', '{"loanId":5,"memberId":"a5840d21-f938-4e87-9bb3-5e7e923e20e8","bookId":3,"bookType":"DIGITAL"}',
     'PROCESSED', 0, '2026-07-12 13:45:00', '2026-07-12 13:45:05');

INSERT INTO saga_log (
    saga_id,
    loan_id,
    saga_type,
    current_step,
    overall_status,
    compensations,
    started_at,
    finished_at
) VALUES
    (1, 1, 'BORROW_BOOK', 'LOAN_CREATED', 'COMPLETED', 0, '2026-07-15 08:59:58', '2026-07-15 09:00:01'),
    (2, 2, 'BORROW_BOOK', 'LOAN_CREATED', 'COMPLETED', 0, '2026-06-20 10:29:58', '2026-06-20 10:30:01'),
    (3, 3, 'BORROW_BOOK', 'LOAN_CREATED', 'COMPLETED', 0, '2026-06-10 08:14:58', '2026-06-10 08:15:01'),
    (4, 3, 'RETURN_BOOK', 'COPY_RELEASED', 'COMPLETED', 0, '2026-06-22 16:19:58', '2026-06-22 16:20:01'),
    (5, 4, 'BORROW_BOOK', 'LOAN_CREATED', 'COMPLETED', 0, '2026-05-01 13:59:58', '2026-05-01 14:00:01'),
    (6, 4, 'RENEW_BOOK', 'DUE_DATE_UPDATED', 'COMPLETED', 0, '2026-05-20 08:59:58', '2026-05-20 09:00:01'),
    (7, 4, 'RETURN_BOOK', 'COPY_RELEASED', 'COMPLETED', 0, '2026-06-02 09:09:58', '2026-06-02 09:10:01'),
    (8, 5, 'BORROW_BOOK', 'DIGITAL_ACCESS_GRANTED', 'COMPLETED', 0, '2026-07-12 13:44:58', '2026-07-12 13:45:01'),
    (9, 6, 'RETURN_BOOK', 'DIGITAL_ACCESS_CLOSED', 'COMPLETED', 0, '2026-04-20 17:59:58', '2026-04-20 18:00:01');

INSERT INTO reservations (
    reservation_id,
    member_id,
    book_id,
    status,
    queue_position,
    reserved_at,
    notified_at,
    expires_at
) VALUES
    (1, 'a5840d21-f938-4e87-9bb3-5e7e923e20e8', 7, 'WAITING', 1, '2026-07-16 08:00:00', NULL, NULL),
    (2, 'e06c1a8e-cfbe-4290-95b8-d218dc60d4b2', 10, 'NOTIFIED', 1, '2026-07-10 10:00:00', '2026-07-17 09:00:00', '2026-07-19 09:00:00'),
    (3, 'd3b07384-d113-4ec6-a5cc-918d30e012cf', 3, 'COMPLETED', 1, '2026-06-01 14:00:00', '2026-06-05 08:30:00', '2026-06-07 08:30:00');

ALTER TABLE loans ALTER COLUMN loan_id RESTART WITH 7;
ALTER TABLE loan_status_history ALTER COLUMN id RESTART WITH 12;
ALTER TABLE saga_outbox ALTER COLUMN event_id RESTART WITH 7;
ALTER TABLE saga_log ALTER COLUMN saga_id RESTART WITH 10;
ALTER TABLE reservations ALTER COLUMN reservation_id RESTART WITH 4;
