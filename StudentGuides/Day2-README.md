# Day 2 -- Java Basics & Console App (Sprint 1)

> TICKET-F011 through TICKET-F020

---

## Overview

Today you build **Java POJO classes** that represent the database tables from Day 1, and a **console menu application** to interact with them using `Scanner` and `ArrayList`.

By the end of Day 2, you will have a runnable Java program that lets you list, add, and view transactions in the terminal.

---

## Key Concepts

- **POJO** (Plain Old Java Object): A simple Java class with fields, constructors, getters, and setters -- no framework annotations
- **ArrayList**: A resizable list collection (unlike fixed-size arrays)
- **Scanner**: Reads keyboard input from the user
- **String.format / printf**: Formats output into aligned columns

---

## Tickets

### TICKET-F011: Java Environment Setup
**File:** N/A

**Description:** Verify your Java and Maven setup, import the project into your IDE.

**What**
- Java 25+, Maven 3.8+, and the `backend/` Maven project opened in IntelliJ with `mvn compile` succeeding cleanly.

**Why**
- Every Java ticket from F012 onwards (and the Spring Boot work on Day 5) assumes this toolchain.
- A wrong JDK version surfaces as confusing `UnsupportedClassVersionError` failures later.

**Observe**
- `java -version` prints `25.x` (or higher) and `./mvnw compile` in `backend/` ends with `BUILD SUCCESS`.
- IntelliJ shows the `model/`, `entity/`, `console/` packages in the project tree.

**Instructions:**
1. Run `java -version` -- must be 25+
2. Run `mvn -version` -- must be 3.8+
3. Import the `backend/` folder as a Maven project in IntelliJ IDEA
4. Run `mvn compile` to verify it compiles cleanly
5. Explore the provided entity classes in `entity/` -- these are JPA versions; you will create plain Java versions in `model/`

**Acceptance Criteria:**
- [ ] `java -version` shows 25+
- [ ] `mvn compile` succeeds with no errors
- [ ] Project structure is visible in your IDE
- [ ] You understand the difference between `entity/` (JPA, provided) and `model/` (POJO, you build)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Pure environment check — no code to write. If `java -version` returns anything below 25, install a newer JDK (`brew install openjdk@25` / SDKMAN / Temurin). In IntelliJ, "Open" the `backend/` folder (don't import as Eclipse) and pick the bundled Maven wrapper.

</details>

<details>
<summary><b>Hint 2 — Commands</b></summary>

```bash
java -version       # must print 25 or higher
mvn -version        # must print 3.8 or higher (or use ./mvnw)

cd backend
./mvnw compile      # ✓ BUILD SUCCESS
```

In IntelliJ: `File → Open → backend/pom.xml → Open as Project`. Pick **JDK 25** in `File → Project Structure → Project SDK`.

</details>

<details>
<summary><b>Hint 3 — Walkthrough + folder map</b></summary>

```bash
# Install Java 25 if needed
brew install --cask temurin@25       # macOS
sdk install java 25.0.0-tem         # SDKMAN (any OS)

# Confirm
java -version
# openjdk version "17.0.x" ...

# Build the backend
cd backend
./mvnw clean compile
# [INFO] BUILD SUCCESS
```

What lives where (so you understand "entity vs model"):

```
backend/src/main/java/com/smartbudget/
├── SmartBudgetApplication.java   <- Spring Boot entry-point (Day 5)
├── console/Main.java             <- Today's work (F016-F020)
├── model/                        <- YOU CREATE: plain POJOs (F012-F015)
├── entity/                       <- PROVIDED: JPA @Entity classes for Day 5
├── repository/                   <- PROVIDED: JpaRepository interfaces
├── controller/                   <- PROVIDED basic, you refactor (Day 6)
├── service/                      <- YOU CREATE on Day 3+
├── dao/                          <- YOU CREATE on Day 4 (JDBC)
└── exception/                    <- YOU CREATE custom exceptions
```

`model/` = lightweight POJOs for Day 2-4 (Java-only, no Spring).  
`entity/` = JPA-annotated classes used by Spring Data from Day 5 onwards. They're already provided so the app can boot on Day 1.

</details>

---

### TICKET-F012: User POJO
**File:** `backend/src/main/java/com/smartbudget/model/User.java` (create this file)

**Description:** Create a plain Java class representing a user -- NOT a JPA entity.

**What**
- A `User.java` POJO in `com.smartbudget.model` with four fields, two constructors (full-args + name/email convenience), getters/setters, and a `toString()`.

**Why**
- This is the in-memory mirror of the Day 1 `users` table.
- Day 4's JDBC DAO will map result-set rows into instances of this class, and Day 3 will subclass it for the OOP refactor.

**Observe**
- `new User("Alice Smith", "alice@bank.com")` prints `User{userId=0, name='Alice Smith', email='alice@bank.com'}`.
- `getCreatedAt()` returns a non-null `LocalDateTime` close to now.

**Instructions:**
1. Create the file in `com.smartbudget.model` package
2. Add fields: `int userId`, `String name`, `String email`, `LocalDateTime createdAt`
3. Add a constructor that accepts all fields
4. Add an overloaded constructor that accepts only name and email (auto-set createdAt to now)
5. Add getters and setters for all fields
6. Override `toString()` to show a readable representation

**Acceptance Criteria:**
- [ ] Class compiles without errors
- [ ] Both constructors work (with and without createdAt)
- [ ] `toString()` returns something like `User{userId=1, name='Alice', email='alice@db.com'}`
- [ ] Fields match the `users` database table columns from Day 1

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Create the file under `backend/src/main/java/com/smartbudget/model/User.java`. Use `private` fields, a full-args constructor, plus an overloaded constructor that takes only name+email and sets `createdAt = LocalDateTime.now()`. Have your IDE generate getters/setters/`toString()` — Alt+Insert in IntelliJ.

</details>

<details>
<summary><b>Hint 2 — Constructor pattern</b></summary>

```java
public User(int userId, String name, String email, LocalDateTime createdAt) {
    this.userId    = userId;
    this.name      = name;
    this.email     = email;
    this.createdAt = createdAt;
}

// Convenience constructor — chains to the full one
public User(String name, String email) {
    this(0, name, email, LocalDateTime.now());
}
```

The chained `this(...)` call avoids repeating field assignments.

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
package com.smartbudget.model;

import java.time.LocalDateTime;

public class User {

    private int userId;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    public User() { }                                              // needed by some libs

    public User(int userId, String name, String email, LocalDateTime createdAt) {
        this.userId    = userId;
        this.name      = name;
        this.email     = email;
        this.createdAt = createdAt;
    }

    public User(String name, String email) {
        this(0, name, email, LocalDateTime.now());
    }

    public int getUserId()                  { return userId; }
    public void setUserId(int userId)       { this.userId = userId; }

    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }

    public String getEmail()                { return email; }
    public void setEmail(String email)      { this.email = email; }

    public LocalDateTime getCreatedAt()           { return createdAt; }
    public void setCreatedAt(LocalDateTime t)     { this.createdAt = t; }

    @Override
    public String toString() {
        return "User{userId=" + userId
             + ", name='"     + name + '\''
             + ", email='"    + email + '\'' + '}';
    }
}
```

Test in `Main.main`:

```java
User u = new User("Alice Smith", "alice@bank.com");
System.out.println(u);
// User{userId=0, name='Alice Smith', email='alice@bank.com'}
```

</details>

---

### TICKET-F013: Category POJO
**File:** `backend/src/main/java/com/smartbudget/model/Category.java` (create this file)

**Description:** Create a plain Java class representing a transaction category.

**What**
- A `Category.java` POJO with three fields plus a `setType` that rejects anything other than `"INCOME"` or `"EXPENSE"` by throwing `IllegalArgumentException`.

**Why**
- This is the Java mirror of the Day 1 `CHECK (type IN ('INCOME','EXPENSE'))` constraint — pushing the rule into the object stops bad data from ever reaching the DAO on Day 4.

**Observe**
- `new Category(2, "Junk", "RANDOM")` throws `IllegalArgumentException: type must be 'INCOME' or 'EXPENSE', got: RANDOM`.
- The valid `EXPENSE`/`INCOME` constructions succeed silently.

**Instructions:**
1. Create the file in `com.smartbudget.model` package
2. Add fields: `int categoryId`, `String name`, `String type`
3. Type must be either "INCOME" or "EXPENSE"
4. Add constructor, getters, setters, toString()
5. Add validation in the setter: if type is not INCOME or EXPENSE, throw IllegalArgumentException

**Acceptance Criteria:**
- [ ] Class compiles without errors
- [ ] Setting type to "INCOME" or "EXPENSE" works
- [ ] Setting type to "RANDOM" throws IllegalArgumentException
- [ ] `toString()` shows the category name and type

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Same pattern as `User` but the type setter does validation. Throw `IllegalArgumentException` if the value isn't `"INCOME"` or `"EXPENSE"`. This is the Java equivalent of the SQL `CHECK` constraint from Day 1 — it pushes validation into the object so bad data can't be silently held in memory.

</details>

<details>
<summary><b>Hint 2 — Validating setter</b></summary>

```java
public void setType(String type) {
    if (!"INCOME".equals(type) && !"EXPENSE".equals(type)) {
        throw new IllegalArgumentException(
            "type must be 'INCOME' or 'EXPENSE', got: " + type);
    }
    this.type = type;
}
```

Tip: call your own setter from the constructor — that way the validation runs even when the object is first built.

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
package com.smartbudget.model;

public class Category {

    private int categoryId;
    private String name;
    private String type;          // 'INCOME' or 'EXPENSE'

    public Category() { }

    public Category(int categoryId, String name, String type) {
        this.categoryId = categoryId;
        this.name       = name;
        setType(type);            // run validation in the constructor too
    }

    public int getCategoryId()           { return categoryId; }
    public void setCategoryId(int id)    { this.categoryId = id; }

    public String getName()              { return name; }
    public void setName(String name)     { this.name = name; }

    public String getType()              { return type; }
    public void setType(String type) {
        if (!"INCOME".equals(type) && !"EXPENSE".equals(type)) {
            throw new IllegalArgumentException(
                "type must be 'INCOME' or 'EXPENSE', got: " + type);
        }
        this.type = type;
    }

    @Override
    public String toString() {
        return "Category{id=" + categoryId
             + ", name='" + name + '\''
             + ", type='" + type + "'}";
    }
}
```

Test:

```java
Category food = new Category(1, "Food", "EXPENSE");      // ok
Category bad  = new Category(2, "Junk", "RANDOM");
//   ^ throws IllegalArgumentException at construction
```

</details>

---

### TICKET-F014: Transaction POJO
**File:** `backend/src/main/java/com/smartbudget/model/Transaction.java` (create this file)

**Description:** Create a plain Java class representing a financial transaction.

**What**
- A `Transaction.java` POJO with seven fields where `amount` is `BigDecimal` (not `double`) and the setter rejects anything `<= 0` via `compareTo(BigDecimal.ZERO)`.

**Why**
- `BigDecimal` is the non-negotiable money type — `double` quietly produces `0.1 + 0.2 = 0.30000000000000004` and breaks finance code.
- The DAO on Day 4 and the REST controller on Day 6 both rely on this being correct.

**Observe**
- `new Transaction(..., new BigDecimal("-50"), ...)` throws `IllegalArgumentException: amount must be > 0, got: -50`.
- A valid construction yields a `toString()` like `Transaction[id=1, user=1, cat=3, amount=45.20, date=2026-01-08, type=EXPENSE, ...]`.

**Instructions:**
1. Create the file in `com.smartbudget.model` package
2. Add fields: `int txnId`, `int userId`, `int categoryId`, `BigDecimal amount`, `LocalDate txnDate`, `String description`, `String type`
3. Use `BigDecimal` for amount (never `double` for financial values)
4. Add constructor, getters, setters, toString()
5. Add validation: amount must be > 0 (use `compareTo(BigDecimal.ZERO) > 0`)

**Acceptance Criteria:**
- [ ] Class compiles without errors
- [ ] Uses `BigDecimal` for amount, not double
- [ ] Amount validation works (negative amounts rejected)
- [ ] `toString()` shows all fields in a readable format

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Same recipe as `User`/`Category`: 7 fields, constructor, getters/setters, `toString`, validation on the amount setter. The only twist: amount is `BigDecimal` (not `double`), and you compare with `.compareTo(BigDecimal.ZERO) > 0`, not `>`.

</details>

<details>
<summary><b>Hint 2 — BigDecimal validation</b></summary>

`BigDecimal` doesn't support `>` / `<` operators (it's an object, not a primitive). Use `.compareTo(other)`:

```java
public void setAmount(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("amount must be > 0, got: " + amount);
    }
    this.amount = amount;
}
```

`compareTo` returns -1, 0, or 1. So `> 0` means "amount is greater than zero".

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
package com.smartbudget.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {

    private int txnId;
    private int userId;
    private int categoryId;
    private BigDecimal amount;
    private LocalDate txnDate;
    private String description;
    private String type;          // 'INCOME' or 'EXPENSE'

    public Transaction() { }

    public Transaction(int txnId, int userId, int categoryId,
                       BigDecimal amount, LocalDate txnDate,
                       String description, String type) {
        this.txnId       = txnId;
        this.userId      = userId;
        this.categoryId  = categoryId;
        setAmount(amount);
        this.txnDate     = txnDate;
        this.description = description;
        this.type        = type;
    }

    public int getTxnId()                    { return txnId; }
    public void setTxnId(int id)             { this.txnId = id; }

    public int getUserId()                   { return userId; }
    public void setUserId(int id)            { this.userId = id; }

    public int getCategoryId()               { return categoryId; }
    public void setCategoryId(int id)        { this.categoryId = id; }

    public BigDecimal getAmount()            { return amount; }
    public void setAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "amount must be > 0, got: " + amount);
        }
        this.amount = amount;
    }

    public LocalDate getTxnDate()            { return txnDate; }
    public void setTxnDate(LocalDate d)      { this.txnDate = d; }

    public String getDescription()           { return description; }
    public void setDescription(String d)     { this.description = d; }

    public String getType()                  { return type; }
    public void setType(String type)         { this.type = type; }

    @Override
    public String toString() {
        return String.format(
            "Transaction[id=%d, user=%d, cat=%d, amount=%s, date=%s, type=%s, desc='%s']",
            txnId, userId, categoryId, amount, txnDate, type, description);
    }
}
```

Why `BigDecimal` and not `double`? Floating-point can't represent decimal money exactly: `0.1 + 0.2 == 0.30000000000000004` in Java. `BigDecimal` is exact decimal arithmetic — non-negotiable for finance.

</details>

---

### TICKET-F015: SavingsGoal POJO
**File:** `backend/src/main/java/com/smartbudget/model/SavingsGoal.java` (create this file)

**Description:** Create a plain Java class representing a savings goal.

**What**
- A `SavingsGoal.java` POJO with six fields plus two derived methods — `getProgressPercentage()` (using `BigDecimal.divide` with scale + `RoundingMode.HALF_UP`) and `isCompleted()`.

**Why**
- These derived methods are pure-Java business logic — Day 6's REST endpoint and Day 9's React progress bar both call them.
- Skipping the rounding mode on `divide()` will blow up at runtime on any non-terminating decimal.

**Observe**
- A 500/1000 goal returns `50.0000` from `getProgressPercentage()` and `false` from `isCompleted()`.
- A 1500/1500 goal returns `100.0000` and `true`.

**Instructions:**
1. Create the file in `com.smartbudget.model` package
2. Add fields: `int goalId`, `int userId`, `String goalName`, `BigDecimal targetAmount`, `BigDecimal currentAmount`, `LocalDate deadline`
3. Add constructor, getters, setters, toString()
4. Add a method `getProgressPercentage()` that calculates (currentAmount / targetAmount * 100)
5. Add a method `isCompleted()` that returns true when currentAmount >= targetAmount

**Acceptance Criteria:**
- [ ] Class compiles without errors
- [ ] `getProgressPercentage()` returns correct values (e.g., 50% when current = 500 and target = 1000)
- [ ] `isCompleted()` returns false when current < target, true when current >= target
- [ ] `toString()` shows goal name, progress, and deadline

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Same field/constructor/getter/setter pattern as before. Then add two derived methods: `getProgressPercentage()` returns `(current / target) * 100` as a `BigDecimal`, and `isCompleted()` returns `current.compareTo(target) >= 0`. Watch out for `BigDecimal` division — you must pass scale + rounding mode or it throws on non-terminating decimals.

</details>

<details>
<summary><b>Hint 2 — Derived methods</b></summary>

```java
public BigDecimal getProgressPercentage() {
    if (targetAmount.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
    return currentAmount
            .divide(targetAmount, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
}

public boolean isCompleted() {
    return currentAmount.compareTo(targetAmount) >= 0;
}
```

The `4, RoundingMode.HALF_UP` tells `divide()` "scale to 4 decimal places, round half-up" — without it, `5.divide(3)` throws `ArithmeticException`.

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
package com.smartbudget.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class SavingsGoal {

    private int goalId;
    private int userId;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDate deadline;

    public SavingsGoal() { }

    public SavingsGoal(int goalId, int userId, String goalName,
                       BigDecimal targetAmount, BigDecimal currentAmount,
                       LocalDate deadline) {
        this.goalId        = goalId;
        this.userId        = userId;
        this.goalName      = goalName;
        this.targetAmount  = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline      = deadline;
    }

    // (all getters/setters omitted for brevity — IDE-generate them)

    public BigDecimal getProgressPercentage() {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAmount
                .divide(targetAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public boolean isCompleted() {
        return currentAmount != null
            && targetAmount  != null
            && currentAmount.compareTo(targetAmount) >= 0;
    }

    @Override
    public String toString() {
        return String.format(
            "Goal[%s, %.2f / %.2f (%.1f%%), due %s]",
            goalName, currentAmount, targetAmount,
            getProgressPercentage(), deadline);
    }
}
```

Quick test:

```java
SavingsGoal g = new SavingsGoal(1, 1, "Holiday",
        new BigDecimal("1000.00"),
        new BigDecimal("500.00"),
        LocalDate.of(2026, 12, 1));
System.out.println(g.getProgressPercentage());  // 50.0000
System.out.println(g.isCompleted());             // false
```

</details>

---

### TICKET-F016: Console Menu Loop
**File:** `backend/src/main/java/com/smartbudget/console/Main.java`

**Description:** Build an interactive text menu using `while` loop and `Scanner`.

**What**
- A `Main.main(...)` that loops on `boolean running`, prints a 4-option menu, reads an `int` with `Scanner`, and dispatches via `switch` to four stub methods.

**Why**
- This is the UI shell every console-app ticket today (F018-F020) hangs off.
- Get the `nextInt()` + `nextLine()` newline-eating gotcha right once here, and the rest of the day is just filling in cases.

**Observe**
- Each menu option prints something distinct and re-prompts; typing a letter prints `Please enter a number 1-4.` instead of crashing with `InputMismatchException`.
- Picking `4` prints `Goodbye!` and the process exits.

**Instructions (follow the TODOs in Main.java):**
1. Create a `Scanner` for `System.in`
2. Use a `boolean running = true` to control the while loop
3. Print menu options: 1. List Transactions, 2. Add Transaction, 3. Summary, 4. Exit
4. Read user's choice with `scanner.nextInt()`
5. IMPORTANT: Call `scanner.nextLine()` after `nextInt()` to consume the leftover newline
6. Use a `switch` statement to handle each option
7. Option 4 sets `running = false` to exit

**Acceptance Criteria:**
- [ ] Program runs with right-click -> Run 'Main.main()'
- [ ] Menu displays 4 options
- [ ] Each option triggers different behavior
- [ ] Option 4 exits the program cleanly
- [ ] Invalid input does not crash the program

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`while (running) { print menu; read choice; switch(choice) { ... } }`. The classic Java gotcha: after `scanner.nextInt()` add `scanner.nextLine()` to consume the newline char, otherwise the next `nextLine()` returns "". Wrap `nextInt()` in try-catch for `InputMismatchException` so typing a letter doesn't crash the app.

</details>

<details>
<summary><b>Hint 2 — Loop skeleton</b></summary>

```java
Scanner sc = new Scanner(System.in);
boolean running = true;
while (running) {
    System.out.println("\n=== SmartBudget ===");
    System.out.println("1) List   2) Add   3) Summary   4) Exit");
    System.out.print("Choice: ");

    int choice;
    try {
        choice = sc.nextInt();
        sc.nextLine();                        // consume newline
    } catch (InputMismatchException e) {
        System.out.println("Please enter a number 1-4.");
        sc.nextLine();
        continue;
    }

    switch (choice) {
        case 1 -> { /* F018 list */ }
        case 2 -> { /* F019 add  */ }
        case 3 -> { /* F020 summary */ }
        case 4 -> running = false;
        default -> System.out.println("Unknown option: " + choice);
    }
}
sc.close();
```

</details>

<details>
<summary><b>Hint 3 — Full solution (menu loop only)</b></summary>

```java
package com.smartbudget.console;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== SmartBudget Console ===");
            System.out.println("1) List Transactions");
            System.out.println("2) Add Transaction");
            System.out.println("3) Summary");
            System.out.println("4) Exit");
            System.out.print("Choice: ");

            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine();                    // discard trailing newline
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number 1-4.");
                sc.nextLine();                    // discard bad token
                continue;
            }

            switch (choice) {
                case 1 -> listTransactions();     // wired up in F018
                case 2 -> addTransaction(sc);     // wired up in F019
                case 3 -> showSummary();          // wired up in F020
                case 4 -> running = false;
                default -> System.out.println("Unknown option: " + choice);
            }
        }
        sc.close();
        System.out.println("Goodbye!");
    }

    private static void listTransactions() { /* F018 */ }
    private static void addTransaction(Scanner sc) { /* F019 */ }
    private static void showSummary() { /* F020 */ }
}
```

Run from IntelliJ (right-click `Main` → Run 'Main.main()') and verify each option prints something distinct, and that picking `4` exits cleanly.

</details>

---

### TICKET-F017: Sample Data in Console
**File:** `backend/src/main/java/com/smartbudget/console/Main.java`

**Description:** Create an ArrayList of hardcoded sample transactions for the console app.

**What**
- A `static final List<Transaction> TXNS` field on `Main` populated by a `seed()` method with 10+ `Transaction` instances (mix of INCOME/EXPENSE, multiple dates and users).

**Why**
- Day 4 swaps this hardcoded list for a JDBC DAO call — having a fixed, predictable in-memory dataset now lets you verify F018/F019/F020 work before the database wiring lands.

**Observe**
- `seed()` runs at startup and prints `Seeded 10 transactions` before the menu appears.
- Option 1 then shows 10 rows.

**Instructions:**
1. Create an ArrayList to store sample data
2. Add 10+ hardcoded transactions (can be String arrays or use your Transaction POJO from F014)
3. Include a mix of INCOME and EXPENSE types
4. Include different dates and amounts

**Acceptance Criteria:**
- [ ] ArrayList contains 10+ transactions
- [ ] Mix of INCOME and EXPENSE types
- [ ] `list.size()` prints the correct count
- [ ] Data is realistic (reasonable amounts, real-world categories)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`ArrayList<Transaction>` is the natural choice — use your POJO from F014. Declare it as a `static` field on `Main` so all the methods (`list`, `add`, `summary`) can see it. Hardcode 10+ entries inside a private `static` method `seed()` called from `main`.

</details>

<details>
<summary><b>Hint 2 — Static list + seed method</b></summary>

```java
private static final List<Transaction> TXNS = new ArrayList<>();

private static void seed() {
    TXNS.add(new Transaction(1, 1, 1, new BigDecimal("3500.00"),
            LocalDate.of(2026, 1, 1), "January salary", "INCOME"));
    TXNS.add(new Transaction(2, 1, 3, new BigDecimal("45.20"),
            LocalDate.of(2026, 1, 8), "Groceries", "EXPENSE"));
    // ... 8 more
}
```

Call `seed();` once at the top of `main(...)` before the menu loop.

</details>

<details>
<summary><b>Hint 3 — Full solution (seed method)</b></summary>

```java
import com.smartbudget.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final List<Transaction> TXNS = new ArrayList<>();

    public static void main(String[] args) {
        seed();
        // ... menu loop from F016
    }

    private static void seed() {
        TXNS.add(t(1, 1, 1, "3500.00", "2026-01-01", "January salary",   "INCOME"));
        TXNS.add(t(2, 1, 3,   "45.20", "2026-01-08", "Groceries",        "EXPENSE"));
        TXNS.add(t(3, 1, 4,   "25.00", "2026-01-15", "Bus pass",         "EXPENSE"));
        TXNS.add(t(4, 2, 1, "4200.00", "2026-01-01", "January salary",   "INCOME"));
        TXNS.add(t(5, 2, 5,  "120.00", "2026-01-20", "Electricity bill", "EXPENSE"));
        TXNS.add(t(6, 3, 2,  "800.00", "2026-02-05", "Freelance gig",    "INCOME"));
        TXNS.add(t(7, 3, 3,   "60.00", "2026-02-10", "Restaurant",       "EXPENSE"));
        TXNS.add(t(8, 1, 1, "3500.00", "2026-02-01", "February salary",  "INCOME"));
        TXNS.add(t(9, 4, 1, "2800.00", "2026-02-01", "February salary",  "INCOME"));
        TXNS.add(t(10, 5, 3, "52.00",  "2026-03-05", "Groceries",        "EXPENSE"));
        System.out.println("Seeded " + TXNS.size() + " transactions");
    }

    // tiny factory to keep seed() readable
    private static Transaction t(int id, int uid, int cid, String amt,
                                 String date, String desc, String type) {
        return new Transaction(id, uid, cid, new BigDecimal(amt),
                LocalDate.parse(date), desc, type);
    }
}
```

Running prints `Seeded 10 transactions` before the menu appears.

</details>

---

### TICKET-F018: Formatted Output
**File:** `backend/src/main/java/com/smartbudget/console/Main.java`

**Description:** Display transactions in a formatted table using `printf`.

**What**
- A `listTransactions()` method that prints a header row, a `"-".repeat(65)` separator, and one `System.out.printf` row per transaction using `%-5d %-25s %-8s %10.2f %-12s%n`.

**Why**
- Aligned, two-decimal output is what makes the console app feel like a real tool.
- Day 9's React table mirrors these same columns — the mental model carries over.

**Observe**
- Columns line up vertically and amounts show exactly two decimal places (e.g. `45.20` not `45.2`).
- The footer reads `Total rows: 10`.

**Instructions:**
1. When user picks "1. List Transactions":
   - Print a header row: ID, Description, Type, Amount, Date
   - Print a separator: `"-".repeat(55)`
   - Loop through the ArrayList and print each row using `System.out.printf`
2. Format specifiers:
   - `%-5s` -- left-aligned, 5 chars wide (for ID)
   - `%-15s` -- left-aligned, 15 chars (for description)
   - `%10.2f` -- right-aligned, 10 chars, 2 decimal places (for amount)

**Acceptance Criteria:**
- [ ] Output displays as a clean, aligned table
- [ ] All amounts show exactly 2 decimal places
- [ ] Columns are properly aligned
- [ ] Header and separator line appear above the data

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`System.out.printf` with format specifiers. `%-5d` = integer, left-aligned, 5 chars wide. `%-15s` = string, left-aligned, 15 chars. `%10.2f` = float, right-aligned, 10 chars, 2 decimals. Print a header, then `"-".repeat(60)`, then loop and print each row with the same format string.

</details>

<details>
<summary><b>Hint 2 — listTransactions() body</b></summary>

```java
private static void listTransactions() {
    System.out.printf("%-5s %-25s %-8s %10s %-12s%n",
            "ID", "Description", "Type", "Amount", "Date");
    System.out.println("-".repeat(65));
    for (Transaction t : TXNS) {
        System.out.printf("%-5d %-25s %-8s %10.2f %-12s%n",
                t.getTxnId(),
                t.getDescription(),
                t.getType(),
                t.getAmount(),
                t.getTxnDate());
    }
    System.out.println("(" + TXNS.size() + " transactions)");
}
```

`%n` = platform-correct newline. `%-25s` truncates strings longer than 25, so keep descriptions short.

</details>

<details>
<summary><b>Hint 3 — Full solution + sample output</b></summary>

```java
private static void listTransactions() {
    if (TXNS.isEmpty()) {
        System.out.println("(no transactions)");
        return;
    }

    String fmt = "%-5s %-25s %-8s %10s %-12s%n";
    System.out.printf(fmt, "ID", "Description", "Type", "Amount", "Date");
    System.out.println("-".repeat(65));

    String rowFmt = "%-5d %-25s %-8s %10.2f %-12s%n";
    for (Transaction t : TXNS) {
        System.out.printf(rowFmt,
                t.getTxnId(),
                t.getDescription(),
                t.getType(),
                t.getAmount(),
                t.getTxnDate());
    }
    System.out.println("-".repeat(65));
    System.out.println("Total rows: " + TXNS.size());
}
```

Sample output:

```
ID    Description               Type        Amount Date
-----------------------------------------------------------------
1     January salary            INCOME     3500.00 2026-01-01
2     Groceries                 EXPENSE      45.20 2026-01-08
3     Bus pass                  EXPENSE      25.00 2026-01-15
4     January salary            INCOME     4200.00 2026-01-01
...
-----------------------------------------------------------------
Total rows: 10
```

Common format specifiers cheat-sheet:
| Format  | Meaning                            |
|---------|------------------------------------|
| `%d`    | integer                            |
| `%-5d`  | integer, left-aligned, 5 wide      |
| `%s`    | string                             |
| `%-15s` | string, left-aligned, 15 wide      |
| `%f`    | float, 6 decimals (default)        |
| `%10.2f`| float, right-aligned, 10 wide, 2dp |
| `%n`    | platform-correct newline           |

</details>

---

### TICKET-F019: Input Validation
**File:** `backend/src/main/java/com/smartbudget/console/Main.java`

**Description:** Validate user input when adding a transaction.

**What**
- An `addTransaction(Scanner sc)` method that reads amount/date/description/type, catches `NumberFormatException` and `DateTimeParseException`, and rejects negative amounts and future dates with a clear message before appending to `TXNS`.

**Why**
- This is the application-layer validation that pairs with the POJO-level validation from F014.
- Day 6's REST controller layers `@Valid` + `@ControllerAdvice` on top of the same idea — same rules, different transport.

**Observe**
- Entering amount `-50` prints `Amount must be positive. Transaction not added.` and `TXNS.size()` is unchanged; entering date `2030-01-01` prints `Date cannot be in the future...`.
- Valid input prints `Added transaction #11` and option 1 now lists 11 rows.

**Instructions:**
1. When user picks "2. Add Transaction":
   - Read amount from Scanner, parse to BigDecimal
   - Validate: amount must be > 0 (use `compareTo(BigDecimal.ZERO)`)
   - Read date from Scanner, parse to LocalDate
   - Validate: date must not be in the future (use `isAfter(LocalDate.now())`)
   - If valid, add to the ArrayList
   - If invalid, print a clear error message and skip the add

**Acceptance Criteria:**
- [ ] Adding amount = -10 prints an error, does not add
- [ ] Adding a future date prints an error, does not add
- [ ] Adding valid data succeeds and the new item appears in the list
- [ ] Error messages are clear and specific

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Read each field with `sc.nextLine()`, parse it, validate, then build a `Transaction`. Wrap `new BigDecimal(input)` and `LocalDate.parse(input)` in try-catch — bad input throws `NumberFormatException` / `DateTimeParseException`. On any validation failure: print a clear message and `return` (don't add the row). Use `LocalDate.now()` to detect future dates.

</details>

<details>
<summary><b>Hint 2 — Validation pattern</b></summary>

```java
System.out.print("Amount: ");
BigDecimal amount;
try {
    amount = new BigDecimal(sc.nextLine());
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        System.out.println("Amount must be > 0. Skipping.");
        return;
    }
} catch (NumberFormatException e) {
    System.out.println("Invalid amount. Skipping.");
    return;
}

System.out.print("Date (yyyy-mm-dd): ");
LocalDate date;
try {
    date = LocalDate.parse(sc.nextLine());
    if (date.isAfter(LocalDate.now())) {
        System.out.println("Date can't be in the future. Skipping.");
        return;
    }
} catch (DateTimeParseException e) {
    System.out.println("Invalid date format. Skipping.");
    return;
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
private static void addTransaction(Scanner sc) {
    System.out.print("Amount: ");
    BigDecimal amount;
    try {
        amount = new BigDecimal(sc.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("Invalid number. Transaction not added.");
        return;
    }
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        System.out.println("Amount must be positive. Transaction not added.");
        return;
    }

    System.out.print("Date (yyyy-mm-dd): ");
    LocalDate date;
    try {
        date = LocalDate.parse(sc.nextLine().trim());
    } catch (DateTimeParseException e) {
        System.out.println("Invalid date format. Transaction not added.");
        return;
    }
    if (date.isAfter(LocalDate.now())) {
        System.out.println("Date cannot be in the future. Transaction not added.");
        return;
    }

    System.out.print("Description: ");
    String desc = sc.nextLine().trim();
    if (desc.isEmpty()) {
        System.out.println("Description required. Transaction not added.");
        return;
    }

    System.out.print("Type (INCOME/EXPENSE): ");
    String type = sc.nextLine().trim().toUpperCase();
    if (!"INCOME".equals(type) && !"EXPENSE".equals(type)) {
        System.out.println("Type must be INCOME or EXPENSE. Transaction not added.");
        return;
    }

    int nextId = TXNS.size() + 1;
    TXNS.add(new Transaction(nextId, 1, 1, amount, date, desc, type));
    System.out.println("Added transaction #" + nextId);
}
```

Try it:
- amount `-50` → "Amount must be positive..."
- date `2030-01-01` → "Date cannot be in the future..."
- amount `abc` → "Invalid number..."
- valid input → "Added transaction #11", and option 1 now shows 11 rows.

</details>

---

### TICKET-F020: Summary View
**File:** `backend/src/main/java/com/smartbudget/console/Main.java`

**Description:** Calculate and display summary statistics.

**What**
- A `showSummary()` method that walks `TXNS`, accumulates two `BigDecimal` totals (one per type), computes `net = income.subtract(expense)`, and prints all three with `%12.2f`.

**Why**
- This is the Java mirror of Day 1's CTE-based net-balance query (F010).
- Day 6 will expose the same calculation as a `/api/summary` REST endpoint and Day 9 will render it in a card — same math, three layers.

**Observe**
- With the F017 seed the output reads `Total Income: 14800.00 / Total Expenses: 302.20 / Net Balance: 14497.80`.
- Adding a new INCOME row via option 2 increases the income line by exactly that amount.

**Instructions:**
1. When user picks "3. Summary":
   - Calculate total income (sum of all INCOME amounts)
   - Calculate total expenses (sum of all EXPENSE amounts)
   - Calculate net balance (income - expenses)
   - Display all three values formatted with 2 decimal places

**Acceptance Criteria:**
- [ ] Total income is correct (sum of all INCOME transactions)
- [ ] Total expenses is correct (sum of all EXPENSE transactions)
- [ ] Net balance = income - expenses
- [ ] Values are formatted with 2 decimal places

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Loop the list, accumulate two `BigDecimal` totals — one for `"INCOME"` rows, one for `"EXPENSE"`. Net = income.subtract(expense). Print with `%.2f`. (You can also use Streams with `map(...)` + `reduce(BigDecimal.ZERO, BigDecimal::add)` once you've seen them — that's Day 4 territory.)

</details>

<details>
<summary><b>Hint 2 — Accumulate pattern</b></summary>

```java
BigDecimal income  = BigDecimal.ZERO;
BigDecimal expense = BigDecimal.ZERO;

for (Transaction t : TXNS) {
    if ("INCOME".equals(t.getType()))  income  = income .add(t.getAmount());
    if ("EXPENSE".equals(t.getType())) expense = expense.add(t.getAmount());
}

BigDecimal net = income.subtract(expense);
System.out.printf("Income:  %10.2f%n", income);
System.out.printf("Expense: %10.2f%n", expense);
System.out.printf("Net:     %10.2f%n", net);
```

`BigDecimal` is immutable — `income.add(x)` returns a new value; you must reassign.

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
private static void showSummary() {
    BigDecimal income  = BigDecimal.ZERO;
    BigDecimal expense = BigDecimal.ZERO;

    for (Transaction t : TXNS) {
        if ("INCOME".equals(t.getType())) {
            income = income.add(t.getAmount());
        } else if ("EXPENSE".equals(t.getType())) {
            expense = expense.add(t.getAmount());
        }
    }
    BigDecimal net = income.subtract(expense);

    System.out.println();
    System.out.println("=== Summary ===");
    System.out.printf("Total Income:    %12.2f%n", income);
    System.out.printf("Total Expenses:  %12.2f%n", expense);
    System.out.println("-".repeat(28));
    System.out.printf("Net Balance:     %12.2f%n", net);
}
```

Example output with the F018 seed data:

```
=== Summary ===
Total Income:        14800.00
Total Expenses:        302.20
----------------------------
Net Balance:         14497.80
```

</details>

---

## End-of-Day Checklist

- [ ] All 4 POJO classes created in `model/` package (User, Category, Transaction, SavingsGoal)
- [ ] Console app runs and shows a working menu
- [ ] List option shows formatted transactions in a table
- [ ] Add option validates input (negative amounts, future dates rejected)
- [ ] Summary option shows correct totals
- [ ] You can explain: POJO, ArrayList, Scanner, printf, BigDecimal, input validation

---

*Tomorrow (Day 3): You will refactor using OOP -- abstract classes, inheritance, and polymorphism.*
