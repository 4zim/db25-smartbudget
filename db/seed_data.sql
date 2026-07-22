-- ============================================================
-- TICKET-F005 (Day 1, Sprint 0) — Insert Seed Data
-- TICKET-F004 (Day 1, Sprint 0) — Test Constraints
-- ============================================================
-- Seed data populates your tables with realistic test data.
-- Insert order matters! Parents first, then children:
--   categories → users → transactions → savings_goals
-- ============================================================


-- ============================================================
-- TODO TICKET-F005: Insert 5 categories
-- ============================================================
-- WHAT: Seed data is pre-populated test data that makes development easier.
--       Without it, every time you reset the database you'd start with empty tables.
--
-- HOW:  Write 5 INSERT INTO categories (name, type) VALUES (...) statements.
--       Include at least 2 INCOME categories (e.g., Salary, Freelance)
--       and at least 3 EXPENSE categories (e.g., Food, Transport, Utilities).
--       You do NOT need to specify category_id — SERIAL auto-assigns it.
--
-- WHY:  The React frontend needs categories for its dropdown.
--       The API endpoint GET /api/categories returns this data.
--
-- OBSERVE: After inserting, run SELECT * FROM categories;
--          You should see 5 rows with auto-assigned category_id values 1-5.


-- ============================================================
-- TODO TICKET-F005: Insert 10 users
-- ============================================================
-- WHAT: Each user has a unique name and email. The email UNIQUE constraint
--       means no two users can share the same email address.
--
-- HOW:  Write 10 INSERT INTO users (name, email) VALUES (...) statements.
--       Use realistic names like 'Alice Smith', 'Bob Jones', etc.
--       Use email format like 'alice@bank.com'.
--       Do NOT specify user_id or created_at — they auto-populate.
--
-- WHY:  10 users gives enough variety to test multi-user features.
--
-- OBSERVE: Run SELECT * FROM users; — you should see 10 rows.
--          Try inserting a duplicate email — it should FAIL with a unique violation.


-- ============================================================
-- TODO TICKET-F005: Insert 50+ transactions
-- ============================================================
-- WHAT: Transactions are the core data. Each references a user_id and category_id.
--       These IDs must match existing rows in the users and categories tables
--       (this is enforced by the FOREIGN KEY constraints you added).
--
-- HOW:  Write 50+ INSERT INTO transactions (user_id, category_id, amount, txn_date, description, type) VALUES (...) statements.
--       Spread them across multiple users and categories.
--       Use different dates spanning 2-3 months (for monthly summary testing).
--       Mix INCOME and EXPENSE types.
--       Make amounts realistic: salaries around 3000-5000, food around 30-100, etc.
--
-- WHY:  50+ records provide enough data to test filtering, sorting, aggregation,
--       and make charts look meaningful.
--
-- OBSERVE: Run SELECT COUNT(*) FROM transactions; — should return 50+.
--          Run the JOIN query from queries.sql to see full details.


-- ============================================================
-- TODO TICKET-F005: Insert 3+ savings goals
-- ============================================================
-- WHAT: Savings goals track progress toward a financial target.
--       current_amount starts at 0 or a partial amount.
--
-- HOW:  Write 3+ INSERT INTO savings_goals (user_id, name, target_amount, current_amount, deadline) VALUES (...).
--       Examples: 'Holiday Fund' target 2000, current 500, deadline 2026-12-01
--       Assign goals to different users.
--
-- WHY:  The Savings Goals page needs this data to display progress bars.
--
-- OBSERVE: Run SELECT * FROM savings_goals; — check the progress percentages
--          (current_amount / target_amount * 100).


-- ============================================================
-- TODO TICKET-F004: Test constraint violations
-- ============================================================
-- WHAT: Constraints are rules the database enforces. If violated, the INSERT fails.
--       This is a safety net — even if application code has bugs, the database
--       prevents invalid data from being stored.
--
-- HOW:  Try these INSERTs (each should FAIL):
--         1. A transaction with amount = -10 (violates CHECK amount > 0)
--         2. A transaction with user_id = 999 (violates FOREIGN KEY — user doesn't exist)
--         3. A user with a duplicate email (violates UNIQUE constraint)
--         4. A category with type = 'INVALID' (violates CHECK type IN ('INCOME','EXPENSE'))
--
-- WHY:  Proving constraints work builds confidence in your schema design.
--       If constraints don't catch bad data, fix them before moving on.
--
-- OBSERVE: Each of the 4 INSERTs above should produce an ERROR message.
--          Read the error message — it tells you which constraint was violated.
