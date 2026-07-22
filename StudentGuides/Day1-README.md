# Day 1 -- SQL Foundations (Sprint 0)

> TICKET-F001 through TICKET-F010

---

## Overview

Today you design and build the **database layer** for SmartBudget. You will:

1. Draw an ER (Entity-Relationship) diagram
2. Write CREATE TABLE statements with constraints
3. Insert seed data
4. Write 5 SQL queries (JOINs, aggregations, window functions, CTEs, views)

By the end of Day 1, you will have a fully populated PostgreSQL database with 4 tables and be able to answer business questions using SQL.

---

## Setup

```bash
# Start PostgreSQL and connect
psql -U postgres

# Create the database and user
CREATE DATABASE smartbudget;
CREATE USER sb_user WITH PASSWORD 'sb_pass';
GRANT ALL PRIVILEGES ON DATABASE smartbudget TO sb_user;

# Connect to the smartbudget database
\c smartbudget
```

---

## Tickets

### TICKET-F001: Project Setup and Introduction
**File:** N/A (no code)

**Description:** Understand the SmartBudget project requirements, review the ER diagram, and set up your PostgreSQL environment.

**What**
- A working PostgreSQL environment with the `smartbudget` database and `sb_user` user.
- A clear picture of what SmartBudget is and what four tables it will need.

**Why**
- Every other ticket today (and the JDBC work on Day 4) assumes you can connect as `sb_user` to `smartbudget`.
- Skip this and nothing downstream runs.

**Observe**
- `psql -U sb_user -d smartbudget -h localhost` drops you at the `smartbudget=>` prompt.
- Inside it, `\dt` reports "Did not find any relations." — correct, you haven't built any tables yet.

**Instructions:**
1. Read the Day0-README.md to understand the full project
2. Install PostgreSQL if not already installed
3. Create the `smartbudget` database and `sb_user` user (see Setup above)
4. Review the 4 entities: Users, Categories, Transactions, SavingsGoals

**Acceptance Criteria:**
- [ ] PostgreSQL is running on localhost:5432
- [ ] Database `smartbudget` exists
- [ ] User `sb_user` can connect to the database
- [ ] You can explain what each of the 4 tables will store

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

This ticket is about environment setup. Make sure PostgreSQL is actually running (`pg_isready` or `brew services list`) before you try to connect. Then `psql -U postgres` opens a prompt where you run the `CREATE DATABASE` and `CREATE USER` commands from the Setup section above.

</details>

<details>
<summary><b>Hint 2 — Commands</b></summary>

```bash
# Verify postgres is up
pg_isready                       # should print "accepting connections"

# Open the postgres super-user shell
psql -U postgres
```

Inside the psql prompt:

```sql
CREATE DATABASE smartbudget;
CREATE USER sb_user WITH PASSWORD 'sb_pass';
GRANT ALL PRIVILEGES ON DATABASE smartbudget TO sb_user;
\q
```

Then verify you can log in as the new user:

```bash
psql -U sb_user -d smartbudget
```

</details>

<details>
<summary><b>Hint 3 — Full walkthrough</b></summary>

```bash
# 1. Install PostgreSQL (skip if already installed)
brew install postgresql@15           # macOS
sudo apt install postgresql          # Ubuntu/Debian

# 2. Start the service
brew services start postgresql@15    # macOS
sudo service postgresql start        # Linux

# 3. Confirm it accepts connections on 5432
pg_isready -h localhost -p 5432
# -> localhost:5432 - accepting connections

# 4. Open psql as the super-user
psql -U postgres
```

In the psql prompt:

```sql
-- Create the project database
CREATE DATABASE smartbudget;

-- Create an application user with a password
CREATE USER sb_user WITH PASSWORD 'sb_pass';

-- Give that user permission to use the database
GRANT ALL PRIVILEGES ON DATABASE smartbudget TO sb_user;

-- Confirm both exist
\l                       -- list databases (smartbudget should appear)
\du                      -- list users     (sb_user should appear)
\q                       -- quit
```

Then verify the new user can connect:

```bash
psql -U sb_user -d smartbudget -h localhost
# you should land at the prompt: smartbudget=>
```

If that prompt appears, every item on the Acceptance Criteria checklist is satisfied. You can also briefly describe each table out loud as a check:
- `users` — one row per person
- `categories` — Income/Expense bucket labels
- `transactions` — money movements linked to a user + category
- `savings_goals` — savings targets linked to a user

</details>

---

### TICKET-F002: ER Diagram
**File:** N/A (draw on paper or use a tool like draw.io)

**Description:** Design the Entity-Relationship diagram showing all 4 tables and their relationships.

**What**
- A picture (boxes + lines) showing the four entities with columns, primary keys, foreign keys, and cardinalities.
- Exported as `docs/er-diagram.png` and committed to the repo.

**Why**
- The diagram is the contract between the Day 1 SQL and the Day 5 JPA `@Entity` classes.
- If the picture is wrong, the queries are wrong, and the React UI shows garbage.

**Observe**
- You can trace each of the three JOIN queries from the diagram alone, without looking at code.
- Cardinality labels (`1` on the parent side, `*` or `N` on the child side) appear on every relationship line.

**Instructions:**
1. Identify the 4 entities: users, categories, transactions, savings_goals
2. For each entity, list all columns with data types
3. Mark primary keys (PK) and foreign keys (FK)
4. Draw the relationships:
   - A user has MANY transactions (1:N)
   - A category has MANY transactions (1:N)
   - A user has MANY savings goals (1:N)
5. Label cardinality on each relationship line

**Acceptance Criteria:**
- [ ] All 4 tables are shown with all columns
- [ ] Primary keys are marked on every table
- [ ] Foreign keys connect child tables to parent tables
- [ ] Cardinality (1:N) is shown on each relationship
- [ ] Data types are listed for each column

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

There are 4 boxes and 3 lines. Boxes = entities (`users`, `categories`, `transactions`, `savings_goals`). Lines = wherever a child table has a `*_id` column that points to a parent. Each line goes from the **parent** (the "1" side, the table whose PK is being referenced) to the **child** (the "N" side, the table that holds the FK).

</details>

<details>
<summary><b>Hint 2 — Box contents and relationships</b></summary>

For each entity, draw a box with the columns inside. Mark PK with **(PK)** and FK with **(FK)**.

```
users                       categories
─────────                   ──────────
user_id   (PK) SERIAL       category_id (PK) SERIAL
name           VARCHAR(100) name           VARCHAR(50)
email          VARCHAR(150) type           VARCHAR(10)  -- 'INCOME'|'EXPENSE'
created_at     TIMESTAMP
```

Then add the two child tables and draw lines:

```
users 1 ──────< N transactions    (transactions.user_id     → users.user_id)
categories 1 ──< N transactions   (transactions.category_id → categories.category_id)
users 1 ──────< N savings_goals   (savings_goals.user_id    → users.user_id)
```

</details>

<details>
<summary><b>Hint 3 — Full ER diagram (text form)</b></summary>

```
┌─────────────────────────────┐         ┌─────────────────────────────┐
│ users                       │         │ categories                  │
├─────────────────────────────┤         ├─────────────────────────────┤
│ user_id    SERIAL  (PK)     │         │ category_id SERIAL (PK)     │
│ name       VARCHAR(100)     │         │ name        VARCHAR(50)     │
│ email      VARCHAR(150) UQ  │         │ type        VARCHAR(10)     │
│ created_at TIMESTAMP        │         │             CHECK IN        │
└──────┬──────────────────────┘         │             ('INCOME',      │
       │ 1                              │              'EXPENSE')     │
       │                                └────┬────────────────────────┘
       │                                     │ 1
       │                                     │
       │ N        ┌──────────────────────────┴─┐
       └──────────┤ transactions               │
                  ├────────────────────────────┤
                  │ txn_id      SERIAL  (PK)   │
                  │ user_id     INT (FK→users) │
                  │ category_id INT (FK→cats)  │
                  │ amount      NUMERIC(12,2)  │
                  │ txn_date    DATE           │
                  │ description VARCHAR(255)   │
                  │ type        VARCHAR(10)    │
                  └────────────────────────────┘
       │ 1
       │
       │ N
┌──────┴──────────────────────┐
│ savings_goals               │
├─────────────────────────────┤
│ goal_id        SERIAL (PK)  │
│ user_id        INT (FK)     │
│ goal_name      VARCHAR(100) │
│ target_amount  NUMERIC(12,2)│
│ current_amount NUMERIC(12,2)│
│ deadline       DATE         │
└─────────────────────────────┘
```

Cardinalities:
- `users (1) ──< transactions (N)`   — one user, many transactions
- `categories (1) ──< transactions (N)` — one category, many transactions
- `users (1) ──< savings_goals (N)`  — one user, many goals

Self-check: can you write each of the 3 JOIN queries from this diagram alone (without looking at code)? If yes, the diagram is right.

Export your draw.io diagram as `docs/er-diagram.png` and commit it to the repo.

</details>

---

### TICKET-F003: Create Table Scripts
**File:** `db/create_tables.sql`

**Description:** Write CREATE TABLE statements for all 4 tables in the correct order.

**What**
- Four empty tables — `users`, `categories`, `transactions`, `savings_goals` — created in dependency order.
- Plain DDL only; constraints (FK, CHECK, NOT NULL, UNIQUE, DEFAULT) land in F004.

**Why**
- This is the skeleton your seed data and queries land on. Wrong column types here force a rewrite later.
- `NUMERIC(12,2)` for money is non-negotiable — `FLOAT` quietly breaks finance code.

**Observe**
- `\dt` in psql lists all four tables.
- `\d users` shows the `user_id` column with a `nextval(...)` default — proof that `SERIAL` worked.

**Instructions:**
1. Open `db/create_tables.sql` -- follow the TODO comments
2. Create tables in this order (parent tables first):
   - `users` -- user_id (SERIAL PK), name, email (UNIQUE), created_at
   - `categories` -- category_id (SERIAL PK), name, type
   - `transactions` -- txn_id (SERIAL PK), user_id (FK), category_id (FK), amount, txn_date, description, type
   - `savings_goals` -- goal_id (SERIAL PK), user_id (FK), goal_name, target_amount, current_amount, deadline
3. Use appropriate data types: SERIAL, VARCHAR, NUMERIC(12,2), DATE, TIMESTAMP

**Acceptance Criteria:**
- [ ] All 4 CREATE TABLE statements execute without errors
- [ ] Tables are created in the correct order (parents before children)
- [ ] `\dt` in psql shows all 4 tables
- [ ] SERIAL columns auto-increment when you insert data
- [ ] VARCHAR lengths are reasonable (100 for names, 150 for emails)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Open `db/create_tables.sql` and follow the TODO comments. Parent tables (`users`, `categories`) first — child tables (`transactions`, `savings_goals`) reference them so they must already exist. `SERIAL` gives you an auto-incrementing primary key without writing your own sequence. Don't worry about constraints yet — F004 layers those on top.

</details>

<details>
<summary><b>Hint 2 — Two of the four tables</b></summary>

```sql
CREATE TABLE users (
    user_id    SERIAL PRIMARY KEY,
    name       VARCHAR(100),
    email      VARCHAR(150),
    created_at TIMESTAMP
);

CREATE TABLE categories (
    category_id SERIAL PRIMARY KEY,
    name        VARCHAR(50),
    type        VARCHAR(10)
);
```

Now you do `transactions` and `savings_goals` — both reference `users` (and `transactions` also references `categories`), so use the same pattern but with FK columns that are `INT` (matching `user_id SERIAL`).

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```sql
-- Run order: users → categories → transactions → savings_goals

CREATE TABLE users (
    user_id    SERIAL PRIMARY KEY,
    name       VARCHAR(100),
    email      VARCHAR(150),
    created_at TIMESTAMP
);

CREATE TABLE categories (
    category_id SERIAL PRIMARY KEY,
    name        VARCHAR(50),
    type        VARCHAR(10)
);

CREATE TABLE transactions (
    txn_id      SERIAL PRIMARY KEY,
    user_id     INT,
    category_id INT,
    amount      NUMERIC(12,2),
    txn_date    DATE,
    description VARCHAR(255),
    type        VARCHAR(10)
);

CREATE TABLE savings_goals (
    goal_id        SERIAL PRIMARY KEY,
    user_id        INT,
    goal_name      VARCHAR(100),
    target_amount  NUMERIC(12,2),
    current_amount NUMERIC(12,2),
    deadline       DATE
);
```

Verify in psql:

```
smartbudget=> \dt
              List of relations
 Schema |     Name      | Type  |  Owner
--------+---------------+-------+---------
 public | users         | table | sb_user
 public | categories    | table | sb_user
 public | transactions  | table | sb_user
 public | savings_goals | table | sb_user
```

Note: this only satisfies F003. The FK + CHECK + NOT NULL + UNIQUE constraints come next in F004. We use `NUMERIC(12,2)` (not `FLOAT`) for money — floating-point rounding errors quietly break finance code (`0.1 + 0.2 = 0.30000000000000004`).

</details>

---

### TICKET-F004: Add Constraints
**File:** `db/create_tables.sql`

**Description:** Add NOT NULL, UNIQUE, CHECK, FOREIGN KEY, and DEFAULT constraints.

**What**
- Five flavours of constraint applied across the four tables: `NOT NULL`, `UNIQUE`, `CHECK`, `FOREIGN KEY`, `DEFAULT`.
- The database itself now rejects bad data — no Java needed.

**Why**
- The database is the last line of defence.
- Java validation (Day 2 BigDecimal setters, Day 6 service layer) catches most bad data; these constraints catch what slips through.

**Observe**
- Each acceptance-criteria INSERT fails with a clear constraint-violation error.
- A valid INSERT without `created_at` auto-fills the current timestamp.

**Instructions:**
1. Add NOT NULL to required columns (name, email, amount)
2. Add UNIQUE to users.email
3. Add CHECK constraint on categories.type: only 'INCOME' or 'EXPENSE' allowed
4. Add CHECK constraint on transactions.amount: must be > 0
5. Add FOREIGN KEY constraints:
   - transactions.user_id REFERENCES users(user_id)
   - transactions.category_id REFERENCES categories(category_id)
   - savings_goals.user_id REFERENCES users(user_id)
6. Add DEFAULT CURRENT_TIMESTAMP on users.created_at
7. Add DEFAULT 0 on savings_goals.current_amount

**Acceptance Criteria:**
- [ ] INSERT with NULL name fails with constraint error
- [ ] INSERT with duplicate email fails
- [ ] INSERT with type = 'RANDOM' into categories fails
- [ ] INSERT with amount = -100 into transactions fails
- [ ] INSERT with non-existent user_id fails (FK violation)
- [ ] INSERT without created_at auto-fills the current timestamp

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Constraints are *rules* the database enforces. Five flavours show up here: `NOT NULL`, `UNIQUE`, `CHECK (...)`, `REFERENCES <parent>(col)` (foreign key) and `DEFAULT <value>`. You can either rewrite the CREATE TABLE statements with constraints inline, or layer them on with `ALTER TABLE` if the tables already exist.

</details>

<details>
<summary><b>Hint 2 — Constraint syntax cheat-sheet</b></summary>

```sql
-- NOT NULL and UNIQUE go right after the type:
email   VARCHAR(150) NOT NULL UNIQUE

-- CHECK is a boolean expression:
type    VARCHAR(10) NOT NULL CHECK (type IN ('INCOME','EXPENSE'))

-- FOREIGN KEY: inline form
user_id INT NOT NULL REFERENCES users(user_id)

-- DEFAULT auto-fills if you don't supply a value
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
current_amount NUMERIC(12,2) DEFAULT 0
```

If your tables already exist from F003, use `ALTER TABLE`:

```sql
ALTER TABLE users
    ALTER COLUMN name  SET NOT NULL,
    ALTER COLUMN email SET NOT NULL,
    ADD CONSTRAINT users_email_unique UNIQUE (email);
```

</details>

<details>
<summary><b>Hint 3 — Full solution (rewrite of create_tables.sql)</b></summary>

```sql
DROP TABLE IF EXISTS savings_goals;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id    SERIAL       PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    category_id SERIAL       PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    type        VARCHAR(10)  NOT NULL
                             CHECK (type IN ('INCOME', 'EXPENSE'))
);

CREATE TABLE transactions (
    txn_id      SERIAL         PRIMARY KEY,
    user_id     INT            NOT NULL REFERENCES users(user_id),
    category_id INT            NOT NULL REFERENCES categories(category_id),
    amount      NUMERIC(12,2)  NOT NULL CHECK (amount > 0),
    txn_date    DATE           NOT NULL DEFAULT CURRENT_DATE,
    description VARCHAR(255),
    type        VARCHAR(10)    NOT NULL
                               CHECK (type IN ('INCOME', 'EXPENSE'))
);

CREATE TABLE savings_goals (
    goal_id        SERIAL        PRIMARY KEY,
    user_id        INT           NOT NULL REFERENCES users(user_id),
    goal_name      VARCHAR(100)  NOT NULL,
    target_amount  NUMERIC(12,2) NOT NULL CHECK (target_amount > 0),
    current_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    deadline       DATE
);
```

Test each constraint individually:

```sql
-- NOT NULL
INSERT INTO users (email) VALUES ('x@y.com');
-- ERROR: null value in column "name" violates not-null constraint

-- UNIQUE
INSERT INTO users (name, email) VALUES ('A', 'a@b.com');
INSERT INTO users (name, email) VALUES ('B', 'a@b.com');
-- ERROR: duplicate key value violates unique constraint "users_email_key"

-- CHECK on categories.type
INSERT INTO categories (name, type) VALUES ('Test', 'RANDOM');
-- ERROR: new row violates check constraint "categories_type_check"

-- CHECK on transactions.amount
INSERT INTO transactions (user_id, category_id, amount, txn_date, type)
VALUES (1, 1, -50, CURRENT_DATE, 'EXPENSE');
-- ERROR: new row violates check constraint "transactions_amount_check"

-- FOREIGN KEY
INSERT INTO transactions (user_id, category_id, amount, txn_date, type)
VALUES (999, 1, 50, CURRENT_DATE, 'EXPENSE');
-- ERROR: insert or update on table "transactions" violates foreign key constraint

-- DEFAULT
INSERT INTO users (name, email) VALUES ('Test', 'test@db.com');
SELECT created_at FROM users WHERE email = 'test@db.com';
-- returns current timestamp, not NULL
```

</details>

---

### TICKET-F005: Seed Data
**File:** `db/seed_data.sql`

**Description:** Insert realistic sample data into all 4 tables.

**What**
- 5 categories, 5 users, 15+ transactions across 3+ months, and 4 savings goals.
- All inserted in dependency order: categories and users first (no FKs), then transactions and goals.

**Why**
- Without seed data every query returns zero rows and you can't tell if your SQL is right.
- The 3+ months requirement is what makes Day 9's chart actually render.

**Observe**
- `SELECT COUNT(*)` returns 5 / 5 / 15+ / 4 on the four tables.
- `SELECT DISTINCT TO_CHAR(txn_date,'YYYY-MM') FROM transactions;` shows at least 3 distinct months.

**Instructions:**
1. Insert 5 users with realistic names and emails
2. Insert 5 categories: at least 2 INCOME and 3 EXPENSE types
3. Insert 15+ transactions across different users, categories, and dates
4. Insert 4 savings goals for different users with varying progress

**Acceptance Criteria:**
- [ ] All INSERT statements execute without errors
- [ ] `SELECT COUNT(*) FROM users` returns 5
- [ ] `SELECT COUNT(*) FROM categories` returns 5
- [ ] `SELECT COUNT(*) FROM transactions` returns 15+
- [ ] `SELECT COUNT(*) FROM savings_goals` returns 4
- [ ] Data includes both INCOME and EXPENSE transactions
- [ ] Transactions span at least 3 different months

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Insert in dependency order: `categories` and `users` first (no FKs), then `transactions` and `savings_goals` (which need a real `user_id` / `category_id` to exist). Don't supply the `*_id` columns yourself — `SERIAL` auto-assigns them in the order you insert.

</details>

<details>
<summary><b>Hint 2 — Multi-row INSERT pattern</b></summary>

```sql
-- One INSERT can carry many rows using a comma-separated VALUES list:
INSERT INTO users (name, email) VALUES
    ('Alice Smith', 'alice@bank.com'),
    ('Bob Jones',   'bob@bank.com'),
    ('Carol Reed',  'carol@bank.com'),
    ('Dave Patel',  'dave@bank.com'),
    ('Eve Lin',     'eve@bank.com');
```

Mix INCOME and EXPENSE categories. Spread transaction dates across at least 3 months so Day 1 / Day 9 chart code has something to render.

</details>

<details>
<summary><b>Hint 3 — Full solution (db/seed_data.sql)</b></summary>

```sql
-- Categories first (no dependencies)
INSERT INTO categories (name, type) VALUES
    ('Salary',     'INCOME'),
    ('Freelance',  'INCOME'),
    ('Food',       'EXPENSE'),
    ('Transport',  'EXPENSE'),
    ('Utilities',  'EXPENSE');

-- Users next
INSERT INTO users (name, email) VALUES
    ('Alice Smith', 'alice@bank.com'),
    ('Bob Jones',   'bob@bank.com'),
    ('Carol Reed',  'carol@bank.com'),
    ('Dave Patel',  'dave@bank.com'),
    ('Eve Lin',     'eve@bank.com');

-- Transactions (15 rows across 3 months, mixed types)
INSERT INTO transactions
    (user_id, category_id, amount, txn_date, description, type) VALUES
    (1, 1, 3500.00, '2026-01-01', 'January salary',   'INCOME'),
    (1, 3,   45.20, '2026-01-08', 'Groceries',        'EXPENSE'),
    (1, 4,   25.00, '2026-01-15', 'Bus pass',         'EXPENSE'),
    (2, 1, 4200.00, '2026-01-01', 'January salary',   'INCOME'),
    (2, 5,  120.00, '2026-01-20', 'Electricity bill', 'EXPENSE'),
    (3, 2,  800.00, '2026-02-05', 'Freelance gig',    'INCOME'),
    (3, 3,   60.00, '2026-02-10', 'Restaurant',       'EXPENSE'),
    (1, 1, 3500.00, '2026-02-01', 'February salary',  'INCOME'),
    (1, 3,   38.40, '2026-02-12', 'Groceries',        'EXPENSE'),
    (4, 1, 2800.00, '2026-02-01', 'February salary',  'INCOME'),
    (4, 4,   35.00, '2026-02-18', 'Taxi to airport',  'EXPENSE'),
    (5, 1, 3100.00, '2026-03-01', 'March salary',     'INCOME'),
    (5, 3,   52.00, '2026-03-05', 'Groceries',        'EXPENSE'),
    (2, 2,  500.00, '2026-03-10', 'Side project',     'INCOME'),
    (2, 5,   95.00, '2026-03-20', 'Internet bill',    'EXPENSE');

-- Savings goals
INSERT INTO savings_goals
    (user_id, goal_name, target_amount, current_amount, deadline) VALUES
    (1, 'Holiday Fund',     2000.00,  450.00, '2026-12-01'),
    (2, 'New Laptop',       1500.00, 1500.00, '2026-06-30'),  -- complete!
    (3, 'Emergency Buffer', 5000.00,  800.00, '2026-12-31'),
    (4, 'Wedding',         10000.00, 2500.00, '2027-09-15');

-- Verify
SELECT COUNT(*) FROM categories;     -- 5
SELECT COUNT(*) FROM users;          -- 5
SELECT COUNT(*) FROM transactions;   -- 15
SELECT COUNT(*) FROM savings_goals;  -- 4
```

</details>

---

### TICKET-F006: Write 5 SQL Queries
**File:** `db/queries.sql`

**Description:** Write queries that answer business questions using JOINs, GROUP BY, and ORDER BY.

**What**
- Five SELECT queries saved in `db/queries.sql`.
- Covers: 3-table JOIN, filter + sort, monthly totals via `DATE_TRUNC`, a window function, and a VIEW.

**Why**
- These five techniques cover ~80% of real analytical SQL.
- Every dashboard, report, or analytics screen you'll ever build is some combination of them.

**Observe**
- Q1 shows names instead of numeric IDs.
- Q3 returns one row per (user, month).
- Q4 shows a running balance that grows on INCOME and shrinks on EXPENSE.

**Instructions (follow the TODOs in queries.sql):**

1. **Q1 (also TICKET-F007):** All transactions with user name and category name (3-table JOIN)
2. **Q2:** EXPENSE transactions only, sorted by amount highest first (WHERE + ORDER BY DESC)
3. **Q3:** Monthly totals per user (GROUP BY with DATE_TRUNC or EXTRACT)
4. **Q4:** Running balance per user using a window function (SUM OVER with PARTITION BY)
5. **Q5 (also TICKET-F008):** Top 3 expense categories as a VIEW

**Acceptance Criteria:**
- [ ] Q1 returns human-readable names, not numeric IDs
- [ ] Q2 results are all EXPENSE type and sorted descending by amount
- [ ] Q3 shows one row per user per month with total amounts
- [ ] Q4 shows a cumulative running balance that increases with each transaction
- [ ] Q5 creates a VIEW that you can query with `SELECT * FROM top_expense_categories`

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Write each query in isolation first, then save them all to `db/queries.sql`. Q1 = 3-table JOIN (covered in F007). Q2 = same JOIN + `WHERE type='EXPENSE' ORDER BY amount DESC`. Q3 = `GROUP BY user, DATE_TRUNC('month', txn_date)`. Q4 = `SUM(...) OVER (PARTITION BY user_id ORDER BY txn_date)`. Q5 = wrap a TOP-3 query in `CREATE VIEW`.

</details>

<details>
<summary><b>Hint 2 — Skeletons for each of the 5 queries</b></summary>

```sql
-- Q1: JOIN three tables (see F007 for the full pattern)
SELECT t.txn_id, u.name AS user_name, c.name AS category, t.amount, t.txn_date
FROM transactions t
JOIN users u      ON t.user_id     = u.user_id
JOIN categories c ON t.category_id = c.category_id;

-- Q2: filter + sort
SELECT ... WHERE t.type = 'EXPENSE' ORDER BY t.amount DESC;

-- Q3: monthly totals per user
SELECT u.name,
       DATE_TRUNC('month', t.txn_date) AS month,
       SUM(t.amount) AS total
FROM transactions t JOIN users u USING (user_id)
GROUP BY u.name, month;

-- Q4: window function
SELECT ..., SUM(CASE WHEN type='INCOME' THEN amount ELSE -amount END)
              OVER (PARTITION BY user_id ORDER BY txn_date) AS running_balance
FROM transactions;

-- Q5: VIEW (see F008)
CREATE VIEW top_expense_categories AS ...;
```

</details>

<details>
<summary><b>Hint 3 — Full solution (db/queries.sql)</b></summary>

```sql
-- ============================================================
-- Q1: All transactions with user + category names (3-table JOIN)
-- ============================================================
SELECT t.txn_id,
       u.name  AS user_name,
       c.name  AS category,
       t.amount,
       t.txn_date,
       t.description,
       t.type
FROM transactions t
JOIN users      u ON t.user_id     = u.user_id
JOIN categories c ON t.category_id = c.category_id
ORDER BY t.txn_date DESC, t.txn_id;

-- ============================================================
-- Q2: EXPENSE only, highest amount first
-- ============================================================
SELECT t.txn_id, u.name, c.name AS category, t.amount, t.txn_date
FROM transactions t
JOIN users      u ON t.user_id     = u.user_id
JOIN categories c ON t.category_id = c.category_id
WHERE t.type = 'EXPENSE'
ORDER BY t.amount DESC;

-- ============================================================
-- Q3: Monthly totals per user
-- ============================================================
SELECT u.name,
       TO_CHAR(DATE_TRUNC('month', t.txn_date), 'Mon YYYY') AS month,
       SUM(t.amount) AS total
FROM transactions t
JOIN users u ON t.user_id = u.user_id
GROUP BY u.name, DATE_TRUNC('month', t.txn_date)
ORDER BY u.name, DATE_TRUNC('month', t.txn_date);

-- ============================================================
-- Q4: Running balance per user (window function)
--   Income adds, expense subtracts.
-- ============================================================
SELECT u.name,
       t.txn_date,
       t.type,
       t.amount,
       SUM(CASE WHEN t.type = 'INCOME' THEN  t.amount
                                       ELSE -t.amount END)
           OVER (PARTITION BY u.user_id ORDER BY t.txn_date, t.txn_id)
           AS running_balance
FROM transactions t
JOIN users u ON t.user_id = u.user_id
ORDER BY u.name, t.txn_date, t.txn_id;

-- ============================================================
-- Q5: VIEW — top 3 expense categories by total spend
-- ============================================================
CREATE OR REPLACE VIEW top_expense_categories AS
SELECT c.name AS category, SUM(t.amount) AS total_spent
FROM transactions t
JOIN categories c ON t.category_id = c.category_id
WHERE c.type = 'EXPENSE'
GROUP BY c.name
ORDER BY total_spent DESC
LIMIT 3;

SELECT * FROM top_expense_categories;
```

</details>

---

### TICKET-F007: JOIN Query
**File:** `db/queries.sql` (same as Q1 above)

**Description:** Write a multi-table JOIN query combining transactions, users, and categories.

**What**
- One SELECT that combines rows from three tables — `transactions`, `users`, `categories` — using two `JOIN ... ON ...` clauses.
- Returns human-readable names (`Alice Smith`, `Food`) in place of numeric IDs (`1`, `3`).

**Why**
- Without JOINs you'd run three separate queries and stitch them together in code.
- JOINs let the database do the matching once, in one round-trip — the basis for every API endpoint from Day 5 onwards.

**Observe**
- Result columns show `Alice Smith` and `Food`, not `1` and `3`.
- Row count equals `SELECT COUNT(*) FROM transactions;` — no rows dropped by the inner join.

**Instructions:**
- JOIN transactions with users ON user_id
- JOIN transactions with categories ON category_id
- Select: txn_id, user name, category name, amount, date, description, type
- Use table aliases (t, u, c) for readability

**Acceptance Criteria:**
- [ ] Query uses at least 2 JOIN clauses
- [ ] Results show names instead of numeric IDs
- [ ] All columns are present in the output
- [ ] Query runs without errors on your seed data

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

A `JOIN` says "match rows from this table with rows in that table on a shared column". You need two JOINs because `transactions` connects to BOTH `users` AND `categories`. Use short aliases (`t`, `u`, `c`) so the query reads like English.

</details>

<details>
<summary><b>Hint 2 — Pattern</b></summary>

```sql
SELECT ...                             -- columns you want, using aliases
FROM   transactions t
JOIN   users      u ON t.user_id     = u.user_id
JOIN   categories c ON t.category_id = c.category_id;
```

Pick columns you actually want returned: `t.txn_id, u.name, c.name, t.amount, t.txn_date, t.description, t.type`. If two columns share a name (`u.name` and `c.name`), alias them: `u.name AS user_name, c.name AS category`.

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```sql
SELECT t.txn_id,
       u.name        AS user_name,
       c.name        AS category,
       t.amount,
       t.txn_date,
       t.description,
       t.type
FROM transactions t
JOIN users      u ON t.user_id     = u.user_id
JOIN categories c ON t.category_id = c.category_id
ORDER BY t.txn_date DESC, t.txn_id;
```

Expected output (first few rows):

```
 txn_id | user_name |  category  | amount  |  txn_date  |     description     |  type
--------+-----------+------------+---------+------------+---------------------+---------
     15 | Bob Jones | Utilities  |   95.00 | 2026-03-20 | Internet bill       | EXPENSE
     14 | Bob Jones | Freelance  |  500.00 | 2026-03-10 | Side project        | INCOME
     13 | Eve Lin   | Food       |   52.00 | 2026-03-05 | Groceries           | EXPENSE
     12 | Eve Lin   | Salary     | 3100.00 | 2026-03-01 | March salary        | INCOME
     ...
```

Sanity check: count rows — should equal `SELECT COUNT(*) FROM transactions;` (every transaction has a user and a category, so an INNER JOIN drops nothing).

</details>

---

### TICKET-F008: Create a VIEW
**File:** `db/queries.sql` (same as Q5 above)

**Description:** Create a reusable VIEW for top expense categories.

**What**
- A saved SELECT named `top_expense_categories` you can query like a table.
- Returns the 3 EXPENSE categories with the highest total spend.

**Why**
- Views give you a stable name for a complex query.
- Day 9 dashboards (or any BI tool later) can `SELECT * FROM top_expense_categories` without re-deriving the aggregation.

**Observe**
- `SELECT * FROM top_expense_categories;` returns exactly 3 rows, ordered by spend descending.
- Insert another EXPENSE in one of those categories and the view's total updates immediately — no data is stored; the query re-runs.

**Instructions:**
- Write a query that groups expenses by category and sums their amounts
- Use ORDER BY total DESC and LIMIT 3
- Wrap it in CREATE VIEW top_expense_categories AS (...)
- Test by querying: SELECT * FROM top_expense_categories

**Acceptance Criteria:**
- [ ] VIEW is created successfully
- [ ] `SELECT * FROM top_expense_categories` returns 3 rows
- [ ] Categories are sorted by total spending (highest first)
- [ ] Only EXPENSE categories appear (not INCOME)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

A VIEW is a saved SELECT query that you can re-run by name. Write the SELECT first, prove it returns the rows you want, *then* wrap it in `CREATE VIEW <name> AS (...)`. Use `CREATE OR REPLACE VIEW` so re-running the script doesn't error.

</details>

<details>
<summary><b>Hint 2 — Build it in two steps</b></summary>

Step 1 — get the SELECT right:

```sql
SELECT c.name AS category, SUM(t.amount) AS total_spent
FROM transactions t
JOIN categories c ON t.category_id = c.category_id
WHERE c.type = 'EXPENSE'
GROUP BY c.name
ORDER BY total_spent DESC
LIMIT 3;
```

Step 2 — once that returns 3 sensible rows, save it as a VIEW with `CREATE OR REPLACE VIEW top_expense_categories AS ( <that SELECT> );`.

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```sql
CREATE OR REPLACE VIEW top_expense_categories AS
SELECT c.name           AS category,
       SUM(t.amount)    AS total_spent,
       COUNT(*)         AS txn_count
FROM transactions t
JOIN categories  c ON t.category_id = c.category_id
WHERE c.type = 'EXPENSE'
GROUP BY c.name
ORDER BY total_spent DESC
LIMIT 3;

-- Use it like a regular table
SELECT * FROM top_expense_categories;
```

Expected output (with the seed data from F005):

```
 category  | total_spent | txn_count
-----------+-------------+-----------
 Utilities |      215.00 |         2
 Food      |      195.60 |         4
 Transport |       60.00 |         2
```

Notes:
- `OR REPLACE` lets you re-run the script without dropping the view first.
- The VIEW always reflects the latest data — it's not a snapshot. Insert more EXPENSE rows and re-`SELECT * FROM top_expense_categories;` to confirm.
- To remove it: `DROP VIEW top_expense_categories;`

</details>

---

### TICKET-F009: Advanced SQL -- Window Functions
**File:** `db/queries.sql`

**Description:** Use window functions to calculate running balances.

**What**
- A query that returns every transaction plus a running-balance column per user, accumulated in date order.
- INCOME adds to the balance; EXPENSE subtracts.

**Why**
- Canonical window-function example.
- `GROUP BY` collapses rows; window functions keep them. Both are essential — knowing when to reach for each is the lesson.

**Observe**
- Per user, `running_balance` grows on INCOME rows and shrinks on EXPENSE rows.
- When the next user's first row appears, the balance resets to that row's signed amount — PARTITION BY at work.

**Instructions:**
- Write a query using SUM() OVER (PARTITION BY user_id ORDER BY txn_date)
- The running balance should accumulate for each user separately
- Income adds to the balance, expenses subtract

**Acceptance Criteria:**
- [ ] Each user's transactions show a running total
- [ ] The running balance resets for each user (PARTITION BY)
- [ ] Results are ordered by date within each user

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

A window function looks at a "window" of rows around the current row without collapsing them like `GROUP BY` does. `SUM(...) OVER (PARTITION BY user_id ORDER BY txn_date)` means: for each row, sum every row with the same `user_id` whose `txn_date` is on or before the current row's date. That's a running total.

</details>

<details>
<summary><b>Hint 2 — Anatomy of the OVER clause</b></summary>

```sql
SUM( <thing to add> ) OVER ( PARTITION BY <reset column> ORDER BY <accumulate column> )
                                    │                            │
                                    │                            └─ within the partition,
                                    │                               accumulate in this order
                                    └─ reset the running total when this column changes
```

For the SmartBudget running balance, INCOME should *add* and EXPENSE should *subtract*. Use a `CASE` expression to flip the sign:

```sql
CASE WHEN type = 'INCOME' THEN  amount
                          ELSE -amount END
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```sql
SELECT u.name,
       t.txn_date,
       t.type,
       t.amount,
       SUM( CASE WHEN t.type = 'INCOME' THEN  t.amount
                                        ELSE -t.amount END )
           OVER ( PARTITION BY u.user_id
                  ORDER BY     t.txn_date, t.txn_id )
           AS running_balance
FROM transactions t
JOIN users u ON t.user_id = u.user_id
ORDER BY u.name, t.txn_date, t.txn_id;
```

Expected (for Alice, who has 5 transactions in the seed):

```
 name        | txn_date   | type    | amount  | running_balance
-------------+------------+---------+---------+-----------------
 Alice Smith | 2026-01-01 | INCOME  | 3500.00 |         3500.00
 Alice Smith | 2026-01-08 | EXPENSE |   45.20 |         3454.80
 Alice Smith | 2026-01-15 | EXPENSE |   25.00 |         3429.80
 Alice Smith | 2026-02-01 | INCOME  | 3500.00 |         6929.80
 Alice Smith | 2026-02-12 | EXPENSE |   38.40 |         6891.40
 Bob Jones   | 2026-01-01 | INCOME  | 4200.00 |         4200.00   ← resets per user
 ...
```

Notes:
- The secondary `ORDER BY t.txn_id` in the OVER clause is a tie-breaker so two transactions on the same date come out in a deterministic order.
- Replace `SUM` with `ROW_NUMBER()`, `LAG()`, `RANK()` etc. — same `OVER` syntax.
- Window functions don't reduce the number of rows. `GROUP BY` would have collapsed each user to 1 row.

</details>

---

### TICKET-F010: Advanced SQL -- CTEs
**File:** `db/queries.sql`

**Description:** Use a Common Table Expression (CTE) for a multi-step query.

**What**
- A query that uses `WITH income AS (...), expenses AS (...)` to build two named intermediate result sets.
- LEFT JOINs them onto `users` to compute each user's net balance.

**Why**
- CTEs let you name your steps; equivalent subqueries work but get unreadable fast.
- The LEFT JOIN + COALESCE pattern keeps users with no income (or no expenses) visible with zeros instead of dropping them.

**Observe**
- One row per user (5 rows on the seed data).
- A user with only expenses shows `total_income = 0`, not NULL.
- `net_balance = total_income - total_expenses` matches mental arithmetic.

**Instructions:**
- Write a CTE using WITH ... AS syntax
- First CTE step: calculate total income per user
- Second CTE step: calculate total expenses per user
- Final SELECT: combine to show each user's net balance (income - expenses)

**Acceptance Criteria:**
- [ ] Query uses WITH ... AS syntax
- [ ] Net balance = total income - total expenses for each user
- [ ] Users with no income or no expenses still appear (use COALESCE)
- [ ] Results show user name, total income, total expenses, and net balance

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

A CTE (`WITH name AS (SELECT ...)`) is a named, throwaway result set you can reference like a table in the rest of the query. You'll build two CTEs — `income` and `expenses` — each with `(user_id, total)`, then `LEFT JOIN` them onto `users` and compute the difference. `LEFT JOIN` (not inner) is what keeps users with no income or no expenses visible.

</details>

<details>
<summary><b>Hint 2 — Skeleton</b></summary>

```sql
WITH income AS (
    SELECT user_id, SUM(amount) AS total
    FROM transactions
    WHERE type = 'INCOME'
    GROUP BY user_id
),
expenses AS (
    SELECT user_id, SUM(amount) AS total
    FROM transactions
    WHERE type = 'EXPENSE'
    GROUP BY user_id
)
SELECT u.name,
       COALESCE(i.total, 0)                                AS total_income,
       COALESCE(e.total, 0)                                AS total_expenses,
       COALESCE(i.total, 0) - COALESCE(e.total, 0)         AS net_balance
FROM users u
LEFT JOIN income   i ON u.user_id = i.user_id
LEFT JOIN expenses e ON u.user_id = e.user_id
ORDER BY net_balance DESC;
```

`COALESCE(x, 0)` returns `0` when `x` IS NULL — needed because a user with no INCOME transactions won't appear in the `income` CTE, so the LEFT JOIN yields NULL.

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```sql
-- ============================================================
-- Net balance per user using a CTE
-- ============================================================
WITH income AS (
    SELECT user_id, SUM(amount) AS total
    FROM transactions
    WHERE type = 'INCOME'
    GROUP BY user_id
),
expenses AS (
    SELECT user_id, SUM(amount) AS total
    FROM transactions
    WHERE type = 'EXPENSE'
    GROUP BY user_id
)
SELECT u.user_id,
       u.name,
       COALESCE(i.total, 0)                          AS total_income,
       COALESCE(e.total, 0)                          AS total_expenses,
       COALESCE(i.total, 0) - COALESCE(e.total, 0)   AS net_balance
FROM users u
LEFT JOIN income   i ON u.user_id = i.user_id
LEFT JOIN expenses e ON u.user_id = e.user_id
ORDER BY net_balance DESC;
```

Expected output (with the F005 seed data):

```
 user_id | name        | total_income | total_expenses | net_balance
---------+-------------+--------------+----------------+-------------
       2 | Bob Jones   |      4700.00 |         215.00 |     4485.00
       1 | Alice Smith |      7000.00 |         108.60 |     6891.40
       4 | Dave Patel  |      2800.00 |          35.00 |     2765.00
       5 | Eve Lin     |      3100.00 |          52.00 |     3048.00
       3 | Carol Reed  |       800.00 |          60.00 |      740.00
```

Why a CTE here? You could write this as two subqueries in the SELECT, but CTEs make the intent obvious — "first compute income per user, then expenses per user, then combine." That's the value: readability and decomposition.

Self-check: insert a user with no transactions at all, re-run — they should appear with `0 / 0 / 0`, *not* be missing.

</details>

---

## End-of-Day Checklist

- [ ] All 4 tables created with correct constraints
- [ ] Seed data loaded (5 users, 5 categories, 15+ transactions, 4 goals)
- [ ] 5 SQL queries written and tested
- [ ] ER diagram completed
- [ ] You can explain: PRIMARY KEY, FOREIGN KEY, JOIN, GROUP BY, WINDOW FUNCTION, CTE, VIEW

---

*Tomorrow (Day 2): You will create Java classes that represent these database tables as objects.*
