-- 1. Check all registered users
SELECT * FROM users;

-- 2. Check all bank accounts and their balances
SELECT * FROM accounts;

-- 3. Check the roles assigned to each user
SELECT * FROM user_roles;

-- 4. Check the full transaction history
SELECT * FROM transactions;



SELECT
    u.first_name,
    u.last_name,
    u.email,
    a.account_number,
    a.type AS account_type,
    a.balance,
    a.currency
FROM users u
JOIN accounts a ON u.id = a.user_id
ORDER BY u.last_name;


SELECT
    t.transaction_id,
    t.type AS txn_type,
    t.amount,
    f.account_number AS from_account,
    r.account_number AS to_account,
    t.status,
    t.created_at
FROM transactions t
LEFT JOIN accounts f ON t.from_account_id = f.id
LEFT JOIN accounts r ON t.to_account_id = r.id
ORDER BY t.created_at DESC;

