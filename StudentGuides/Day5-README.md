# Day 5 -- Spring Boot & JPA (Sprint 4)

> TICKET-F044 through TICKET-F055

---

## Overview

Today you enter the **Spring Boot** world. You will:

1. Understand Spring's IoC (Inversion of Control) and Dependency Injection
2. Study the provided `@Entity` classes and understand JPA annotations
3. Add custom query methods to the provided repositories
4. Explore the H2 in-memory database console
5. Extend the seed data

By the end of Day 5, the Spring Boot backend serves real data from the database with custom query capabilities.

---

## Key Concepts

- **Spring Boot**: An opinionated framework that auto-configures a Java web application
- **IoC Container**: Spring creates and manages objects ("beans") for you
- **Dependency Injection**: Spring passes dependencies through constructors automatically
- **JPA**: Java Persistence API -- maps Java classes to database tables
- **H2**: An in-memory database for development (data resets on restart)
- **Query Derivation**: Spring generates SQL from method names like `findByEmail`

---

## Important: What is Already Provided

The following are **already implemented and working**. Your job is to study them and extend them:

| Provided File | Your Task |
|---------------|-----------|
| `entity/User.java` | Study @Entity, @Id, @GeneratedValue annotations |
| `entity/Category.java` | Study @Column, @Table annotations |
| `entity/Transaction.java` | Study @ManyToOne, @JoinColumn relationships |
| `entity/SavingsGoal.java` | Study @Entity with multiple BigDecimal fields |
| `repository/*Repository.java` | Add custom query methods |
| `application.properties` | Verify settings, understand each property |
| `data.sql` | Extend with your own data |

---

## Tickets

### TICKET-F044: Spring Boot Introduction
**File:** N/A (study existing code)

**Description:** Understand how Spring Boot auto-configures the application.

**What**
- A running Spring Boot app on port 8080 and an explanation of what `@SpringBootApplication` does and the three annotations it combines.

**Why**
- Every later ticket assumes the backend boots cleanly; the meta-annotation is the single line that wires the whole web + data stack together.

**Observe**
- `./mvnw spring-boot:run` prints `Started SmartBudgetApplication in X seconds` and `http://localhost:8080/api/categories` returns a JSON list.

**Instructions:**
1. Run `mvn spring-boot:run` from the `backend/` directory
2. Watch the startup logs -- notice:
   - "Tomcat started on port 8080"
   - "H2 console available at /h2-console"
   - "Creating table: USERS, CATEGORIES..."
3. Open `SmartBudgetApplication.java` -- notice the `@SpringBootApplication` annotation
4. Understand: this single annotation enables component scanning, auto-configuration, and property loading

**Acceptance Criteria:**
- [ ] App starts without errors
- [ ] You can explain what `@SpringBootApplication` does
- [ ] You can identify the 3 annotations it combines: `@Configuration`, `@EnableAutoConfiguration`, `@ComponentScan`

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

This is a "read + run" ticket — no code to write. `cd backend && ./mvnw spring-boot:run`. Read the startup banner. The magic that wires everything together is `@SpringBootApplication` on the `SmartBudgetApplication` class. That's three annotations rolled into one.

</details>

<details>
<summary><b>Hint 2 — What to look for in logs</b></summary>

```bash
cd backend
./mvnw spring-boot:run
```

Watch for these lines in order:

```
Starting SmartBudgetApplication using Java 25
Bootstrapping Spring Data JPA repositories...
HHH000204: Processing PersistenceUnitInfo [name: default]
H2 console available at '/h2-console'. ...
Tomcat started on port 8080 (http) with context path ''
Started SmartBudgetApplication in 2.3 seconds
```

Then hit `http://localhost:8080/api/categories` in a browser — JSON appears.

`@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan` (rooted at this class's package). Cmd-click into the source to confirm.

</details>

<details>
<summary><b>Hint 3 — Walkthrough + endpoints</b></summary>

```bash
cd backend
./mvnw spring-boot:run
# wait for "Started SmartBudgetApplication in X seconds"
```

In a second terminal:

```bash
curl http://localhost:8080/api/categories         # JSON list
curl http://localhost:8080/api/users              # JSON list
curl http://localhost:8080/api/transactions       # 15 seeded txns
```

The class enabling it all:

```java
@SpringBootApplication               // ↓ expands to:
//   @Configuration                  // <-- registers Java-based config beans
//   @EnableAutoConfiguration        // <-- wires beans Spring sees on classpath
//   @ComponentScan                  // <-- discovers @Component / @Service / @RestController
public class SmartBudgetApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartBudgetApplication.class, args);
    }
}
```

What each one does:
- `@Configuration` — class can declare `@Bean`-producing methods.
- `@EnableAutoConfiguration` — Spring inspects the classpath (Spring Web, JPA, H2, ...) and configures sensible defaults.
- `@ComponentScan` — recursively finds `@RestController`, `@Service`, `@Repository`, `@Component` from this package downward and registers them as beans.

This is Spring's "convention over configuration" tax: you get a fully-wired web + data app with one annotation, paid for with some magic you have to debug if you stray off the happy path.

</details>

---

### TICKET-F045: Verify Application Properties
**File:** `backend/src/main/resources/application.properties`

**Description:** Understand each configuration property in the pre-configured file.

**What**
- A working H2 console session against `jdbc:h2:mem:smartbudget` plus a one-line explanation of every property in `application.properties`.

**Why**
- These properties control where data lives, when the schema is built, and whether `data.sql` runs — misreading any one of them produces silent boot failures later.

**Observe**
- The H2 console connects, the left tree shows USERS / CATEGORIES / TRANSACTIONS / SAVINGS_GOALS, and `SELECT COUNT(*) FROM TRANSACTIONS` returns 15.

**Instructions:**
1. Read `application.properties` and understand each property:
   - `spring.datasource.url` -- where the H2 database lives
   - `spring.h2.console.enabled=true` -- enables the web-based DB browser
   - `spring.jpa.hibernate.ddl-auto=create-drop` -- recreates tables on each restart
   - `spring.sql.init.mode=always` -- runs data.sql on startup
   - `spring.jpa.defer-datasource-initialization=true` -- ensures tables exist before data.sql runs
2. Open H2 console at http://localhost:8080/h2-console
3. Use JDBC URL: `jdbc:h2:mem:smartbudget`, Username: `sa`, Password: (blank)
4. Run `SELECT * FROM TRANSACTIONS` to see seed data

**Acceptance Criteria:**
- [ ] H2 console opens and connects successfully
- [ ] You can see all 4 tables in the H2 console
- [ ] `SELECT COUNT(*) FROM TRANSACTIONS` returns 15
- [ ] You can explain each property in application.properties

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Run app → open `http://localhost:8080/h2-console` in a browser → fill the form with **JDBC URL: `jdbc:h2:mem:smartbudget`**, **User: `sa`**, **Password: (blank)** → Connect. The trick that catches everyone: the default JDBC URL on the form is `jdbc:h2:~/test` — overwrite it with our URL.

</details>

<details>
<summary><b>Hint 2 — H2 console login + table check</b></summary>

H2 console form values:
```
Saved Settings:  Generic H2 (Embedded)
Driver Class:    org.h2.Driver
JDBC URL:        jdbc:h2:mem:smartbudget        <-- replace the default
User Name:       sa
Password:        (leave blank)
```

Click Connect. In the left tree you should see USERS, CATEGORIES, TRANSACTIONS, SAVINGS_GOALS. In the SQL editor:

```sql
SELECT COUNT(*) FROM TRANSACTIONS;      -- 15
SELECT * FROM USERS;
SELECT u.NAME, t.AMOUNT, t.DESCRIPTION
FROM TRANSACTIONS t
JOIN USERS u ON t.USER_ID = u.USER_ID;
```

</details>

<details>
<summary><b>Hint 3 — Property-by-property explainer</b></summary>

| Property | Effect | Why this value |
|---|---|---|
| `spring.datasource.url=jdbc:h2:mem:smartbudget` | Use in-memory H2 with database name `smartbudget` | No install, no setup. Resets on app restart. |
| `spring.datasource.driver-class-name=org.h2.Driver` | Use the H2 JDBC driver | Required when not relying on autodetection. |
| `spring.h2.console.enabled=true` | Exposes the web UI at `/h2-console` | Lets you browse data without external tools. |
| `spring.h2.console.path=/h2-console` | URL path | Convention; matches what we tell students. |
| `spring.jpa.hibernate.ddl-auto=create-drop` | Hibernate creates tables on startup, drops on shutdown | Convenient for dev. **Never use in production** — use Flyway/Liquibase. |
| `spring.jpa.show-sql=true` | Logs every SQL Hibernate executes | Lets you see what query derivation produces. |
| `spring.sql.init.mode=always` | Runs `data.sql` on startup | Loads seed data into the empty schema. |
| `spring.jpa.defer-datasource-initialization=true` | Waits for `create-drop` to finish before running `data.sql` | Without this, `data.sql` tries to INSERT into tables that don't yet exist. |
| `server.port=8080` | HTTP port | Default; overridable with `--server.port=9090`. |

Run from H2 console:

```sql
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'PUBLIC';

SELECT COUNT(*) FROM TRANSACTIONS;       -- 15
SELECT COUNT(*) FROM USERS;              -- 5
SELECT COUNT(*) FROM CATEGORIES;         -- 5
SELECT COUNT(*) FROM SAVINGS_GOALS;      -- 4
```

</details>

---

### TICKET-F046: Study Entity -- User
**File:** `backend/src/main/java/com/smartbudget/entity/User.java` (provided, study only)

**Description:** Understand JPA annotations by studying the provided User entity.

**What**
- An explanation of `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, and `@Column` and how `entity/User.java` differs from your Day 2 `model/User.java` POJO.

**Why**
- Every repository, service, and controller from here on operates on JPA entities — you cannot debug Hibernate output without first understanding what each annotation tells it to do.

**Observe**
- You can point at each annotation in `entity/User.java` and state what DDL or runtime behaviour it produces, and you can name the no-arg-constructor requirement JPA imposes.

**Instructions:**
1. Open `entity/User.java` and identify:
   - `@Entity` -- marks this class as a JPA entity (mapped to a database table)
   - `@Table(name = "users")` -- specifies the table name
   - `@Id` -- marks the primary key field
   - `@GeneratedValue(strategy = GenerationType.IDENTITY)` -- auto-increment
   - `@Column` -- maps fields to specific columns
2. Compare with your `model/User.java` POJO from Day 2 -- notice the annotations are the only difference

**Acceptance Criteria:**
- [ ] You can explain what each annotation does
- [ ] You understand the difference between your POJO (model/) and the JPA entity (entity/)
- [ ] You know that JPA requires a no-arg constructor

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Pure read-and-understand. Open `entity/User.java`, find each `@`-prefixed annotation, and write down what each one does in plain English. Compare line-by-line with your `model/User.java` from Day 2 — the *only* difference should be annotations + a no-arg constructor.

</details>

<details>
<summary><b>Hint 2 — Annotation cheat-sheet</b></summary>

| Annotation | Meaning |
|---|---|
| `@Entity` | "This class is a JPA-managed object backed by a DB table." |
| `@Table(name = "users")` | DB table name (otherwise JPA defaults to lowercased class name). |
| `@Id` | This field is the primary key. |
| `@GeneratedValue(strategy = IDENTITY)` | DB auto-generates the ID on insert (matches `SERIAL` in Postgres). |
| `@Column(name = "user_id", nullable = false)` | Map field to a specific column, enforce NOT NULL. |
| `@Column(unique = true)` | Add UNIQUE constraint when DDL is generated. |

JPA also requires:
- a `public` or `protected` **no-arg** constructor (so the framework can `newInstance()` then setter-inject fields),
- the class must NOT be `final`,
- fields can be `private` with getters/setters.

</details>

<details>
<summary><b>Hint 3 — Annotated User.java for reference</b></summary>

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public User() { }       // required by JPA

    // getters, setters, optional toString
}
```

POJO vs Entity:

| `model/User.java` (Day 2) | `entity/User.java` (today) |
|---|---|
| Plain Java class | Same class + JPA annotations |
| Lives in memory only | Round-trips to DB via Hibernate |
| You instantiate with `new` | Loaded by `userRepository.findById(1L)` |
| Field types: `int` is fine | Field types: prefer `Long` (can be null until persisted) |
| Mutable, no constraints | DB constraints enforced |

That's why entity classes have a no-arg constructor — Hibernate uses reflection to call `new User()` then `setUserId(...)`, `setName(...)`, etc.

</details>

---

### TICKET-F047: Study Entity -- Category
**File:** `backend/src/main/java/com/smartbudget/entity/Category.java` (provided, study only)

**Description:** Study the Category entity's annotations and column mappings.

**What**
- An explanation of how `@Column(nullable = false)` becomes DDL `NOT NULL` and how JPA's naming strategy converts Java `camelCase` fields to DB `snake_case` columns.

**Why**
- Knowing the mapping rules lets you predict the generated DDL and read Hibernate's `show-sql` output instead of guessing why a column name doesn't exist.

**Observe**
- In the H2 console, `SHOW COLUMNS FROM CATEGORIES` lists `category_id`, `name`, `type` with the correct `NOT NULL` flags matching the annotations.

**Acceptance Criteria:**
- [ ] You can explain `@Column(nullable = false)` vs SQL NOT NULL
- [ ] You understand how JPA maps Java field names to database column names

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`@Column(nullable = false)` only affects Hibernate's DDL generation — it adds `NOT NULL` when the table is created. It does NOT validate at runtime if the database already exists. For runtime validation use `@NotNull` from `jakarta.validation`.

</details>

<details>
<summary><b>Hint 2 — Mapping rules</b></summary>

Without `@Column`, JPA defaults to **snake_case** of the Java field name (depending on the naming strategy):

| Field | Default column |
|---|---|
| `private String name` | `name` |
| `private LocalDate txnDate` | `txn_date` (with default `SpringPhysicalNamingStrategy`) |
| `private Long categoryId` | `category_id` |

Use `@Column(name = "...")` whenever the convention doesn't match your DB schema (e.g., legacy tables).

</details>

<details>
<summary><b>Hint 3 — Full study + DDL trace</b></summary>

For `Category`:

```java
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 10)
    private String type;          // 'INCOME' or 'EXPENSE'

    public Category() { }
    // getters / setters
}
```

What Hibernate emits at startup (with `show-sql=true`):

```
Hibernate: create table categories (
    category_id bigint generated by default as identity,
    name varchar(50) not null,
    type varchar(10) not null,
    primary key (category_id)
)
```

`@Column(nullable = false)` translated to `not null` in the DDL. Note that JPA does not auto-emit a `CHECK (type IN ('INCOME','EXPENSE'))` — you'd need `@Check(constraints = "type IN ('INCOME','EXPENSE')")` (Hibernate-specific) or write it in `schema.sql`. Day 1's raw SQL caught this; JPA's annotation surface is intentionally narrower.

`@Column(nullable = false)` vs SQL `NOT NULL`:
- They produce the same DDL when Hibernate generates the schema.
- `nullable = false` does not enforce anything at the Java layer — pass `null` and the row hits the DB constraint instead. For pre-DB validation, layer on `@NotNull` + `@Valid` (Day 6).

</details>

---

### TICKET-F048: Study Entity -- Transaction
**File:** `backend/src/main/java/com/smartbudget/entity/Transaction.java` (provided, study only)

**Description:** Study the Transaction entity, focusing on relationships.

**What**
- An explanation of `@ManyToOne` and `@JoinColumn` and how the `private User user` field replaces the raw `user_id` FK + manual JOIN you wrote in Day 1.

**Why**
- Relationships are where JPA earns its keep — and where N+1 query bugs are born — so understanding the mapping is a prerequisite to writing performant services on Day 6.

**Observe**
- In the H2 console, `SHOW COLUMNS FROM TRANSACTIONS` shows `user_id` and `category_id` as FK columns, and `show-sql=true` logs a `SELECT ... FROM users WHERE user_id = ?` when `txn.getUser()` is accessed.

**Instructions:**
1. Find the `@ManyToOne` annotation -- this creates a relationship
2. Find `@JoinColumn(name = "user_id")` -- this specifies the foreign key column
3. Understand: `@ManyToOne` means "many transactions belong to one user"
4. Compare with the FOREIGN KEY constraint you wrote in Day 1 SQL

**Acceptance Criteria:**
- [ ] You can explain `@ManyToOne` in plain English
- [ ] You know what `@JoinColumn` maps to in the database
- [ ] You understand how JPA relationships replace raw FOREIGN KEY + JOIN queries

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`@ManyToOne` on a field that holds the *parent* object. Example: many transactions belong to one user, so `Transaction` has a `private User user;` field annotated `@ManyToOne`. `@JoinColumn(name = "user_id")` tells JPA which DB column holds the foreign key.

</details>

<details>
<summary><b>Hint 2 — Code shape + what JPA does</b></summary>

```java
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "txn_id")
    private Long txnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                  // ← whole User object, not just an id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "txn_date")
    private LocalDate txnDate;

    // ...
}
```

When you call `txn.getUser().getName()`, JPA quietly issues `SELECT * FROM users WHERE user_id = ?` — that's the JOIN you wrote by hand in Day 1.

</details>

<details>
<summary><b>Hint 3 — Full study + N+1 warning</b></summary>

```java
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "txn_id")
    private Long txnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "txn_date", nullable = false)
    private LocalDate txnDate;

    private String description;

    @Column(length = 10, nullable = false)
    private String type;

    public Transaction() { }
    // getters / setters
}
```

JPA's `@ManyToOne` replaces:
1. The SQL `FOREIGN KEY (user_id) REFERENCES users(user_id)` (in DDL gen).
2. The `JOIN users u ON t.user_id = u.user_id` you'd write in Day 1.
3. The manual `rs.getInt("user_id"); userDao.findById(...)` you'd write in Day 4.

⚠️ **N+1 trap.** With `FetchType.EAGER` (the default for `@ManyToOne`), Hibernate fires a *second* SELECT per transaction to load the user — fetch 100 txns and you do 101 queries. We use `LAZY` and fetch joins (`@EntityGraph` or `JOIN FETCH`) on the slow paths. This is the #1 JPA performance pitfall in production code.

Verify in the H2 console:

```sql
DESCRIBE TRANSACTIONS;
-- USER_ID column exists; ALTER ... ADD CONSTRAINT FOREIGN KEY visible
```

</details>

---

### TICKET-F049: Study Entity -- SavingsGoal
**File:** `backend/src/main/java/com/smartbudget/entity/SavingsGoal.java` (provided, study only)

**Description:** Study the SavingsGoal entity.

**What**
- An explanation of how `BigDecimal` plus `@Column(precision = 12, scale = 2)` maps to `NUMERIC(12,2)` in the database, and identification of every JPA annotation on `SavingsGoal`.

**Why**
- Money columns must be exact — using `double`/`float` corrupts totals — and Day 6's savings-goal service depends on these precision rules.

**Observe**
- In the H2 console, `SHOW COLUMNS FROM SAVINGS_GOALS` shows `target_amount` and `current_amount` as `DECIMAL(12,2) NOT NULL`.

**Acceptance Criteria:**
- [ ] You can identify all JPA annotations used
- [ ] You understand how `BigDecimal` fields are stored in the database

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Same pattern as the other entities. Two `BigDecimal` fields (`targetAmount`, `currentAmount`) — both annotated `@Column(precision = 12, scale = 2)` which maps to `NUMERIC(12,2)` in PostgreSQL and `DECIMAL(12,2)` in H2.

</details>

<details>
<summary><b>Hint 2 — Storage detail</b></summary>

```java
@Column(name = "target_amount",  nullable = false, precision = 12, scale = 2)
private BigDecimal targetAmount;          // up to 9_999_999_999.99

@Column(name = "current_amount", nullable = false, precision = 12, scale = 2)
private BigDecimal currentAmount = BigDecimal.ZERO;
```

- `precision` = total significant digits (left + right of decimal).
- `scale` = digits to the right of the decimal point.
- Default field value (`= BigDecimal.ZERO`) doubles as a Java-side default *and* matches the DB `DEFAULT 0`.

</details>

<details>
<summary><b>Hint 3 — Full study + DDL trace</b></summary>

```java
@Entity
@Table(name = "savings_goals")
public class SavingsGoal {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long goalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "goal_name", nullable = false, length = 100)
    private String goalName;

    @Column(name = "target_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column
    private LocalDate deadline;

    public SavingsGoal() { }
    // getters / setters
}
```

Generated DDL (`show-sql=true`):

```
create table savings_goals (
    goal_id         bigint generated by default as identity,
    user_id         bigint not null,
    goal_name       varchar(100) not null,
    target_amount   numeric(12,2) not null,
    current_amount  numeric(12,2) not null,
    deadline        date,
    primary key (goal_id)
)
alter table savings_goals
    add constraint FK... foreign key (user_id) references users
```

`BigDecimal` ↔ SQL `NUMERIC(12,2)` is *exact*. Don't use `double` / `float` for money columns — same rule as Day 2.

</details>

---

### TICKET-F050: Add Custom Queries to UserRepository
**File:** `backend/src/main/java/com/smartbudget/repository/UserRepository.java`

**Description:** Add custom query methods using Spring Data query derivation.

**What**
- Two derived methods on `UserRepository`: `Optional<User> findByEmail(String email)` and `boolean existsByEmail(String email)`.

**Why**
- Login, signup, and uniqueness checks in Day 7's auth layer all hit these two methods — without them the controller cannot resolve a user from an email.

**Observe**
- App restarts cleanly; `curl http://localhost:8080/api/users/by-email/alice@bank.com` returns the user JSON, and an unknown email returns 404 (not a 500).

**Instructions (follow the TODOs in the file):**
1. Add `Optional<User> findByEmail(String email)` -- Spring generates: `WHERE email = ?`
2. Add `boolean existsByEmail(String email)` -- Spring generates: `SELECT EXISTS(WHERE email = ?)`

**Acceptance Criteria:**
- [ ] `findByEmail("alice@db.com")` returns the user
- [ ] `findByEmail("nobody@db.com")` returns `Optional.empty()`
- [ ] `existsByEmail("alice@db.com")` returns `true`
- [ ] `existsByEmail("nobody@db.com")` returns `false`
- [ ] App restarts without errors after adding the methods

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Just method *signatures* — no implementation. Spring Data reads the method name (`findByEmail`) at startup and generates the SQL (`SELECT * FROM users WHERE email = ?`) for you. Wrap the return in `Optional` so callers handle the "not found" case explicitly.

</details>

<details>
<summary><b>Hint 2 — Repository file</b></summary>

```java
package com.smartbudget.repository;

import com.smartbudget.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
```

That's it — no class body, no SQL. The `interface` extends `JpaRepository<User, Long>` which already gives you `findAll`, `findById`, `save`, `delete`, etc.

</details>

<details>
<summary><b>Hint 3 — Full solution + naming rules</b></summary>

```java
package com.smartbudget.repository;

import com.smartbudget.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /** WHERE email = ? */
    Optional<User> findByEmail(String email);

    /** SELECT EXISTS(SELECT 1 FROM users WHERE email = ?) */
    boolean existsByEmail(String email);

    /** Bonus: WHERE LOWER(name) LIKE LOWER('%search%') */
    List<User> findByNameContainingIgnoreCase(String search);
}
```

Smoke test from `H2 console` after a restart:

```sql
-- Spring will run this when findByEmail("alice@bank.com") is called
SELECT * FROM USERS WHERE EMAIL = 'alice@bank.com';
```

Or via the existing UserController (already provided):
```bash
curl http://localhost:8080/api/users/by-email/alice@bank.com
```

**Query-derivation naming rules (Spring Data):**
- `findBy<Field>` → `WHERE field = ?`
- `findBy<Field>And<Other>` → `WHERE field = ? AND other = ?`
- `findBy<Field>Like` → `WHERE field LIKE ?`
- `findBy<Field>Containing` → `WHERE field LIKE %?%`
- `findBy<Field>IgnoreCase` → `WHERE LOWER(field) = LOWER(?)`
- `findBy<Field>OrderBy<Other>Desc` → adds `ORDER BY other DESC`
- `existsBy<Field>` → returns boolean
- `countBy<Field>` → returns long

If you misspell `findByEmial`, Spring fails fast at startup with `No property emial found for User` — that's the safety net.

</details>

---

### TICKET-F051: Add Custom Queries to TransactionRepository
**File:** `backend/src/main/java/com/smartbudget/repository/TransactionRepository.java`

**Description:** Add 4 custom query methods to the transaction repository.

**What**
- Four methods on `TransactionRepository`: `findByUser_UserIdOrderByTxnDateDesc`, `findByType`, `findByTxnDateBetween`, and a `@Query`-driven `sumByUserAndType` returning a non-null `BigDecimal`.

**Why**
- These cover the four read patterns the dashboard needs — per-user feed, type filter, date-range filter, and aggregate — and the `COALESCE` in the JPQL is what stops a NullPointerException on users with zero transactions.

**Observe**
- `curl http://localhost:8080/api/transactions/user/1` returns Alice's transactions newest-first, and `sumByUserAndType(999L, "INCOME")` returns `0` (not `null`) when called via a test or service.

**Instructions (follow the 4 TODOs in the file):**

1. **findByUser_UserIdOrderByTxnDateDesc(Long userId)** -- finds all transactions for a user, sorted newest first
2. **findByType(String type)** -- finds all INCOME or EXPENSE transactions
3. **findByTxnDateBetween(LocalDate from, LocalDate to)** -- finds transactions in a date range
4. **sumByUserAndType(Long userId, String type)** -- uses `@Query` with JPQL to calculate SUM with COALESCE

**Acceptance Criteria:**
- [ ] All 4 methods compile and the app restarts
- [ ] GET /api/transactions/user/1 returns user 1's transactions (uses method 1)
- [ ] Method 2 can filter by INCOME or EXPENSE
- [ ] Method 3 returns transactions within a date range
- [ ] Method 4 returns a BigDecimal sum (not null -- COALESCE handles empty results)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Three are pure name-derived; the fourth (`sumByUserAndType`) needs `@Query` with JPQL because aggregations aren't derivable from method names. The underscore in `findByUser_UserId` traverses the relationship: `User.userId` of the `user` field.

</details>

<details>
<summary><b>Hint 2 — Method signatures</b></summary>

```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser_UserIdOrderByTxnDateDesc(Long userId);

    List<Transaction> findByType(String type);

    List<Transaction> findByTxnDateBetween(LocalDate from, LocalDate to);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.userId = :userId AND t.type = :type")
    BigDecimal sumByUserAndType(@Param("userId") Long userId,
                                @Param("type")   String type);
}
```

JPQL is "SQL for entities": you query `Transaction`, not `transactions`; you traverse `t.user.userId`, not `t.user_id`. `COALESCE(..., 0)` makes the result `0` instead of `null` when there are no matching rows.

</details>

<details>
<summary><b>Hint 3 — Full solution + test</b></summary>

```java
package com.smartbudget.repository;

import com.smartbudget.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /** 1) All transactions for a user, newest first. */
    List<Transaction> findByUser_UserIdOrderByTxnDateDesc(Long userId);

    /** 2) Filter by 'INCOME' or 'EXPENSE'. */
    List<Transaction> findByType(String type);

    /** 3) Inclusive date range. */
    List<Transaction> findByTxnDateBetween(LocalDate from, LocalDate to);

    /** 4) Sum amounts for a user + type. COALESCE keeps result non-null. */
    @Query("""
           SELECT COALESCE(SUM(t.amount), 0)
           FROM   Transaction t
           WHERE  t.user.userId = :userId
             AND  t.type        = :type
           """)
    BigDecimal sumByUserAndType(@Param("userId") Long userId,
                                @Param("type")   String type);
}
```

Test endpoints (existing controllers wire to these):

```bash
curl http://localhost:8080/api/transactions/user/1
# returns all of Alice's transactions, newest first

curl http://localhost:8080/api/transactions?type=EXPENSE
# (if your TransactionController exposes a query param using findByType)
```

Quick JUnit:

```java
@SpringBootTest
class TransactionRepositoryTest {
    @Autowired TransactionRepository repo;

    @Test void sumByUserAndType_returnsZeroWhenEmpty() {
        BigDecimal total = repo.sumByUserAndType(999L, "INCOME");
        assertEquals(0, total.compareTo(BigDecimal.ZERO));   // 0, not null
    }
}
```

Why `findByUser_UserId` (underscore)? It tells Spring Data: navigate the `user` relationship, then match on its `userId` field. Without the underscore, Spring would look for a `user_userId` field directly on `Transaction` and fail.

</details>

---

### TICKET-F052: Add Custom Query to CategoryRepository
**File:** `backend/src/main/java/com/smartbudget/repository/CategoryRepository.java`

**Description:** Add a query method to find categories by type.

**What**
- One derived method on `CategoryRepository`: `List<Category> findByType(String type)`.

**Why**
- The category picker on the transaction form needs to split INCOME vs EXPENSE buckets — this is the query that powers it.

**Observe**
- App restarts cleanly; `findByType("INCOME")` returns only income categories and `findByType("EXPENSE")` returns only expense categories with no overlap.

**Instructions (follow the TODO in the file):**
- Add `List<Category> findByType(String type)` -- returns all INCOME or all EXPENSE categories

**Acceptance Criteria:**
- [ ] `findByType("INCOME")` returns only income categories
- [ ] `findByType("EXPENSE")` returns only expense categories
- [ ] App restarts without errors

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

One-liner — same pattern as `UserRepository.findByEmail`. Returns a `List<Category>` (not `Optional`) because we expect multiple matches.

</details>

<details>
<summary><b>Hint 2 — Interface</b></summary>

```java
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(String type);
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution + test</b></summary>

```java
package com.smartbudget.repository;

import com.smartbudget.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /** All categories of a given type ('INCOME' or 'EXPENSE'). */
    List<Category> findByType(String type);
}
```

Quick test:

```bash
# If the CategoryController exposes filtering:
curl "http://localhost:8080/api/categories?type=INCOME"

# Or in a JUnit:
@Autowired CategoryRepository repo;

@Test void findByType_filtersCorrectly() {
    assertEquals(2, repo.findByType("INCOME").size());
    assertEquals(3, repo.findByType("EXPENSE").size());
    assertTrue(repo.findByType("BOGUS").isEmpty());
}
```

</details>

---

### TICKET-F053: Extend Seed Data
**File:** `backend/src/main/resources/data.sql`

**Description:** Add more sample data to the existing seed file.

**What**
- 10 additional `INSERT INTO transactions` rows in `data.sql` spanning multiple users, categories, dates, and both INCOME and EXPENSE types.

**Why**
- Aggregations and date-range filters (Days 6-8) only look credible with a wider sample; 15 rows is too thin to see month-over-month patterns.

**Observe**
- App restarts cleanly and `curl -s http://localhost:8080/api/transactions | jq length` returns 25 or more.

**Instructions:**
1. Open `data.sql` and study the existing INSERT statements
2. Add 10 more transactions with various users, categories, and dates
3. Make sure amounts are realistic and dates span multiple months

**Acceptance Criteria:**
- [ ] App restarts without errors (no SQL syntax issues)
- [ ] GET /api/transactions returns 25+ transactions (15 original + 10 new)
- [ ] New data appears in the H2 console
- [ ] Mix of INCOME and EXPENSE types in the new data

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Open `backend/src/main/resources/data.sql`. Append 10 more `INSERT INTO transactions (...) VALUES (...)`. Use `user_id` and `category_id` values that exist in the seed. Restart Spring Boot — `spring.jpa.hibernate.ddl-auto=create-drop` rebuilds the schema and `data.sql` re-runs.

</details>

<details>
<summary><b>Hint 2 — Insert pattern</b></summary>

```sql
-- Existing rows live above this; add yours below.
INSERT INTO transactions (user_id, category_id, amount, txn_date, description, type) VALUES
    (1, 3,   12.50, '2026-04-02', 'Lunch',           'EXPENSE'),
    (1, 4,   18.00, '2026-04-05', 'Tube fare',       'EXPENSE'),
    (2, 1, 4200.00, '2026-04-01', 'April salary',    'INCOME'),
    (3, 2,  650.00, '2026-04-09', 'Freelance gig',   'INCOME'),
    (3, 3,   80.00, '2026-04-12', 'Dinner out',      'EXPENSE'),
    (4, 5,  130.00, '2026-04-15', 'Electric bill',   'EXPENSE'),
    (5, 1, 3100.00, '2026-04-01', 'April salary',    'INCOME'),
    (5, 5,   89.00, '2026-04-18', 'Internet',        'EXPENSE'),
    (1, 1, 3500.00, '2026-04-01', 'April salary',    'INCOME'),
    (4, 3,   45.00, '2026-04-22', 'Groceries',       'EXPENSE');
```

Restart `./mvnw spring-boot:run`, then `curl http://localhost:8080/api/transactions | jq length` → `25`.

</details>

<details>
<summary><b>Hint 3 — Verification</b></summary>

After restart:

```bash
curl -s http://localhost:8080/api/transactions | jq 'length'
# 25
```

Or in the H2 console:

```sql
SELECT COUNT(*) FROM TRANSACTIONS;        -- 25
SELECT TYPE, COUNT(*), SUM(AMOUNT)
FROM TRANSACTIONS GROUP BY TYPE;
-- INCOME  | 11 | 31900.00
-- EXPENSE | 14 |   934.10
```

Common breakages:
- Trailing semicolon missing → script fails silently on Spring 3+.
- Reused PK like `INSERT ... (txn_id, ...) VALUES (1, ...)` → unique constraint violation. Don't supply `txn_id`; let IDENTITY generate it.
- Reference a non-existent `user_id` → foreign-key violation.

</details>

---

### TICKET-F054: Spring IoC Exploration
**File:** N/A (study and observe)

**Description:** Understand Spring's Inversion of Control by observing bean creation.

**What**
- A plain-English explanation of IoC plus evidence in the logs that Spring instantiated your repositories and controllers as singleton beans.

**Why**
- Knowing Spring owns object construction is the mental model behind every later annotation (`@Autowired`, `@Transactional`, `@MockBean`) — without it the framework feels like magic.

**Observe**
- With DEBUG logging on, the startup log contains `Creating shared instance of singleton bean 'transactionRepository'` (and similar lines for each `@Repository`, `@Service`, `@RestController`).

**Instructions:**
1. Add `logging.level.org.springframework=DEBUG` to application.properties (temporarily)
2. Restart the app and search the logs for "Creating bean"
3. Find your repositories being created as beans
4. Remove the debug logging when done

**Acceptance Criteria:**
- [ ] You can find repository beans in the Spring logs
- [ ] You can explain: "IoC means Spring creates objects, not you"
- [ ] You understand why you never write `new TransactionRepository()`

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Temporarily flip Spring to DEBUG, restart, scroll the logs for `Creating shared instance of singleton bean 'transactionRepository'`. Each repository, controller, and service shows up there. Remove the debug line afterwards or your logs become unreadable.

</details>

<details>
<summary><b>Hint 2 — How to switch + what to search</b></summary>

`application.properties` (add temporarily):

```
logging.level.org.springframework.beans=DEBUG
logging.level.org.springframework.context=DEBUG
```

Restart and grep the logs:

```bash
./mvnw spring-boot:run 2>&1 | grep "Creating shared instance"
```

Expect lines like:

```
Creating shared instance of singleton bean 'transactionRepository'
Creating shared instance of singleton bean 'userRepository'
Creating shared instance of singleton bean 'transactionController'
Creating shared instance of singleton bean 'savingsGoalService'
```

</details>

<details>
<summary><b>Hint 3 — IoC explained + injection example</b></summary>

```java
// What you DON'T do (manual):
TransactionRepository repo = new TransactionRepositoryImpl(
    new EntityManagerImpl(
        new DataSourceImpl("jdbc:h2:mem:smartbudget", "sa", "")));
// ...repeat above for every controller, service, transaction manager, etc.

// What Spring does for you:
@RestController
public class TransactionController {

    private final TransactionRepository repo;

    public TransactionController(TransactionRepository repo) {
        this.repo = repo;        // Spring passes the singleton bean in
    }
}
```

**Inversion of Control (IoC):** the *flow* of object creation is inverted — Spring (the framework) creates and wires objects; you only *declare* what you need. The mechanism is **dependency injection (DI)**: constructor parameters automatically receive the right beans.

Why this is a win:
- You can swap the real `TransactionRepository` for a mock in tests with one annotation.
- Lifecycle (`@PostConstruct`, `@PreDestroy`), transactions (`@Transactional`), security, AOP — Spring layers these on transparently.
- No manual wiring graphs to maintain.

Don't forget to **remove the DEBUG lines** — production logs would balloon by an order of magnitude.

</details>

---

### TICKET-F055: Compare JDBC vs JPA
**File:** N/A (reflection exercise)

**Description:** Compare your Day 4 JDBC code with today's JPA approach.

**What**
- A written comparison naming at least 5 things JPA handles automatically that you coded by hand in JDBC (connection lifecycle, SQL generation, parameter binding, result-set mapping, transaction management) and at least one case where raw JDBC still wins.

**Why**
- Tomorrow's service layer assumes JPA defaults; knowing where those defaults break down stops you from reaching for `@Query(nativeQuery = true)` whenever a query feels hard.

**Observe**
- You can recite the line-count delta (`getByUserId` is ~18 JDBC lines vs 1 JPA line) and name a scenario — bulk inserts, window-function reports, perf hot paths — where you'd drop back to JDBC.

**Instructions:**
1. Open `TransactionDAO.java` (Day 4) side by side with `TransactionRepository.java` (Day 5)
2. Count the lines of code difference
3. List what JPA does automatically that you coded manually with JDBC

**Acceptance Criteria:**
- [ ] You can list 5 things JPA does automatically (connection management, SQL generation, result mapping, transaction handling, parameter binding)
- [ ] You understand when raw JDBC might still be preferred (complex queries, performance tuning)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Open `dao/TransactionDAO.java` (Day 4 JDBC) and `repository/TransactionRepository.java` (today) side by side. Count: how many lines does JDBC need for `getByUserId`? How many does JPA need? The ratio is roughly 30:1.

</details>

<details>
<summary><b>Hint 2 — Side-by-side</b></summary>

JDBC (Day 4):

```java
public List<Transaction> getByUserId(int userId) throws SQLException {
    List<Transaction> list = new ArrayList<>();
    try (Connection c = DatabaseConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(
             "SELECT * FROM transactions WHERE user_id = ? ORDER BY txn_date DESC")) {
        ps.setInt(1, userId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Transaction(
                    rs.getInt("txn_id"), rs.getInt("user_id"),
                    rs.getInt("category_id"), rs.getBigDecimal("amount"),
                    rs.getDate("txn_date").toLocalDate(),
                    rs.getString("description"), rs.getString("type")));
            }
        }
    }
    return list;
}
```

JPA (today):

```java
List<Transaction> findByUser_UserIdOrderByTxnDateDesc(Long userId);
```

1 line vs ~18.

</details>

<details>
<summary><b>Hint 3 — Trade-off matrix</b></summary>

What JPA does for you that you wrote by hand in JDBC:
1. **Connection lifecycle** — open, close, reuse, pool (HikariCP auto-configured).
2. **SQL generation** — method name → SELECT, INSERT, UPDATE, DELETE.
3. **Parameter binding** — type-safe `setInt`/`setString` no longer your problem.
4. **Result-set mapping** — column → field copy via reflection.
5. **Transaction management** — declarative `@Transactional`; auto-rollback on RuntimeException.
6. **Caching** — first-level cache per Session; optional second-level cache.
7. **Schema generation** — `ddl-auto` creates DDL from `@Entity` annotations.

When raw JDBC (or JdbcTemplate, jOOQ, MyBatis) is still better:
- **Complex analytical queries**: window functions, CTEs, hand-tuned hints. JPQL chokes; you fall back to `@Query(nativeQuery = true)` anyway.
- **Bulk operations**: 100k inserts via JPA fires 100k events; JDBC batch is 10× faster.
- **Read-only reporting**: full entity-graph hydration is wasted work; project straight to a DTO.
- **Performance-critical hot paths**: every JPA call has overhead from change-tracking.

Heuristic: **CRUD on a few rows → JPA; analytics or bulk → drop down a layer.**

</details>

---

## End-of-Day Checklist

- [ ] Custom queries added to all 3 repositories
- [ ] H2 console explored with SELECT queries
- [ ] Seed data extended with 10+ new transactions
- [ ] You can explain: @Entity, @ManyToOne, @Query, query derivation, IoC, DI
- [ ] You understand the difference between JDBC (Day 4) and JPA (Day 5)

---

*Tomorrow (Day 6): You will build the service layer with business logic and refactor the controllers.*
