-- Insert test users
-- Password: password123 (BCrypt encoded)
INSERT INTO users (email, password, first_name, last_name, phone_number, email_verified, active, created_at) VALUES
('john.doe@example.com', '$2a$10$xJGVE8z/kKYXhI6y5PYqDuJHxD8bGLKlIvP0mKEHb4UO.oCK4H8Gq', 'John', 'Doe', '+1234567890', true, true, '2024-01-01 10:00:00'),
('jane.smith@example.com', '$2a$10$xJGVE8z/kKYXhI6y5PYqDuJHxD8bGLKlIvP0mKEHb4UO.oCK4H8Gq', 'Jane', 'Smith', '+1234567891', true, true, '2024-01-02 11:00:00'),
('bob.johnson@example.com', '$2a$10$xJGVE8z/kKYXhI6y5PYqDuJHxD8bGLKlIvP0mKEHb4UO.oCK4H8Gq', 'Bob', 'Johnson', '+1234567892', true, true, '2024-01-03 09:00:00'),
('alice.williams@example.com', '$2a$10$xJGVE8z/kKYXhI6y5PYqDuJHxD8bGLKlIvP0mKEHb4UO.oCK4H8Gq', 'Alice', 'Williams', '+1234567893', true, true, '2024-01-04 14:00:00'),
('charlie.brown@example.com', '$2a$10$xJGVE8z/kKYXhI6y5PYqDuJHxD8bGLKlIvP0mKEHb4UO.oCK4H8Gq', 'Charlie', 'Brown', '+1234567894', false, true, '2024-01-05 16:00:00');

-- Insert user roles
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_USER'),
(2, 'ROLE_USER'),
(3, 'ROLE_USER'),
(4, 'ROLE_USER'),
(5, 'ROLE_USER');

-- Insert accounts for John Doe
INSERT INTO accounts (account_number, type, balance, currency, active, created_at, user_id) VALUES
('100000000001', 'CHECKING', 5000.00, 'USD', true, '2024-01-01 10:30:00', 1),
('100000000002', 'SAVINGS', 15000.00, 'USD', true, '2024-01-01 11:00:00', 1),
('100000000003', 'INVESTMENT', 25000.00, 'USD', true, '2024-01-01 12:00:00', 1);

-- Insert accounts for Jane Smith
INSERT INTO accounts (account_number, type, balance, currency, active, created_at, user_id) VALUES
('200000000001', 'CHECKING', 3500.50, 'USD', true, '2024-01-02 11:30:00', 2),
('200000000002', 'SAVINGS', 8000.00, 'USD', true, '2024-01-02 12:00:00', 2);

-- Insert accounts for Bob Johnson
INSERT INTO accounts (account_number, type, balance, currency, active, created_at, user_id) VALUES
('300000000001', 'CHECKING', 1200.75, 'USD', true, '2024-01-03 09:30:00', 3),
('300000000002', 'SAVINGS', 20000.00, 'USD', true, '2024-01-03 10:00:00', 3);

-- Insert accounts for Alice Williams
INSERT INTO accounts (account_number, type, balance, currency, active, created_at, user_id) VALUES
('400000000001', 'CHECKING', 7500.00, 'USD', true, '2024-01-04 14:30:00', 4),
('400000000002', 'INVESTMENT', 50000.00, 'USD', true, '2024-01-04 15:00:00', 4);

-- Insert accounts for Charlie Brown
INSERT INTO accounts (account_number, type, balance, currency, active, created_at, user_id) VALUES
('500000000001', 'CHECKING', 250.00, 'USD', true, '2024-01-05 16:30:00', 5);

-- Insert deposit transactions
INSERT INTO transactions (transaction_id, type, amount, currency, status, description, created_at, completed_at, from_account_id, to_account_id) VALUES
('txn-deposit-001', 'DEPOSIT', 5000.00, 'USD', 'COMPLETED', 'Initial deposit', '2024-01-01 10:35:00', '2024-01-01 10:35:01', NULL, 1),
('txn-deposit-002', 'DEPOSIT', 15000.00, 'USD', 'COMPLETED', 'Salary deposit', '2024-01-01 11:05:00', '2024-01-01 11:05:01', NULL, 2),
('txn-deposit-003', 'DEPOSIT', 25000.00, 'USD', 'COMPLETED', 'Investment capital', '2024-01-01 12:05:00', '2024-01-01 12:05:01', NULL, 3),
('txn-deposit-004', 'DEPOSIT', 3500.50, 'USD', 'COMPLETED', 'Initial deposit', '2024-01-02 11:35:00', '2024-01-02 11:35:01', NULL, 4),
('txn-deposit-005', 'DEPOSIT', 8000.00, 'USD', 'COMPLETED', 'Savings', '2024-01-02 12:05:00', '2024-01-02 12:05:01', NULL, 5),
('txn-deposit-006', 'DEPOSIT', 1200.75, 'USD', 'COMPLETED', 'Paycheck', '2024-01-03 09:35:00', '2024-01-03 09:35:01', NULL, 6),
('txn-deposit-007', 'DEPOSIT', 20000.00, 'USD', 'COMPLETED', 'Bonus', '2024-01-03 10:05:00', '2024-01-03 10:05:01', NULL, 7),
('txn-deposit-008', 'DEPOSIT', 7500.00, 'USD', 'COMPLETED', 'Initial deposit', '2024-01-04 14:35:00', '2024-01-04 14:35:01', NULL, 8),
('txn-deposit-009', 'DEPOSIT', 50000.00, 'USD', 'COMPLETED', 'Investment', '2024-01-04 15:05:00', '2024-01-04 15:05:01', NULL, 9),
('txn-deposit-010', 'DEPOSIT', 250.00, 'USD', 'COMPLETED', 'Cash deposit', '2024-01-05 16:35:00', '2024-01-05 16:35:01', NULL, 10);

-- Insert transfer transactions
INSERT INTO transactions (transaction_id, type, amount, currency, status, description, created_at, completed_at, from_account_id, to_account_id) VALUES
('txn-transfer-001', 'TRANSFER', 500.00, 'USD', 'COMPLETED', 'Transfer to savings', '2024-01-06 10:00:00', '2024-01-06 10:00:01', 1, 2),
('txn-transfer-002', 'TRANSFER', 1000.00, 'USD', 'COMPLETED', 'Payment to Jane', '2024-01-07 11:00:00', '2024-01-07 11:00:01', 1, 4),
('txn-transfer-003', 'TRANSFER', 250.00, 'USD', 'COMPLETED', 'Payment to Bob', '2024-01-08 12:00:00', '2024-01-08 12:00:01', 4, 6),
('txn-transfer-004', 'TRANSFER', 750.00, 'USD', 'COMPLETED', 'Rent payment', '2024-01-09 13:00:00', '2024-01-09 13:00:01', 6, 8),
('txn-transfer-005', 'TRANSFER', 300.00, 'USD', 'COMPLETED', 'Utilities', '2024-01-10 14:00:00', '2024-01-10 14:00:01', 8, 1);

-- Insert withdrawal transactions
INSERT INTO transactions (transaction_id, type, amount, currency, status, description, created_at, completed_at, from_account_id, to_account_id) VALUES
('txn-withdrawal-001', 'WITHDRAWAL', 200.00, 'USD', 'COMPLETED', 'ATM withdrawal', '2024-01-11 10:00:00', '2024-01-11 10:00:01', 1, NULL),
('txn-withdrawal-002', 'WITHDRAWAL', 150.00, 'USD', 'COMPLETED', 'Cash withdrawal', '2024-01-12 11:00:00', '2024-01-12 11:00:01', 4, NULL),
('txn-withdrawal-003', 'WITHDRAWAL', 100.00, 'USD', 'COMPLETED', 'ATM withdrawal', '2024-01-13 12:00:00', '2024-01-13 12:00:01', 6, NULL),
('txn-withdrawal-004', 'WITHDRAWAL', 500.00, 'USD', 'COMPLETED', 'Cash withdrawal', '2024-01-14 13:00:00', '2024-01-14 13:00:01', 8, NULL);

-- Insert pending transaction
INSERT INTO transactions (transaction_id, type, amount, currency, status, description, created_at, completed_at, from_account_id, to_account_id) VALUES
('txn-pending-001', 'TRANSFER', 100.00, 'USD', 'PENDING', 'Pending transfer', '2024-01-15 10:00:00', NULL, 1, 4);

-- Insert failed transaction
INSERT INTO transactions (transaction_id, type, amount, currency, status, description, created_at, completed_at, from_account_id, to_account_id) VALUES
('txn-failed-001', 'TRANSFER', 50000.00, 'USD', 'FAILED', 'Insufficient funds', '2024-01-16 10:00:00', NULL, 10, 1);