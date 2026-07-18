-- Seed member profiles
INSERT INTO member_profiles (
    id, email, first_name, last_name, phone, member_type, member_code, 
    borrowing_limit, loan_period_days, outstanding_balance, avatar_key, 
    status, created_at, updated_at
) VALUES
('e06c1a8e-cfbe-4290-95b8-d218dc60d4b2', 'reader.john@example.com', 'John', 'Doe', '0901112222', 'READER', 'LIB-JOHN0001', 5, 14, 0.00, NULL, 'UNLOCKED', NOW(), NOW()),
('bc0e2270-b745-4b36-a192-d6ffb480c541', 'reader.jane@example.com', 'Jane', 'Smith', '0902223333', 'READER', 'LIB-JANE0002', 5, 14, 0.00, NULL, 'UNLOCKED', NOW(), NOW()),
('a5840d21-f938-4e87-9bb3-5e7e923e20e8', 'premium.bob@example.com', 'Bob', 'Johnson', '0903334444', 'PREMIUM', 'LIB-BOB00003', 10, 30, 0.00, NULL, 'UNLOCKED', NOW(), NOW()),
('d3b07384-d113-4ec6-a5cc-918d30e012cf', 'locked.alice@example.com', 'Alice', 'Brown', '0904445555', 'READER', 'LIB-ALIC0004', 5, 14, 0.00, NULL, 'LOCKED', NOW(), NOW()),
('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'staff.sarah@example.com', 'Sarah', 'Connor', '0905556666', 'LIBRARIAN', 'LIB-SARA0005', 15, 60, 0.00, NULL, 'UNLOCKED', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
