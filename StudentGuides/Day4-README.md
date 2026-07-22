# Day 4 -- JDBC, Collections & Testing (Sprint 3)

> TICKET-F032 through TICKET-F043

---

## Overview

Today you learn three major topics:

1. **JDBC** -- Connect Java to PostgreSQL using raw SQL (DriverManager, PreparedStatement)
2. **Collections** -- Refactor TransactionService from ArrayList to HashMap, add Streams/Lambdas
3. **Testing** -- Write unit tests with JUnit 5 and Mockito

By the end of Day 4, your Java code can read/write to a real PostgreSQL database, and you have automated tests proving your code works.

---

## Key Concepts

- **JDBC**: Java Database Connectivity -- the standard API for database access
- **PreparedStatement**: Prevents SQL injection by separating SQL from data
- **HashMap**: Key-value storage with O(1) lookups (vs ArrayList's O(n))
- **Streams**: Java 8+ functional pipeline for processing collections
- **Lambda**: Anonymous function -- `(a, b) -> a + b`
- **JUnit 5**: Java testing framework with `@Test` annotation
- **Mockito**: Library for creating mock objects to isolate units of code

---

## Tickets

### TICKET-F032: Refactor List to HashMap
**File:** `backend/src/main/java/com/smartbudget/service/TransactionService.java`

**Description:** Replace the ArrayList storage with a HashMap for O(1) lookups by ID.

**What**
- `TransactionService` backed by `Map<String, BaseTransaction>` instead of `List<BaseTransaction>`, with a new `findById(String)` for O(1) lookup.

**Why**
- Lookup-by-id is the dominant access pattern; ArrayList forces an O(n) scan, while HashMap hits the right bucket directly via `hashCode()`.

**Observe**
- `getAll().size()` matches the number of `addTransaction` calls and `findById(String.valueOf(t.getTxnId()))` returns the same instance you added.

**Instructions (follow the TODO in TransactionService.java):**
1. Change `List<BaseTransaction>` to `Map<String, BaseTransaction>`
2. Initialize as `new HashMap<>()`
3. Update `addTransaction`: use `map.put(String.valueOf(t.getTxnId()), t)`
4. Update `getAll`: return `new ArrayList<>(map.values())`

**Acceptance Criteria:**
- [ ] All existing functionality still works after the refactor
- [ ] You can look up a transaction by ID in O(1) time
- [ ] `getAll()` still returns a List (the public API doesn't change)
- [ ] You can explain: Why HashMap is faster than ArrayList for lookups

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Swap `List<BaseTransaction>` for `Map<String, BaseTransaction>`. Key by `String.valueOf(t.getTxnId())`. `addTransaction` becomes `map.put(...)`; `getAll()` returns `new ArrayList<>(map.values())` so the public signature doesn't change. Add a new O(1) method: `findById(String id) -> map.get(id)`.

</details>

<details>
<summary><b>Hint 2 — Field + key methods</b></summary>

```java
private final Map<String, BaseTransaction> transactions = new HashMap<>();

public void addTransaction(BaseTransaction t) {
    transactions.put(String.valueOf(t.getTxnId()), t);
}

public List<BaseTransaction> getAll() {
    return new ArrayList<>(transactions.values());
}

public BaseTransaction findById(String id) {
    return transactions.get(id);          // O(1) lookup — the win
}
```

Filters that iterate (`filterByDateRange`, `calculateTotalByType`) still loop in O(n) — HashMap only helps with point lookups by key.

</details>

<details>
<summary><b>Hint 3 — Full refactor</b></summary>

```java
package com.smartbudget.service;

import com.smartbudget.exception.InvalidTransactionException;
import com.smartbudget.model.BaseTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class TransactionService {

    private final Map<String, BaseTransaction> transactions = new HashMap<>();

    public void addTransaction(BaseTransaction t) {
        if (t == null) {
            throw new IllegalArgumentException("transaction must not be null");
        }
        if (t.getDescription() == null || t.getDescription().isBlank()) {
            throw new InvalidTransactionException("description must not be blank");
        }
        transactions.put(String.valueOf(t.getTxnId()), t);
    }

    public BaseTransaction findById(String id) { return transactions.get(id); }

    public boolean delete(String id) { return transactions.remove(id) != null; }

    public List<BaseTransaction> getAll() {
        return new ArrayList<>(transactions.values());
    }

    public int size() { return transactions.size(); }

    public List<BaseTransaction> filterByDateRange(LocalDate from, LocalDate to) {
        List<BaseTransaction> result = new ArrayList<>();
        for (BaseTransaction t : transactions.values()) {
            LocalDate d = t.getTxnDate();
            if (!d.isBefore(from) && !d.isAfter(to)) result.add(t);
        }
        return result;
    }

    public BigDecimal calculateTotalByType(String type) {
        BigDecimal total = BigDecimal.ZERO;
        for (BaseTransaction t : transactions.values()) {
            if (type.equals(t.getType())) total = total.add(t.getAmount());
        }
        return total;
    }
}
```

Why HashMap is faster for lookups: a HashMap uses the key's `hashCode()` to compute a bucket index in O(1) on average — direct array access. An ArrayList has to scan every element until it finds the match, which is O(n). For 10 items the difference is invisible; for 10 million it's the difference between 100ns and 100ms.

Trade-off: HashMap doesn't preserve insertion order (use `LinkedHashMap` if you want that), and consumes a bit more memory per entry.

</details>

---

### TICKET-F033: Stream-Based Filtering
**File:** `backend/src/main/java/com/smartbudget/service/TransactionService.java`

**Description:** Add methods that use Java Streams instead of for-loops.

**What**
- Two new methods on `TransactionService` — `getExpensesOver100()` and `getSortedByDate()` — implemented as `.stream().filter(...).collect(toList())` pipelines.

**Why**
- Streams express intent linearly (values -> filter -> collect) and force you to think in immutable transformations rather than mutable accumulators.

**Observe**
- With seeded data containing a 200 EXPENSE and a 50 EXPENSE, `getExpensesOver100().size() == 1`.
- `getSortedByDate()` returns transactions with `getTxnDate()` in ascending order.

**Instructions:**
1. `getExpensesOver100()`: Use `.stream().filter()` to keep expenses > 100
   - Chain: `collection.stream().filter(t -> t.getType().equals("EXPENSE")).filter(t -> t.getAmount().compareTo(new BigDecimal("100")) > 0).collect(Collectors.toList())`
2. `getSortedByDate()`: Use `.stream().sorted()` with `Comparator.comparing()`

**Acceptance Criteria:**
- [ ] `getExpensesOver100()` returns only EXPENSE transactions with amount > 100
- [ ] `getSortedByDate()` returns transactions ordered by date (oldest first)
- [ ] Both methods use Streams, not for-loops
- [ ] Original collection is not modified

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Stream pipeline: `collection.stream() → .filter(...) → .sorted(...) → .collect(toList())`. Streams produce a *new* result — the original collection is never mutated. Use `BigDecimal.compareTo` for amount comparisons (no `>` on objects). `Comparator.comparing(BaseTransaction::getTxnDate)` for date sorting (oldest first by default).

</details>

<details>
<summary><b>Hint 2 — Two methods</b></summary>

```java
import static java.util.stream.Collectors.toList;

public List<BaseTransaction> getExpensesOver100() {
    BigDecimal threshold = new BigDecimal("100");
    return transactions.values().stream()
            .filter(t -> "EXPENSE".equals(t.getType()))
            .filter(t -> t.getAmount().compareTo(threshold) > 0)
            .collect(toList());
}

public List<BaseTransaction> getSortedByDate() {
    return transactions.values().stream()
            .sorted(Comparator.comparing(BaseTransaction::getTxnDate))
            .collect(toList());
}
```

`Comparator.comparing(...).reversed()` flips to newest-first.

</details>

<details>
<summary><b>Hint 3 — Full solution + explanation</b></summary>

```java
import java.util.Comparator;
import java.util.stream.Collectors;

public List<BaseTransaction> getExpensesOver100() {
    BigDecimal threshold = new BigDecimal("100");
    return transactions.values().stream()
            .filter(t -> "EXPENSE".equals(t.getType()))
            .filter(t -> t.getAmount().compareTo(threshold) > 0)
            .collect(Collectors.toList());
}

public List<BaseTransaction> getSortedByDate() {
    return transactions.values().stream()
            .sorted(Comparator.comparing(BaseTransaction::getTxnDate))
            .collect(Collectors.toList());
}

public List<BaseTransaction> getSortedByDateDesc() {
    return transactions.values().stream()
            .sorted(Comparator.comparing(BaseTransaction::getTxnDate).reversed())
            .collect(Collectors.toList());
}
```

Why streams? Read the pipeline top-to-bottom and you see exactly what the code does — "values → keep expenses → keep >100 → collect to list". The for-loop equivalent buries that intent in `if` statements and a manually managed `List`.

Sanity test:

```java
svc.addTransaction(new ExpenseTransaction(1, new BigDecimal("50"),  LocalDate.now(), "snack"));
svc.addTransaction(new ExpenseTransaction(2, new BigDecimal("200"), LocalDate.now(), "bill"));
svc.addTransaction(new IncomeTransaction (3, new BigDecimal("500"), LocalDate.now(), "salary"));

System.out.println(svc.getExpensesOver100().size());  // 1  — only the £200 bill
```

Streams are *lazy* — nothing executes until a terminal operation (`collect`, `count`, `forEach`) runs.

</details>

---

### TICKET-F034: Lambda Comparator
**File:** `backend/src/main/java/com/smartbudget/service/TransactionService.java`

**Description:** Sort transactions by amount using a Lambda comparator.

**What**
- A `getSortedByAmount()` method on `TransactionService` that sorts by amount descending via the lambda `(a, b) -> b.getAmount().compareTo(a.getAmount())`.

**Why**
- Lambdas collapse an 8-line anonymous `Comparator` class into a one-liner, and you need fluency with the `(args) -> body` syntax before Day 5's stream-heavy Spring code.

**Observe**
- Given amounts 100, 500, 250 added in any order, `getSortedByAmount()` returns them as 500, 250, 100.

**Instructions:**
- Create `getSortedByAmount()` using `.sorted()` with a lambda:
  - Descending: `(a, b) -> b.getAmount().compareTo(a.getAmount())`
- Compare this one-liner to what the equivalent anonymous class looks like

**Acceptance Criteria:**
- [ ] Transactions are sorted by amount (highest first)
- [ ] Implementation uses a lambda expression, not a named Comparator class
- [ ] You can explain what `(a, b) -> ...` means

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`.sorted(Comparator)` accepts a Comparator function. A lambda `(a, b) -> b.getAmount().compareTo(a.getAmount())` is a Comparator. Putting `b` first reverses the order (descending). `Comparator.comparing(BaseTransaction::getAmount).reversed()` does the same thing more readably.

</details>

<details>
<summary><b>Hint 2 — Two equivalent forms</b></summary>

```java
// Lambda form
public List<BaseTransaction> getSortedByAmount() {
    return transactions.values().stream()
            .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
            .collect(Collectors.toList());
}

// Method-reference form (more readable for simple cases)
public List<BaseTransaction> getSortedByAmount() {
    return transactions.values().stream()
            .sorted(Comparator
                    .comparing(BaseTransaction::getAmount)
                    .reversed())
            .collect(Collectors.toList());
}
```

What `(a, b) -> ...` means: it's an anonymous function that takes two arguments and returns one value. The compiler infers `a` and `b` are both `BaseTransaction`. The body must return an `int` matching `Comparator.compare`'s contract: negative if `a` comes first, positive if `b` comes first, zero if equal.

</details>

<details>
<summary><b>Hint 3 — Full solution + anonymous-class equivalent</b></summary>

```java
public List<BaseTransaction> getSortedByAmount() {
    return transactions.values().stream()
            .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
            .collect(Collectors.toList());
}
```

The lambda is shorthand for this anonymous inner class (pre-Java 8 style):

```java
public List<BaseTransaction> getSortedByAmount() {
    return transactions.values().stream()
            .sorted(new Comparator<BaseTransaction>() {
                @Override
                public int compare(BaseTransaction a, BaseTransaction b) {
                    return b.getAmount().compareTo(a.getAmount());
                }
            })
            .collect(Collectors.toList());
}
```

7 lines of boilerplate → 1 line of intent. The Java compiler generates the same bytecode for both forms (modulo metadata).

Test:

```java
svc.addTransaction(new IncomeTransaction (1, new BigDecimal("100"),  LocalDate.now(), "small"));
svc.addTransaction(new ExpenseTransaction(2, new BigDecimal("500"),  LocalDate.now(), "big"));
svc.addTransaction(new IncomeTransaction (3, new BigDecimal("250"),  LocalDate.now(), "medium"));

svc.getSortedByAmount().forEach(t -> System.out.println(t.getAmount()));
// 500
// 250
// 100
```

</details>

---

### TICKET-F035: JDBC Database Connection
**File:** `backend/src/main/java/com/smartbudget/dao/DatabaseConnection.java`

**Description:** Create a utility class that provides database connections.

**What**
- `DatabaseConnection.getConnection()` returns a live `java.sql.Connection` to `jdbc:postgresql://localhost:5432/smartbudget` using the provided `URL`, `USERNAME`, `PASSWORD` constants.

**Why**
- Every DAO method on this Day depends on this one helper; without a working connection factory, F036-F039 cannot be tested at all.

**Observe**
- `try (Connection c = DatabaseConnection.getConnection()) { System.out.println(c.getMetaData().getURL()); }` prints `jdbc:postgresql://localhost:5432/smartbudget` and exits cleanly.

**Instructions (follow the TODO in DatabaseConnection.java):**
1. Implement `public static Connection getConnection() throws SQLException`
2. Body: `return DriverManager.getConnection(URL, USERNAME, PASSWORD)`
3. Constants are already provided (URL, USERNAME, PASSWORD)

**Prerequisites:**
- PostgreSQL must be running on localhost:5432
- Database `smartbudget` must exist with user `sb_user` / password `sb_pass`
- Run your Day 1 CREATE TABLE scripts first

**Acceptance Criteria:**
- [ ] `DatabaseConnection.getConnection()` returns a live Connection without errors
- [ ] Connection can be used with try-with-resources
- [ ] If PostgreSQL is not running, you get a clear connection refused error
- [ ] You can explain: What DriverManager does, why we use static

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

One-liner method. Three constants already at the top of the file. `DriverManager.getConnection(URL, USERNAME, PASSWORD)` returns a `Connection`. Declare `throws SQLException` so the caller handles it. Don't try to be clever and pool here — it's intentionally simple.

</details>

<details>
<summary><b>Hint 2 — Method body</b></summary>

```java
public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USERNAME, PASSWORD);
}
```

The PostgreSQL JDBC driver registers itself automatically when present on the classpath (Java 6+ `ServiceLoader`). You don't need `Class.forName("org.postgresql.Driver")` anymore — it's a legacy step from older tutorials.

</details>

<details>
<summary><b>Hint 3 — Full file + connection test</b></summary>

```java
package com.smartbudget.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL      = "jdbc:postgresql://localhost:5432/smartbudget";
    private static final String USERNAME = "sb_user";
    private static final String PASSWORD = "sb_pass";

    private DatabaseConnection() { }   // prevent instantiation

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
```

Smoke test (5 lines in a `main`):

```java
try (Connection c = DatabaseConnection.getConnection()) {
    System.out.println("Connected to: " + c.getMetaData().getURL());
    // Connected to: jdbc:postgresql://localhost:5432/smartbudget
} catch (SQLException e) {
    System.err.println("Cannot connect: " + e.getMessage());
}
```

`try (Connection c = ...)` is try-with-resources: it auto-calls `c.close()` at the end of the block, even on exception — non-negotiable for DB resources.

Why `static`? You're not modelling state — there's nothing to instantiate. A static utility method is the idiomatic Java way to expose "get me a thing" helpers. (A production app would replace this with a connection pool like HikariCP.)

If PostgreSQL is down you'll see `SQLException: Connection refused. Check that the hostname and port are correct...` — that's the "clear error" the acceptance criterion is checking for.

</details>

---

### TICKET-F036: DAO -- Insert Transaction
**File:** `backend/src/main/java/com/smartbudget/dao/TransactionDAO.java`

**Description:** Insert a transaction into the database using PreparedStatement.

**What**
- `TransactionDAO.insert(Transaction t)` that runs `INSERT INTO transactions (user_id, category_id, amount, txn_date, description, type) VALUES (?, ?, ?, ?, ?, ?)` via a `PreparedStatement` inside a try-with-resources.

**Why**
- PreparedStatement is the non-negotiable defence against SQL injection and the foundation for every other write operation in the project.

**Observe**
- After `dao.insert(...)`, running `SELECT * FROM transactions ORDER BY txn_id DESC LIMIT 1;` in psql returns the row you just inserted.

**Instructions (follow the TODO in TransactionDAO.java):**
1. Write SQL: `INSERT INTO transactions (user_id, category_id, amount, txn_date, description, type) VALUES (?, ?, ?, ?, ?, ?)`
2. Get a Connection from `DatabaseConnection.getConnection()`
3. Create a `PreparedStatement` and set all 6 parameters
4. Call `ps.executeUpdate()`
5. Use try-with-resources for auto-closing

**Important:** Convert `LocalDate` to `java.sql.Date` using `Date.valueOf(t.getTxnDate())`

**Acceptance Criteria:**
- [ ] Insert executes without errors
- [ ] New record appears in the database (verify with `SELECT * FROM transactions`)
- [ ] PreparedStatement is used (NOT string concatenation)
- [ ] Connection is auto-closed via try-with-resources
- [ ] You can explain: Why PreparedStatement prevents SQL injection

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`try (Connection c = ...; PreparedStatement ps = c.prepareStatement(SQL)) { ps.setX(idx, val) × 6; ps.executeUpdate(); }`. The SQL string uses `?` placeholders for every value. NEVER build SQL with `+` concatenation — that's the SQL-injection door. `Date.valueOf(t.getTxnDate())` turns `LocalDate` into `java.sql.Date`.

</details>

<details>
<summary><b>Hint 2 — Method body</b></summary>

```java
public void insert(Transaction t) throws SQLException {
    String sql = """
            INSERT INTO transactions
                (user_id, category_id, amount, txn_date, description, type)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
    try (Connection c = DatabaseConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, t.getUserId());
        ps.setInt(2, t.getCategoryId());
        ps.setBigDecimal(3, t.getAmount());
        ps.setDate(4, java.sql.Date.valueOf(t.getTxnDate()));
        ps.setString(5, t.getDescription());
        ps.setString(6, t.getType());
        ps.executeUpdate();
    }
}
```

Param indexes are 1-based, not 0. The setter type must match the column type: `setBigDecimal` for `NUMERIC`, `setDate` for `DATE`, etc.

</details>

<details>
<summary><b>Hint 3 — Full solution + injection demo</b></summary>

```java
package com.smartbudget.dao;

import com.smartbudget.model.Transaction;
import java.sql.*;

public class TransactionDAO {

    private static final String INSERT_SQL = """
            INSERT INTO transactions
                (user_id, category_id, amount, txn_date, description, type)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    public void insert(Transaction t) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(INSERT_SQL)) {
            ps.setInt       (1, t.getUserId());
            ps.setInt       (2, t.getCategoryId());
            ps.setBigDecimal(3, t.getAmount());
            ps.setDate      (4, Date.valueOf(t.getTxnDate()));
            ps.setString    (5, t.getDescription());
            ps.setString    (6, t.getType());
            ps.executeUpdate();
        }
    }
}
```

Test:

```java
TransactionDAO dao = new TransactionDAO();
Transaction t = new Transaction(0, 1, 3,
        new BigDecimal("45.20"),
        LocalDate.of(2026, 6, 1),
        "Coffee", "EXPENSE");
dao.insert(t);
// Then in psql: SELECT * FROM transactions ORDER BY txn_id DESC LIMIT 1;
```

**Why PreparedStatement matters.** Suppose `description` came from a form. With string concatenation:

```java
String sql = "INSERT INTO ... VALUES (..., '" + description + "', ...)";
//                                                ^^^^^^^^^^^^
// description = "'); DROP TABLE transactions; --"
// Final SQL: INSERT INTO ... VALUES (..., ''); DROP TABLE transactions; --', ...)
```

PreparedStatement sends the SQL template and the values separately over the wire — the value can't ever be parsed as SQL. Same applies to `setInt`, `setBigDecimal`, etc.

</details>

---

### TICKET-F037: DAO -- Get All Transactions
**File:** `backend/src/main/java/com/smartbudget/dao/TransactionDAO.java`

**Description:** Retrieve all transactions from the database.

**What**
- `TransactionDAO.getAll()` returns a `List<Transaction>` built by looping a `ResultSet` from `SELECT ... FROM transactions ORDER BY txn_date DESC` and mapping each row via a `mapRow(rs)` helper.

**Why**
- Reading rows out of a `ResultSet` and converting `java.sql.Date` to `LocalDate` is the JDBC pattern every later query reuses.

**Observe**
- `dao.getAll().forEach(System.out::println)` prints every seeded row with the most recent `txn_date` first.
- An empty table returns an empty list, not null.

**Instructions:**
1. SQL: `SELECT * FROM transactions ORDER BY txn_date DESC`
2. Use `Statement` (no parameters needed)
3. Loop through `ResultSet` with `while(rs.next())`
4. Map each row to a `Transaction` object using `rs.getLong()`, `rs.getBigDecimal()`, etc.
5. Return the list

**Acceptance Criteria:**
- [ ] Returns a List containing all transactions from the database
- [ ] Transactions are ordered by date (newest first)
- [ ] `rs.getDate("txn_date").toLocalDate()` correctly converts SQL Date to Java LocalDate
- [ ] Empty database returns empty list (not null)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`SELECT * FROM transactions ORDER BY txn_date DESC`. No parameters, so a plain `Statement` works (though `PreparedStatement` is fine too). `while (rs.next()) { build a Transaction from rs.getX(...) and add to list; }`. Pull date with `rs.getDate("txn_date").toLocalDate()`.

</details>

<details>
<summary><b>Hint 2 — Method body + row mapping helper</b></summary>

```java
private static final String SELECT_ALL_SQL =
        "SELECT * FROM transactions ORDER BY txn_date DESC, txn_id DESC";

public List<Transaction> getAll() throws SQLException {
    List<Transaction> list = new ArrayList<>();
    try (Connection c = DatabaseConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(SELECT_ALL_SQL);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) list.add(mapRow(rs));
    }
    return list;
}

private static Transaction mapRow(ResultSet rs) throws SQLException {
    return new Transaction(
            rs.getInt       ("txn_id"),
            rs.getInt       ("user_id"),
            rs.getInt       ("category_id"),
            rs.getBigDecimal("amount"),
            rs.getDate      ("txn_date").toLocalDate(),
            rs.getString    ("description"),
            rs.getString    ("type"));
}
```

Pull the row-mapping out into `mapRow` — `getByUserId` will reuse it.

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
private static final String SELECT_ALL_SQL =
        "SELECT txn_id, user_id, category_id, amount, txn_date, description, type "
      + "FROM transactions ORDER BY txn_date DESC, txn_id DESC";

public List<Transaction> getAll() throws SQLException {
    List<Transaction> list = new ArrayList<>();
    try (Connection c = DatabaseConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(SELECT_ALL_SQL);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            list.add(mapRow(rs));
        }
    }
    return list;       // empty list if table is empty — never null
}

private static Transaction mapRow(ResultSet rs) throws SQLException {
    return new Transaction(
            rs.getInt       ("txn_id"),
            rs.getInt       ("user_id"),
            rs.getInt       ("category_id"),
            rs.getBigDecimal("amount"),
            rs.getDate      ("txn_date").toLocalDate(),
            rs.getString    ("description"),
            rs.getString    ("type"));
}
```

Try it:

```java
TransactionDAO dao = new TransactionDAO();
dao.getAll().forEach(System.out::println);
```

Pitfalls:
- Always name columns explicitly (`SELECT txn_id, ...`) instead of `SELECT *`. Schema changes won't silently break your row mapping.
- `rs.getDate(...)` returns `java.sql.Date`. Always chain `.toLocalDate()` to convert.
- `try-with-resources` chains three resources — Connection, PreparedStatement, ResultSet — closed in reverse order automatically.
- A null `description` column comes back as a literal Java `null`; `rs.getString("description")` returns `null` (not "null").

</details>

---

### TICKET-F038: DAO -- Get by User ID
**File:** `backend/src/main/java/com/smartbudget/dao/TransactionDAO.java`

**Description:** Retrieve transactions for a specific user.

**What**
- `TransactionDAO.getByUserId(int userId)` runs `SELECT ... FROM transactions WHERE user_id = ?` with `ps.setInt(1, userId)` and reuses `mapRow` to build the result list.

**Why**
- Every "show me this user's data" endpoint in the app routes through a parameterised query like this — practising the `?` placeholder + `setInt` flow now prevents string-concatenation habits later.

**Observe**
- `dao.getByUserId(1).size()` returns the count of rows in `transactions` where `user_id = 1`.
- `dao.getByUserId(999999).size()` returns `0` with no exception.

**Instructions:**
1. SQL: `SELECT * FROM transactions WHERE user_id = ?`
2. Use `PreparedStatement` (has a parameter)
3. Set parameter: `ps.setInt(1, userId)`
4. Same ResultSet loop as getAll()

**Acceptance Criteria:**
- [ ] Returns only transactions for the specified user
- [ ] Uses PreparedStatement (not string concatenation)
- [ ] Non-existent userId returns empty list (not null or error)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Almost identical to `getAll`, but the SQL has `WHERE user_id = ?` and you call `ps.setInt(1, userId)` before `executeQuery()`. Reuse the `mapRow` helper from F037.

</details>

<details>
<summary><b>Hint 2 — Method body</b></summary>

```java
private static final String SELECT_BY_USER_SQL =
        "SELECT txn_id, user_id, category_id, amount, txn_date, description, type "
      + "FROM transactions WHERE user_id = ? ORDER BY txn_date DESC";

public List<Transaction> getByUserId(int userId) throws SQLException {
    List<Transaction> list = new ArrayList<>();
    try (Connection c = DatabaseConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(SELECT_BY_USER_SQL)) {
        ps.setInt(1, userId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
    }
    return list;
}
```

The ResultSet is closed inside its own try-with-resources so it's closed before the PreparedStatement.

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
private static final String SELECT_BY_USER_SQL = """
        SELECT txn_id, user_id, category_id, amount, txn_date, description, type
        FROM transactions
        WHERE user_id = ?
        ORDER BY txn_date DESC, txn_id DESC
        """;

public List<Transaction> getByUserId(int userId) throws SQLException {
    List<Transaction> list = new ArrayList<>();
    try (Connection c = DatabaseConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(SELECT_BY_USER_SQL)) {
        ps.setInt(1, userId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
    }
    return list;     // empty list if no rows — never null
}
```

Test:

```java
TransactionDAO dao = new TransactionDAO();
System.out.println(dao.getByUserId(1).size());       // 5 (with the F005 seed)
System.out.println(dao.getByUserId(999).size());     // 0 — no error, empty list
```

If you tried `String sql = "... WHERE user_id = " + userId;` (string concatenation) — that's wrong even when `userId` is an int. Always use PreparedStatement parameters: it's faster (the DB can cache the prepared plan), safer (no injection), and consistent across types.

</details>

---

### TICKET-F039: DAO -- Delete Transaction
**File:** `backend/src/main/java/com/smartbudget/dao/TransactionDAO.java`

**Description:** Delete a transaction by its ID.

**What**
- `TransactionDAO.delete(int txnId)` runs `DELETE FROM transactions WHERE txn_id = ?` and returns the `int` rows-affected from `ps.executeUpdate()`.

**Why**
- Returning the row count (rather than `void`) lets the caller distinguish "deleted" from "nothing matched" without an extra round-trip — the contract every CRUD service relies on.

**Observe**
- Deleting an existing `txn_id` returns `1` and the row disappears from `getAll()`.
- `dao.delete(9999999)` returns `0` and throws nothing.

**Instructions:**
1. SQL: `DELETE FROM transactions WHERE txn_id = ?`
2. Use PreparedStatement
3. Check return value of `executeUpdate()` -- if 0, no record was found

**Acceptance Criteria:**
- [ ] Deleting an existing record succeeds (executeUpdate returns 1)
- [ ] Deleting a non-existent ID returns 0 (no crash)
- [ ] After deletion, the record no longer appears in getAll()

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`DELETE FROM transactions WHERE txn_id = ?`. `executeUpdate()` returns the number of rows affected — return that to the caller. Deleting a missing ID is not an error; it just returns 0.

</details>

<details>
<summary><b>Hint 2 — Method body</b></summary>

```java
private static final String DELETE_SQL =
        "DELETE FROM transactions WHERE txn_id = ?";

public int delete(int txnId) throws SQLException {
    try (Connection c = DatabaseConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(DELETE_SQL)) {
        ps.setInt(1, txnId);
        return ps.executeUpdate();          // 1 if deleted, 0 if not found
    }
}
```

Return type is `int` (rowsAffected) so the caller can tell "did anything change?". Return `boolean` (returns `rowsAffected > 0`) if the caller only needs yes/no.

</details>

<details>
<summary><b>Hint 3 — Full solution + test</b></summary>

```java
private static final String DELETE_SQL =
        "DELETE FROM transactions WHERE txn_id = ?";

public int delete(int txnId) throws SQLException {
    try (Connection c = DatabaseConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(DELETE_SQL)) {
        ps.setInt(1, txnId);
        return ps.executeUpdate();
    }
}
```

Round-trip test:

```java
TransactionDAO dao = new TransactionDAO();
int countBefore = dao.getAll().size();

dao.insert(new Transaction(0, 1, 1,
        new BigDecimal("99.99"), LocalDate.now(),
        "Will be deleted", "EXPENSE"));

int countAfter = dao.getAll().size();
System.out.println("inserted? " + (countAfter == countBefore + 1));

// Find the txn we just made and delete it
int lastId = dao.getAll().stream()
        .mapToInt(Transaction::getTxnId).max().getAsInt();
int affected = dao.delete(lastId);
System.out.println("delete returned: " + affected);   // 1
System.out.println("delete 9_999_999 returned: "
        + dao.delete(9_999_999));                      // 0  — not an error
```

If `transactions.user_id` is referenced by no other table, this is straightforward. If you have a child table referencing `transactions.txn_id`, you'd hit a foreign-key violation — handle it (catch `SQLException`) and surface a meaningful error.

</details>

---

### TICKET-F040: JUnit Test Setup
**File:** `backend/src/test/java/com/smartbudget/service/TransactionServiceTest.java`

**Description:** Set up JUnit 5 test class with Mockito.

**What**
- A `TransactionServiceTest` class with a `@BeforeEach setUp()` that instantiates a fresh `TransactionService` (and reusable `income`/`expense` fixtures) before every test.

**Why**
- A clean SUT per test eliminates ordering-dependent failures; this scaffolding is what F041-F043 build on.

**Observe**
- `./mvnw test` discovers `TransactionServiceTest` and reports at least one `Tests run` line with `Failures: 0`.

**Instructions (follow the TODOs in TransactionServiceTest.java):**
1. Add `@ExtendWith(MockitoExtension.class)` to the class
2. Declare mock fields with `@Mock` (if testing the Spring version later)
3. Declare the service under test with `@InjectMocks`
4. Create a `@BeforeEach` method that initializes test data

**Acceptance Criteria:**
- [ ] Test class compiles and the `@BeforeEach` method runs before each test
- [ ] Test data is initialized fresh for every test (no state leakage between tests)
- [ ] `mvn test` discovers and runs the test class

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

JUnit 5: `import org.junit.jupiter.api.*;`. Class needs no annotation by default. `@BeforeEach` runs before each `@Test`. Use a `private TransactionService svc;` field and reinitialise it in `@BeforeEach` so each test starts clean (no state bleeding between tests).

</details>

<details>
<summary><b>Hint 2 — Class skeleton</b></summary>

```java
package com.smartbudget.service;

import com.smartbudget.model.IncomeTransaction;
import com.smartbudget.model.ExpenseTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    private TransactionService svc;

    @BeforeEach
    void setUp() {
        svc = new TransactionService();    // fresh per test
    }

    @Test
    void emptyServiceHasNoTransactions() {
        assertTrue(svc.getAll().isEmpty());
    }
}
```

Run: `./mvnw test` — should report `Tests run: 1` and a green build.

</details>

<details>
<summary><b>Hint 3 — Full setup file + Mockito variant</b></summary>

```java
package com.smartbudget.service;

import com.smartbudget.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    private TransactionService svc;
    private IncomeTransaction  income;
    private ExpenseTransaction expense;

    @BeforeEach
    void setUp() {
        svc = new TransactionService();
        income  = new IncomeTransaction (1, new BigDecimal("3500"),
                LocalDate.of(2026, 1, 1), "Salary");
        expense = new ExpenseTransaction(2, new BigDecimal("45"),
                LocalDate.of(2026, 1, 5), "Groceries");
    }

    @Test
    void initiallyEmpty() {
        assertEquals(0, svc.size());
    }
}
```

If you want Mockito (you'll need it Day 5/6 when a service depends on a repository):

```java
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository repo;     // collaborator
    @InjectMocks private TransactionService svc;  // SUT — repo injected

    @Test
    void getAll_delegatesToRepo() {
        when(repo.findAll()).thenReturn(List.of(/* ... */));
        assertEquals(0, svc.getAll().size());     // example
        verify(repo).findAll();
    }
}
```

Add to `pom.xml` (already present in starter):
```xml
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter</artifactId>
  <version>5.10.0</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-junit-jupiter</artifactId>
  <version>5.7.0</version>
  <scope>test</scope>
</dependency>
```

</details>

---

### TICKET-F041: Test -- Add and Get
**File:** `backend/src/test/java/com/smartbudget/service/TransactionServiceTest.java`

**Description:** Test that adding a transaction and retrieving it works.

**What**
- A `@Test` method following Arrange-Act-Assert that calls `svc.addTransaction(t)` then asserts `svc.getAll().size() == 1` and the returned item matches what was inserted.

**Why**
- This is the happy-path regression test that locks in the contract of `addTransaction` + `getAll` — break either and the test goes red.

**Observe**
- `./mvnw test` reports the test passing.
- If you intentionally comment out `transactions.put(...)` inside `addTransaction`, the assertion fails with `expected: <1> but was: <0>`.

**Instructions:**
- Create a test method annotated with `@Test`
- Arrange: Create a sample IncomeTransaction
- Act: Call `service.addTransaction(t)`, then `service.getAll()`
- Assert: Verify the list size is 1 and the item matches

**Acceptance Criteria:**
- [ ] Test passes with `mvn test`
- [ ] Uses `assertEquals` or `assertThat` for verification
- [ ] Follows the Arrange-Act-Assert pattern
- [ ] Test method name clearly describes what is being tested

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Three blocks separated by blank lines: **Arrange** (create test data), **Act** (call the method under test), **Assert** (verify the outcome). Method name reads like a sentence: `addTransaction_singleItem_isReturnedByGetAll()`.

</details>

<details>
<summary><b>Hint 2 — Test method</b></summary>

```java
@Test
void addTransaction_singleItem_isReturnedByGetAll() {
    // Arrange
    IncomeTransaction t = new IncomeTransaction(
            1, new BigDecimal("100"), LocalDate.now(), "Test");

    // Act
    svc.addTransaction(t);
    List<BaseTransaction> all = svc.getAll();

    // Assert
    assertEquals(1, all.size());
    assertEquals(t, all.get(0));     // requires BaseTransaction.equals — or check fields
}
```

If `BaseTransaction` doesn't override `equals`, compare fields instead: `assertEquals(t.getTxnId(), all.get(0).getTxnId());`.

</details>

<details>
<summary><b>Hint 3 — Full solution + multi-item test</b></summary>

```java
@Test
void addTransaction_singleItem_isReturnedByGetAll() {
    // Arrange
    IncomeTransaction t = new IncomeTransaction(
            1, new BigDecimal("100"), LocalDate.now(), "Test");

    // Act
    svc.addTransaction(t);

    // Assert
    List<BaseTransaction> all = svc.getAll();
    assertEquals(1, all.size(), "should contain exactly one transaction");
    assertEquals(1, all.get(0).getTxnId());
    assertEquals(new BigDecimal("100"), all.get(0).getAmount());
    assertEquals("INCOME", all.get(0).getType());
}

@Test
void addTransaction_multipleItems_allReturned() {
    svc.addTransaction(income);     // from @BeforeEach
    svc.addTransaction(expense);    // from @BeforeEach

    List<BaseTransaction> all = svc.getAll();
    assertEquals(2, all.size());
}

@Test
void getAll_returnsDefensiveCopy() {
    svc.addTransaction(income);

    svc.getAll().clear();           // mutate the returned list

    assertEquals(1, svc.size(),     // internal state untouched
            "getAll() must return a defensive copy");
}
```

Run with `./mvnw test` — JUnit prints something like:

```
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

The third test guards the design decision from F026 — without the defensive copy, that test would fail.

</details>

---

### TICKET-F042: Test -- Delete
**File:** `backend/src/test/java/com/smartbudget/service/TransactionServiceTest.java`

**Description:** Test that deleting a transaction removes it from the list.

**What**
- A `@Test` that adds a transaction, asserts `svc.size() == 1`, calls `svc.delete(...)`, then asserts `svc.size() == 0` (and the item is absent from `getAll()`).

**Why**
- Asserting only that `delete` doesn't throw is a useless test; checking the post-state proves removal actually happened.

**Observe**
- Test passes under `./mvnw test`.
- If `delete` is stubbed to return without removing, the post-delete `assertEquals(0, svc.size())` fails.

**Instructions:**
- Add a transaction, verify size is 1
- Delete it, verify size is 0
- Or: verify the deleted item no longer appears in getAll()

**Acceptance Criteria:**
- [ ] Test passes
- [ ] Verifies the transaction is actually removed (not just that delete doesn't crash)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Add → assert present → delete → assert absent. Don't just call `delete` and assert it didn't throw — that's a weak test. Confirm `getAll()` no longer contains the item or `size()` decreased.

</details>

<details>
<summary><b>Hint 2 — Test method</b></summary>

```java
@Test
void delete_existingItem_removesIt() {
    svc.addTransaction(income);
    assertEquals(1, svc.size());

    boolean removed = svc.delete(String.valueOf(income.getTxnId()));

    assertTrue(removed);
    assertEquals(0, svc.size());
}
```

If your `delete` returns `void`, just check the size went to 0 after the call.

</details>

<details>
<summary><b>Hint 3 — Full solution + negative case</b></summary>

```java
@Test
void delete_existingItem_removesIt() {
    // Arrange
    svc.addTransaction(income);
    assertEquals(1, svc.size());

    // Act
    boolean removed = svc.delete(String.valueOf(income.getTxnId()));

    // Assert
    assertTrue(removed, "delete should return true when item existed");
    assertEquals(0, svc.size());
    assertNull(svc.findById(String.valueOf(income.getTxnId())));
}

@Test
void delete_missingItem_returnsFalseAndChangesNothing() {
    svc.addTransaction(income);

    boolean removed = svc.delete("999");

    assertFalse(removed);
    assertEquals(1, svc.size(), "delete of missing id must not affect state");
}
```

Why test the negative case? It locks in the *contract*: deleting something that doesn't exist is *not* an error — it just returns `false` (or `0` if you exposed `int`). Without this test, a future refactor could silently change that to throw, and no one would notice until it broke production.

</details>

---

### TICKET-F043: Test -- Invalid Amount
**File:** `backend/src/test/java/com/smartbudget/service/TransactionServiceTest.java`

**Description:** Test that creating a transaction with negative amount throws an exception.

**What**
- A `@Test` that uses `assertThrows(InvalidTransactionException.class, () -> new IncomeTransaction(1, new BigDecimal("-10"), LocalDate.now(), "bad"))` and asserts the message mentions "amount" or "greater than zero".

**Why**
- A method's contract includes its failure modes; without an exception test, a future refactor that silently accepts `-10` ships uncaught.

**Observe**
- Test passes.
- Flipping the constructor's amount check to `<= 0` -> `< 0` immediately turns this test red because zero is no longer rejected.

**Instructions:**
- Use `assertThrows(InvalidTransactionException.class, () -> { ... })`
- Try creating a transaction with amount = -10
- Verify the exception is thrown

**Acceptance Criteria:**
- [ ] Test passes
- [ ] Uses `assertThrows` to verify the exception type
- [ ] Exception message mentions "amount" or "greater than zero"
- [ ] You can explain: Why testing exceptions is important

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`assertThrows(ExceptionClass.class, () -> { code that should throw })`. The lambda body must throw — JUnit captures the exception and returns it so you can inspect the message. Use `assertThrows` exclusively for exception tests — never try/catch in a test body.

</details>

<details>
<summary><b>Hint 2 — Test method</b></summary>

```java
@Test
void negativeAmount_throwsInvalidTransactionException() {
    InvalidTransactionException ex = assertThrows(
            InvalidTransactionException.class,
            () -> new IncomeTransaction(
                    1, new BigDecimal("-10"), LocalDate.now(), "bad"));

    assertTrue(ex.getMessage().toLowerCase().contains("amount"),
            "message should mention 'amount': " + ex.getMessage());
}
```

`assertThrows` returns the caught exception so you can do further assertions on `ex.getMessage()`, `ex.getCause()`, etc.

</details>

<details>
<summary><b>Hint 3 — Full solution + 3 related cases</b></summary>

```java
@Test
void negativeAmount_throwsInvalidTransactionException() {
    InvalidTransactionException ex = assertThrows(
            InvalidTransactionException.class,
            () -> new IncomeTransaction(1, new BigDecimal("-10"),
                    LocalDate.now(), "bad"));

    String msg = ex.getMessage().toLowerCase();
    assertTrue(msg.contains("amount") || msg.contains("greater than zero"),
            "Message should mention amount or 'greater than zero', got: " + ex.getMessage());
}

@Test
void zeroAmount_throwsInvalidTransactionException() {
    assertThrows(InvalidTransactionException.class,
            () -> new ExpenseTransaction(2, BigDecimal.ZERO,
                    LocalDate.now(), "zero"));
}

@Test
void futureDate_throwsInvalidTransactionException() {
    InvalidTransactionException ex = assertThrows(
            InvalidTransactionException.class,
            () -> new IncomeTransaction(3, new BigDecimal("100"),
                    LocalDate.now().plusDays(1), "future"));
    assertTrue(ex.getMessage().toLowerCase().contains("date"));
}

@Test
void addNullTransaction_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
            () -> svc.addTransaction(null));
}
```

Why exception tests matter: a method's contract is "for input X I do Y, for input Z I throw W". The W half is just as much a guarantee as the Y half. Without an exception test, a future change that silently accepts bad input goes unnoticed until production data is corrupt.

`mvn test` should now report `Tests run: 8, Failures: 0` (or however many you've written).

</details>

---

## End-of-Day Checklist

- [ ] TransactionService refactored: HashMap storage + Stream methods
- [ ] DatabaseConnection provides live PostgreSQL connections
- [ ] TransactionDAO has insert, getAll, getByUserId, delete
- [ ] 3+ JUnit tests pass with `mvn test`
- [ ] You can explain: JDBC, PreparedStatement, SQL injection, HashMap vs ArrayList, Streams, Lambdas, JUnit, assertThrows

---

*Tomorrow (Day 5): You will switch from raw JDBC to Spring Data JPA -- same results, zero SQL code.*
