# Day 3 -- Object-Oriented Programming (Sprint 2)

> TICKET-F021 through TICKET-F031

---

## Overview

Today you learn **OOP fundamentals** by building an inheritance hierarchy for transactions and a plain Java `TransactionService`. You will also implement CSV import/export.

By the end of Day 3, you will have:
- An abstract `BaseTransaction` class with `IncomeTransaction` and `ExpenseTransaction` subclasses
- A custom exception class
- A `TransactionService` with CRUD, filtering, and CSV operations

---

## Key Concepts

- **Abstract class**: Cannot be instantiated directly; serves as a blueprint for subclasses
- **Inheritance**: Child classes inherit fields/methods from a parent class
- **Polymorphism**: Same method name, different behavior depending on the subclass
- **Custom exceptions**: Application-specific error types for better error handling
- **DRY principle**: Don't Repeat Yourself -- shared logic goes in the parent class

---

## Tickets

### TICKET-F021: Abstract Base Class
**File:** `backend/src/main/java/com/smartbudget/model/BaseTransaction.java`

**Description:** Create an abstract class that serves as the parent for all transaction types.

**What**
- An `abstract class BaseTransaction` with 4 protected fields, a validating constructor that throws `InvalidTransactionException`, an abstract `getType()`, getters, and a `toString()` that delegates to `getType()`.

**Why**
- This is the shared shape every transaction needs — pulling fields and validation up here is the DRY move that keeps `IncomeTransaction` and `ExpenseTransaction` to a handful of lines each.

**Observe**
- Typing `new BaseTransaction(...)` in your IDE shows a red "Cannot instantiate the type BaseTransaction" error.
- `new IncomeTransaction(1, new BigDecimal("-5"), LocalDate.now(), "x")` throws `InvalidTransactionException: Amount must be greater than zero, got: -5`.

**Instructions (follow the 5 steps in the file):**

**Step 1 -- Declare fields:**
- 4 protected fields: `int txnId`, `BigDecimal amount`, `LocalDate txnDate`, `String description`
- Use `protected` (not `private`) so child classes can access them

**Step 2 -- Constructor with validation:**
- Accept all 4 fields as parameters
- Rule 1: Amount must be > 0 (use `amount.compareTo(BigDecimal.ZERO) <= 0` to check)
- Rule 2: Date must not be in the future (use `txnDate.isAfter(LocalDate.now())`)
- Throw `InvalidTransactionException` if either rule is violated

**Step 3 -- Abstract method:**
- Declare `public abstract String getType();` -- no body, just a signature
- IncomeTransaction will return "INCOME", ExpenseTransaction will return "EXPENSE"

**Step 4 -- Getters for all fields**

**Step 5 -- Override toString():**
- Use `String.format()` to show: `[TYPE] id=X | amount | date | description`
- Call `getType()` (not a hardcoded string) so each subclass shows its own type

**Acceptance Criteria:**
- [ ] Class is declared as `abstract` -- you cannot write `new BaseTransaction(...)`
- [ ] Constructor throws `InvalidTransactionException` for amount <= 0
- [ ] Constructor throws `InvalidTransactionException` for future dates
- [ ] `getType()` is abstract (no method body)
- [ ] Fields are `protected`, not `private`
- [ ] `toString()` produces readable output

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`public abstract class BaseTransaction { ... }` — the `abstract` keyword in the class header is what stops anyone calling `new BaseTransaction(...)`. The `getType()` method gets declared as `public abstract String getType();` with no body (and a `;` instead of `{}`). The constructor still works — subclasses call it via `super(...)`.

</details>

<details>
<summary><b>Hint 2 — Class skeleton</b></summary>

```java
public abstract class BaseTransaction {

    protected int txnId;
    protected BigDecimal amount;
    protected LocalDate txnDate;
    protected String description;

    public BaseTransaction(int txnId, BigDecimal amount,
                           LocalDate txnDate, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException(
                "Amount must be greater than zero, got: " + amount);
        }
        if (txnDate == null || txnDate.isAfter(LocalDate.now())) {
            throw new InvalidTransactionException(
                "Transaction date cannot be in the future: " + txnDate);
        }
        this.txnId       = txnId;
        this.amount      = amount;
        this.txnDate     = txnDate;
        this.description = description;
    }

    public abstract String getType();   // subclasses must implement
    // ...getters + toString
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
package com.smartbudget.model;

import com.smartbudget.exception.InvalidTransactionException;
import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class BaseTransaction {

    protected int txnId;
    protected BigDecimal amount;
    protected LocalDate txnDate;
    protected String description;

    public BaseTransaction(int txnId, BigDecimal amount,
                           LocalDate txnDate, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException(
                "Amount must be greater than zero, got: " + amount);
        }
        if (txnDate == null || txnDate.isAfter(LocalDate.now())) {
            throw new InvalidTransactionException(
                "Transaction date cannot be in the future: " + txnDate);
        }
        this.txnId       = txnId;
        this.amount      = amount;
        this.txnDate     = txnDate;
        this.description = description;
    }

    public abstract String getType();

    public int getTxnId()              { return txnId; }
    public BigDecimal getAmount()      { return amount; }
    public LocalDate getTxnDate()      { return txnDate; }
    public String getDescription()     { return description; }

    @Override
    public String toString() {
        return String.format("[%s] id=%d | %s | %s | %s",
                getType(), txnId, amount, txnDate, description);
    }
}
```

Why `protected` fields? So subclasses can read them directly without going through a getter. Why call `getType()` from `toString()` instead of hardcoding "TRANSACTION"? That's polymorphism in action — the subclass's override gets invoked even though the call is in the parent.

Try `new BaseTransaction(...)` — your IDE underlines it red: "Cannot instantiate the type BaseTransaction." That's the abstract keyword doing its job.

</details>

---

### TICKET-F022: IncomeTransaction Subclass
**File:** `backend/src/main/java/com/smartbudget/model/IncomeTransaction.java` (create this file)

**Description:** Create a concrete subclass for income transactions.

**What**
- A concrete `IncomeTransaction extends BaseTransaction` with one `super(...)`-delegating constructor and an `@Override` of `getType()` returning `"INCOME"`.

**Why**
- This is the first proof that the abstract parent pulls its weight — the whole subclass is ~10 lines because every field, every validation, and `toString()` are already inherited.

**Observe**
- `new IncomeTransaction(1, new BigDecimal("3500"), LocalDate.now(), "Salary").getType()` returns `"INCOME"`.
- The same object's `toString()` starts with `[INCOME] id=1`.

**Instructions:**
1. Extend `BaseTransaction`
2. Create a constructor that calls `super(txnId, amount, txnDate, description)`
3. Override `getType()` to return `"INCOME"`

**Acceptance Criteria:**
- [ ] `new IncomeTransaction(1, new BigDecimal("3500"), LocalDate.now(), "Salary")` compiles and runs
- [ ] `getType()` returns "INCOME"
- [ ] `toString()` shows "[INCOME] id=1 | 3500 | ..."
- [ ] Validation from BaseTransaction still works (negative amounts rejected)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Very small class: `extends BaseTransaction`, one constructor that delegates to `super(...)`, one `@Override` of `getType()` returning `"INCOME"`. No new fields needed. The validation in the parent constructor still runs automatically via `super(...)`.

</details>

<details>
<summary><b>Hint 2 — Class structure</b></summary>

```java
public class IncomeTransaction extends BaseTransaction {

    public IncomeTransaction(int txnId, BigDecimal amount,
                             LocalDate txnDate, String description) {
        super(txnId, amount, txnDate, description);   // parent validates
    }

    @Override
    public String getType() {
        return "INCOME";
    }
}
```

The `@Override` annotation is optional but recommended — the compiler will warn you if the signature drifts from the parent's abstract method.

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
package com.smartbudget.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IncomeTransaction extends BaseTransaction {

    public IncomeTransaction(int txnId, BigDecimal amount,
                             LocalDate txnDate, String description) {
        super(txnId, amount, txnDate, description);
    }

    @Override
    public String getType() {
        return "INCOME";
    }
}
```

Test in `Main.main`:

```java
IncomeTransaction salary = new IncomeTransaction(
        1, new BigDecimal("3500.00"), LocalDate.now(), "January salary");
System.out.println(salary.getType());   // INCOME
System.out.println(salary);             // [INCOME] id=1 | 3500.00 | 2026-... | January salary

// validation inherited from parent:
try {
    new IncomeTransaction(2, new BigDecimal("-100"),
            LocalDate.now(), "Bad");
} catch (InvalidTransactionException e) {
    System.out.println("Rejected: " + e.getMessage());
}
```

</details>

---

### TICKET-F023: ExpenseTransaction Subclass
**File:** `backend/src/main/java/com/smartbudget/model/ExpenseTransaction.java` (create this file)

**Description:** Create a concrete subclass for expense transactions.

**What**
- A concrete `ExpenseTransaction extends BaseTransaction` with a `super(...)`-delegating constructor, an `@Override` of `getType()` returning `"EXPENSE"`, and an optional `String category` field with getter.

**Why**
- Mirroring `IncomeTransaction` proves the parent's contract is symmetrical, and the extra `category` field shows that subclasses can add their own state on top of the inherited shape.

**Observe**
- Both `IncomeTransaction` and `ExpenseTransaction` instances can be added to the same `List<BaseTransaction>` without a cast.
- Looping over that list prints rows that start with `[INCOME]` or `[EXPENSE]` depending on the actual subclass.

**Instructions:**
1. Extend `BaseTransaction`
2. Create a constructor that calls `super(txnId, amount, txnDate, description)`
3. Override `getType()` to return `"EXPENSE"`
4. Optionally add a `category` field specific to expenses

**Acceptance Criteria:**
- [ ] `new ExpenseTransaction(2, new BigDecimal("50"), LocalDate.now(), "Groceries")` works
- [ ] `getType()` returns "EXPENSE"
- [ ] `toString()` shows "[EXPENSE] id=2 | 50 | ..."
- [ ] Both IncomeTransaction and ExpenseTransaction can be stored in a `List<BaseTransaction>`

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Mirror of F022. Same parent, same `super(...)` call, but `getType()` returns `"EXPENSE"`. The optional `category` field (e.g., `"Food"`, `"Transport"`) is what differentiates expenses from incomes — add it as a private field with a getter.

</details>

<details>
<summary><b>Hint 2 — With the optional category field</b></summary>

```java
public class ExpenseTransaction extends BaseTransaction {

    private String category;

    public ExpenseTransaction(int txnId, BigDecimal amount,
                              LocalDate txnDate, String description,
                              String category) {
        super(txnId, amount, txnDate, description);
        this.category = category;
    }

    @Override
    public String getType() { return "EXPENSE"; }

    public String getCategory() { return category; }
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
package com.smartbudget.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseTransaction extends BaseTransaction {

    private String category;        // e.g. "Food", "Transport"

    public ExpenseTransaction(int txnId, BigDecimal amount,
                              LocalDate txnDate, String description) {
        this(txnId, amount, txnDate, description, null);
    }

    public ExpenseTransaction(int txnId, BigDecimal amount,
                              LocalDate txnDate, String description,
                              String category) {
        super(txnId, amount, txnDate, description);
        this.category = category;
    }

    @Override
    public String getType() {
        return "EXPENSE";
    }

    public String getCategory()              { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return super.toString() + (category != null ? " (" + category + ")" : "");
    }
}
```

Polymorphism check:

```java
List<BaseTransaction> all = new ArrayList<>();
all.add(new IncomeTransaction (1, new BigDecimal("3500"), LocalDate.now(), "Salary"));
all.add(new ExpenseTransaction(2, new BigDecimal("45"),   LocalDate.now(), "Groceries", "Food"));
for (BaseTransaction t : all) {
    System.out.println(t.getType() + " -> " + t);
}
// INCOME  -> [INCOME] id=1 | 3500 | ... | Salary
// EXPENSE -> [EXPENSE] id=2 | 45  | ... | Groceries (Food)
```

</details>

---

### TICKET-F024: Custom Exception
**File:** `backend/src/main/java/com/smartbudget/exception/InvalidTransactionException.java` (create this file)

**Description:** Create a custom runtime exception for invalid transaction data.

**What**
- A `public class InvalidTransactionException extends RuntimeException` with a `(String message)` constructor that calls `super(message)`.

**Why**
- A named exception type lets callers `catch (InvalidTransactionException e)` specifically instead of catching every `RuntimeException`, and the name itself tells you the failure category at a glance in stack traces.

**Observe**
- `BaseTransaction`'s constructor compiles without any `throws` clause (because it's a `RuntimeException`, not a checked one).
- Constructing with `new BigDecimal("-10")` throws `InvalidTransactionException: Amount must be greater than zero, got: -10`.

**Instructions:**
1. Create a class that extends `RuntimeException`
2. Add a constructor that accepts a `String message` and calls `super(message)`
3. This is the exception thrown by BaseTransaction's constructor when validation fails

**Acceptance Criteria:**
- [ ] Class extends `RuntimeException` (not `Exception`)
- [ ] Constructor accepts a message String
- [ ] Creating a transaction with amount = -10 produces: `InvalidTransactionException: Amount must be greater than zero`
- [ ] The message is clear and specific about what went wrong

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Six lines of Java. `extends RuntimeException` (not `Exception` — that's a *checked* exception which forces callers to catch). Constructor takes a `String message` and passes it up via `super(message)`. That's it.

</details>

<details>
<summary><b>Hint 2 — Class structure</b></summary>

```java
public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException(String message) {
        super(message);
    }
}
```

Optionally add a second constructor accepting `(String message, Throwable cause)` for chaining — useful when wrapping a parsing error.

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
package com.smartbudget.exception;

public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException(String message) {
        super(message);
    }

    public InvalidTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

`RuntimeException` vs `Exception`:
- `RuntimeException` → *unchecked*. Caller doesn't have to wrap in try/catch. Right choice for programmer errors / invalid state.
- `Exception` → *checked*. Compiler forces `throws` clause or try/catch on every call site. Right choice for predictable, recoverable failures (e.g., `IOException`).

Invalid input is a programmer error from the caller's perspective, so unchecked is correct here.

</details>

---

### TICKET-F025: Polymorphism Demo
**File:** N/A (test in Main.java or a separate test file)

**Description:** Demonstrate polymorphism by storing different transaction types in one list.

**What**
- A small `main` (or test) that builds a `List<BaseTransaction>` containing both `IncomeTransaction` and `ExpenseTransaction` instances, then loops printing `getType()` and `toString()` for each.

**Why**
- This is the payoff for the abstract class — the loop has no `instanceof` checks and no `if/else` on type, yet every row prints with the right prefix. That's dynamic dispatch, and it's what makes the rest of the service-layer code clean.

**Observe**
- Running the demo prints alternating `[INCOME] ...` and `[EXPENSE] ...` lines from a single loop body.
- Removing the `@Override getType()` from one subclass makes the file fail to compile (proving the abstract method is what wires the dispatch).

**Instructions:**
1. Create a `List<BaseTransaction>` (the variable type is the parent class)
2. Add both `IncomeTransaction` and `ExpenseTransaction` objects to the same list
3. Loop through the list and call `getType()` on each -- observe different results
4. Call `toString()` on each -- observe different prefixes

**Acceptance Criteria:**
- [ ] A single `List<BaseTransaction>` holds both Income and Expense transactions
- [ ] Looping and calling `getType()` returns "INCOME" or "EXPENSE" depending on the actual type
- [ ] This works without any `instanceof` checks -- that is polymorphism

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Declare the list with the *parent* type (`List<BaseTransaction>`), add both subclass instances to it, then loop and call `.getType()` and `.toString()`. The fact that the call automatically dispatches to the right subclass override — without any `if (t instanceof IncomeTransaction)` — IS polymorphism.

</details>

<details>
<summary><b>Hint 2 — Skeleton</b></summary>

```java
List<BaseTransaction> mixed = new ArrayList<>();
mixed.add(new IncomeTransaction (1, new BigDecimal("3500"), LocalDate.now(), "Salary"));
mixed.add(new ExpenseTransaction(2, new BigDecimal("45"),   LocalDate.now(), "Groceries"));
mixed.add(new IncomeTransaction (3, new BigDecimal("800"),  LocalDate.now(), "Freelance"));
mixed.add(new ExpenseTransaction(4, new BigDecimal("120"),  LocalDate.now(), "Bills"));

for (BaseTransaction t : mixed) {
    System.out.println(t.getType() + ": " + t);
}
```

</details>

<details>
<summary><b>Hint 3 — Full demo class</b></summary>

```java
package com.smartbudget.console;

import com.smartbudget.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class PolymorphismDemo {
    public static void main(String[] args) {
        List<BaseTransaction> mixed = new ArrayList<>();
        mixed.add(new IncomeTransaction (1, new BigDecimal("3500"),
                LocalDate.now(), "Salary"));
        mixed.add(new ExpenseTransaction(2, new BigDecimal("45"),
                LocalDate.now(), "Groceries", "Food"));
        mixed.add(new IncomeTransaction (3, new BigDecimal("800"),
                LocalDate.now(), "Freelance"));
        mixed.add(new ExpenseTransaction(4, new BigDecimal("120"),
                LocalDate.now(), "Bills", "Utilities"));

        System.out.println("Total rows: " + mixed.size());
        for (BaseTransaction t : mixed) {
            System.out.println(t);    // toString() dispatches to subclass's getType()
        }

        // Sum income with no instanceof:
        BigDecimal income = BigDecimal.ZERO;
        for (BaseTransaction t : mixed) {
            if ("INCOME".equals(t.getType())) income = income.add(t.getAmount());
        }
        System.out.println("Total income: " + income);
    }
}
```

Expected output:

```
Total rows: 4
[INCOME] id=1 | 3500 | 2026-06-22 | Salary
[EXPENSE] id=2 | 45 | 2026-06-22 | Groceries (Food)
[INCOME] id=3 | 800 | 2026-06-22 | Freelance
[EXPENSE] id=4 | 120 | 2026-06-22 | Bills (Utilities)
Total income: 4300
```

The compiler only knows `mixed` contains `BaseTransaction` objects. At runtime, each `.toString()` and `.getType()` call dispatches to the *actual* subclass's implementation. That's dynamic dispatch — the heart of polymorphism.

</details>

---

### TICKET-F026: TransactionService -- CRUD Basics
**File:** `backend/src/main/java/com/smartbudget/service/TransactionService.java`

**Description:** Build a service class with basic Create and Read operations using ArrayList.

**What**
- A `TransactionService` class holding `private final List<BaseTransaction> transactions = new ArrayList<>()` plus `addTransaction(BaseTransaction)` and `getAll()` methods, where `getAll()` returns a defensive copy.

**Why**
- The service is the layer that everything else (CSV, REST controllers on Day 6, React on Day 8) calls into — encapsulating the list behind methods now means you can swap `ArrayList` for a JDBC-backed store on Day 4 without breaking callers.

**Observe**
- After three `addTransaction` calls, `svc.getAll().size()` returns 3.
- Calling `svc.getAll().clear()` does not change `svc.getAll().size()` on the next call — proof the defensive copy worked.

**Instructions (follow the TODOs in TransactionService.java):**

**Step 1 -- Storage field:**
- Declare `private List<BaseTransaction> transactions = new ArrayList<>()`

**Step 2 -- CRUD methods:**
- `addTransaction(BaseTransaction t)` -- adds to the list
- `getAll()` -- returns a defensive copy: `new ArrayList<>(transactions)`

**Acceptance Criteria:**
- [ ] `addTransaction` adds items to the internal list
- [ ] `getAll()` returns a copy (modifying the returned list does not affect the original)
- [ ] Adding 3 items then calling `getAll().size()` returns 3

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`private List<BaseTransaction> transactions = new ArrayList<>();`. `addTransaction(t)` just calls `transactions.add(t)`. `getAll()` returns `new ArrayList<>(transactions)` — the **defensive copy** is the important part: it stops outside code from mutating your internal state.

</details>

<details>
<summary><b>Hint 2 — Skeleton</b></summary>

```java
public class TransactionService {

    private final List<BaseTransaction> transactions = new ArrayList<>();

    public void addTransaction(BaseTransaction t) {
        transactions.add(t);
    }

    public List<BaseTransaction> getAll() {
        return new ArrayList<>(transactions);   // defensive copy
    }
}
```

Why defensive copy? If you return `transactions` directly, a caller could do `service.getAll().clear()` and silently wipe your data. With a copy, their clear only affects their copy.

</details>

<details>
<summary><b>Hint 3 — Full solution (file scaffold)</b></summary>

```java
package com.smartbudget.service;

import com.smartbudget.model.BaseTransaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionService {

    private final List<BaseTransaction> transactions = new ArrayList<>();

    public void addTransaction(BaseTransaction t) {
        transactions.add(t);
    }

    /** Defensive copy — caller mutations don't leak into our state. */
    public List<BaseTransaction> getAll() {
        return new ArrayList<>(transactions);
    }

    /** Read-only alternative — fails fast on attempted mutation. */
    public List<BaseTransaction> getAllUnmodifiable() {
        return Collections.unmodifiableList(transactions);
    }

    public int size() {
        return transactions.size();
    }
}
```

Test:

```java
TransactionService svc = new TransactionService();
svc.addTransaction(new IncomeTransaction(1, new BigDecimal("100"),
        LocalDate.now(), "Test"));
svc.addTransaction(new ExpenseTransaction(2, new BigDecimal("20"),
        LocalDate.now(), "Test"));

List<BaseTransaction> view = svc.getAll();
view.clear();                         // does NOT touch internal list
System.out.println(svc.size());       // still 2 — defensive copy worked
```

</details>

---

### TICKET-F027: Filter by Date Range
**File:** `backend/src/main/java/com/smartbudget/service/TransactionService.java`

**Description:** Add a method to filter transactions within a date range.

**What**
- A `filterByDateRange(LocalDate from, LocalDate to)` method on `TransactionService` that returns a new `List<BaseTransaction>` containing only transactions whose `txnDate` falls inside `[from, to]` inclusive.

**Why**
- Date-range filtering is the single most common query on the React dashboard (Day 8) — building it here, in the service layer, means the controller stays a thin pass-through.

**Observe**
- Given seed data on Jan 5, Feb 15, and Mar 25, `filterByDateRange(2026-01-01, 2026-01-31)` returns 1 row; `filterByDateRange(2026-01-05, 2026-03-25)` returns all 3 (boundary dates included).
- A reversed range returns an empty list, not null.

**Instructions:**
- Method: `filterByDateRange(LocalDate from, LocalDate to)` returns `List<BaseTransaction>`
- Loop through transactions, check if date is >= from AND <= to
- Use `!date.isBefore(from) && !date.isAfter(to)`

**Acceptance Criteria:**
- [ ] Returns only transactions within the specified date range
- [ ] Transactions exactly on the boundary dates are included
- [ ] Empty range returns empty list (not null)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

For-each loop, check date is inside `[from, to]` *inclusive*. The trick: `LocalDate` has `isBefore()` and `isAfter()` but no built-in "between". Use the inverses: `!d.isBefore(from) && !d.isAfter(to)` — which reads as "not before from AND not after to" = "inside the range inclusive".

</details>

<details>
<summary><b>Hint 2 — Method body</b></summary>

```java
public List<BaseTransaction> filterByDateRange(LocalDate from, LocalDate to) {
    List<BaseTransaction> result = new ArrayList<>();
    for (BaseTransaction t : transactions) {
        LocalDate d = t.getTxnDate();
        if (!d.isBefore(from) && !d.isAfter(to)) {
            result.add(t);
        }
    }
    return result;
}
```

Boundary check: if a transaction is on exactly `from`, then `d.isBefore(from)` is `false`, so `!d.isBefore(from)` is `true` — included. ✓

</details>

<details>
<summary><b>Hint 3 — Full solution + tests</b></summary>

```java
public List<BaseTransaction> filterByDateRange(LocalDate from, LocalDate to) {
    if (from == null || to == null) {
        throw new IllegalArgumentException("from and to must be non-null");
    }
    if (from.isAfter(to)) {
        return new ArrayList<>();           // empty range, not error
    }
    List<BaseTransaction> result = new ArrayList<>();
    for (BaseTransaction t : transactions) {
        LocalDate d = t.getTxnDate();
        if (!d.isBefore(from) && !d.isAfter(to)) {
            result.add(t);
        }
    }
    return result;
}
```

Smoke test:

```java
svc.addTransaction(new IncomeTransaction(1, new BigDecimal("100"),
        LocalDate.of(2026, 1,  5), "Jan"));
svc.addTransaction(new IncomeTransaction(2, new BigDecimal("100"),
        LocalDate.of(2026, 2, 15), "Feb"));
svc.addTransaction(new IncomeTransaction(3, new BigDecimal("100"),
        LocalDate.of(2026, 3, 25), "Mar"));

List<BaseTransaction> jan = svc.filterByDateRange(
        LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
System.out.println(jan.size());     // 1 — only the Jan row

List<BaseTransaction> q1 = svc.filterByDateRange(
        LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31));
System.out.println(q1.size());      // 3 — all three rows

List<BaseTransaction> reversed = svc.filterByDateRange(
        LocalDate.of(2026, 3, 1), LocalDate.of(2026, 1, 1));
System.out.println(reversed.size()); // 0 — empty range, not an error
```

</details>

---

### TICKET-F028: Calculate Total by Type
**File:** `backend/src/main/java/com/smartbudget/service/TransactionService.java`

**Description:** Sum all amounts for a given type (INCOME or EXPENSE).

**What**
- A `calculateTotalByType(String type)` method that returns a `BigDecimal` total, accumulated via `total = total.add(t.getAmount())` for every transaction whose `getType()` matches.

**Why**
- This is the building block for "total income", "total expenses", and "net balance" — the three numbers on the dashboard summary card. Doing it in `BigDecimal` (not `double`) avoids the `0.1 + 0.2 = 0.30000000000000004` class of bugs that quietly break finance code.

**Observe**
- With seed data containing INCOME rows of 3500 + 800 = 4300, `calculateTotalByType("INCOME")` returns exactly `4300` (printed as `4300` not `4299.999...`).
- `calculateTotalByType("UNKNOWN")` returns `0`, not null.

**Instructions:**
- Method: `calculateTotalByType(String type)` returns `BigDecimal`
- Start with `BigDecimal.ZERO`
- Loop through transactions, if `getType()` matches, add the amount
- Use `BigDecimal.add()` (not the `+` operator)

**Acceptance Criteria:**
- [ ] Returns correct sum for "INCOME" transactions
- [ ] Returns correct sum for "EXPENSE" transactions
- [ ] Returns `BigDecimal.ZERO` when no matching transactions exist
- [ ] Uses `BigDecimal` arithmetic throughout (no `double`)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Accumulate into a `BigDecimal` (start at `BigDecimal.ZERO`). For each transaction whose `getType()` equals the requested type, do `total = total.add(t.getAmount())`. Remember `BigDecimal` is immutable — `total.add(...)` returns a new object you have to reassign.

</details>

<details>
<summary><b>Hint 2 — Method body</b></summary>

```java
public BigDecimal calculateTotalByType(String type) {
    BigDecimal total = BigDecimal.ZERO;
    for (BaseTransaction t : transactions) {
        if (type.equals(t.getType())) {
            total = total.add(t.getAmount());
        }
    }
    return total;
}
```

Use `type.equals(t.getType())` (not `t.getType().equals(type)`) so a null `type` argument throws a clear NPE on entry rather than later inside the loop.

</details>

<details>
<summary><b>Hint 3 — Full solution + stream variant</b></summary>

```java
public BigDecimal calculateTotalByType(String type) {
    if (type == null) {
        throw new IllegalArgumentException("type must not be null");
    }
    BigDecimal total = BigDecimal.ZERO;
    for (BaseTransaction t : transactions) {
        if (type.equals(t.getType())) {
            total = total.add(t.getAmount());
        }
    }
    return total;
}

// Bonus: stream version — same behaviour, more idiomatic from Java 8 onwards
public BigDecimal calculateTotalByTypeStream(String type) {
    return transactions.stream()
            .filter(t -> type.equals(t.getType()))
            .map(BaseTransaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}
```

Test:

```java
System.out.println(svc.calculateTotalByType("INCOME"));    // sum of all INCOME
System.out.println(svc.calculateTotalByType("EXPENSE"));   // sum of all EXPENSE
System.out.println(svc.calculateTotalByType("UNKNOWN"));   // 0 (no match)
```

Convenience methods you might add: `getTotalIncome()` / `getTotalExpenses()` / `getNetBalance()` — they all just call `calculateTotalByType` internally.

</details>

---

### TICKET-F029: Export to CSV
**File:** `backend/src/main/java/com/smartbudget/service/TransactionService.java`

**Description:** Write all transactions to a CSV file.

**What**
- An `exportToCSV(String filePath) throws IOException` method that uses try-with-resources around a `BufferedWriter`/`FileWriter`, writes a `id,type,amount,date,description` header, then one CSV row per transaction.

**Why**
- CSV is the lowest-common-denominator interchange format — finance users export to Excel, compliance archives to S3, and Day 4's JDBC code can seed itself from these files. Try-with-resources is what guarantees the file handle is released even when an `IOException` fires mid-write.

**Observe**
- After `svc.exportToCSV("transactions.csv")`, the file exists on disk and opens cleanly in Excel with 5 columns.
- `head -1 transactions.csv` prints exactly `id,type,amount,date,description`.

**Instructions:**
- Method: `exportToCSV(String filePath)`
- Use `BufferedWriter` wrapping `FileWriter`
- Write header line: `id,type,amount,date,description`
- Write each transaction as a CSV row
- Use try-with-resources to auto-close the writer

**Acceptance Criteria:**
- [ ] File is created at the specified path
- [ ] First line is the header
- [ ] Each subsequent line is one transaction with comma-separated values
- [ ] File can be opened in Excel and displays correctly

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) { ... }`. Inside the try, write the header line, then loop and write each transaction with commas between fields. Try-with-resources auto-closes the writer even if an exception is thrown. Throw or rethrow `IOException` from the method signature.

</details>

<details>
<summary><b>Hint 2 — Method body</b></summary>

```java
public void exportToCSV(String filePath) throws IOException {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
        bw.write("id,type,amount,date,description");
        bw.newLine();
        for (BaseTransaction t : transactions) {
            bw.write(t.getTxnId() + "," + t.getType() + ","
                   + t.getAmount() + "," + t.getTxnDate() + ","
                   + safe(t.getDescription()));
            bw.newLine();
        }
    }
}

private static String safe(String s) {
    if (s == null) return "";
    return s.contains(",") || s.contains("\"")
            ? "\"" + s.replace("\"", "\"\"") + "\""
            : s;
}
```

The `safe()` helper quotes any description that contains commas or quotes — otherwise `"Hello, world"` would be split into two columns by readers.

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
public void exportToCSV(String filePath) throws IOException {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
        // Header
        bw.write("id,type,amount,date,description");
        bw.newLine();

        // Data rows
        for (BaseTransaction t : transactions) {
            bw.write(String.join(",",
                    String.valueOf(t.getTxnId()),
                    t.getType(),
                    t.getAmount().toPlainString(),
                    t.getTxnDate().toString(),
                    csvEscape(t.getDescription())
            ));
            bw.newLine();
        }
    }
}

private static String csvEscape(String s) {
    if (s == null) return "";
    if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
    return s;
}
```

Try it:

```java
svc.exportToCSV("transactions.csv");
```

Resulting file:

```
id,type,amount,date,description
1,INCOME,3500.00,2026-01-01,January salary
2,EXPENSE,45.20,2026-01-08,Groceries
3,EXPENSE,25.00,2026-01-15,Bus pass
```

Why `toPlainString()` on the BigDecimal? `BigDecimal.toString()` can emit scientific notation (`1E+3`); `toPlainString()` always gives a plain decimal (`1000`).

</details>

---

### TICKET-F030: Import from CSV
**File:** `backend/src/main/java/com/smartbudget/service/TransactionService.java`

**Description:** Read transactions from a CSV file and add them to the list.

**What**
- An `importFromCSV(String filePath) throws IOException` method that uses a `BufferedReader`, skips the header line, splits each row by comma, parses the fields, and instantiates either an `IncomeTransaction` or `ExpenseTransaction` based on the `type` column.

**Why**
- Import + export together form the round-trip that proves your model is faithful — if you can export then re-import without losing data, the in-memory shape and the on-disk shape match. That round-trip is also how Day 4's JDBC layer gets seeded for repeatable tests.

**Observe**
- `b.importFromCSV("transactions.csv")` followed by `b.getAll().size()` returns the same count that `a.exportToCSV` wrote (e.g., 2).
- `b.calculateTotalByType("INCOME")` matches `a.calculateTotalByType("INCOME")` to the cent.

**Instructions:**
- Method: `importFromCSV(String filePath)`
- Use `BufferedReader` wrapping `FileReader`
- Skip the header line
- Split each line by comma, parse fields, create IncomeTransaction or ExpenseTransaction
- Add each to the transactions list

**Acceptance Criteria:**
- [ ] Export then import produces the same data
- [ ] Header line is skipped (not treated as a transaction)
- [ ] Correct transaction type is created (INCOME or EXPENSE) based on the CSV data
- [ ] File not found produces a clear error message

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Reverse of F029. Use `BufferedReader` in try-with-resources, read line by line. Skip the first line (header). Split the rest on `,`, parse each field, and instantiate either `IncomeTransaction` or `ExpenseTransaction` based on the `type` column. Add each to the list (don't replace existing entries).

</details>

<details>
<summary><b>Hint 2 — Method body</b></summary>

```java
public void importFromCSV(String filePath) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        String line = br.readLine();        // skip header
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",", -1);   // -1 keeps trailing empties
            int id            = Integer.parseInt(parts[0]);
            String type       = parts[1];
            BigDecimal amount = new BigDecimal(parts[2]);
            LocalDate date    = LocalDate.parse(parts[3]);
            String desc       = parts.length > 4 ? parts[4] : "";

            BaseTransaction t = "INCOME".equals(type)
                ? new IncomeTransaction (id, amount, date, desc)
                : new ExpenseTransaction(id, amount, date, desc);
            transactions.add(t);
        }
    }
}
```

The simple split breaks on quoted descriptions with commas — a real CSV parser (Apache Commons CSV, OpenCSV) handles those properly, but for now the simple split is fine if your descriptions are quote-free.

</details>

<details>
<summary><b>Hint 3 — Full solution + round-trip test</b></summary>

```java
public void importFromCSV(String filePath) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        String line = br.readLine();
        if (line == null) return;                    // empty file

        int lineNum = 1;
        while ((line = br.readLine()) != null) {
            lineNum++;
            if (line.isBlank()) continue;
            try {
                String[] parts = line.split(",", -1);
                int id            = Integer.parseInt(parts[0].trim());
                String type       = parts[1].trim();
                BigDecimal amount = new BigDecimal(parts[2].trim());
                LocalDate date    = LocalDate.parse(parts[3].trim());
                String desc       = parts.length > 4 ? parts[4] : "";

                BaseTransaction t = switch (type) {
                    case "INCOME"  -> new IncomeTransaction (id, amount, date, desc);
                    case "EXPENSE" -> new ExpenseTransaction(id, amount, date, desc);
                    default -> throw new InvalidTransactionException(
                            "Unknown type on line " + lineNum + ": " + type);
                };
                transactions.add(t);
            } catch (NumberFormatException | DateTimeParseException e) {
                System.err.println("Skipping bad row at line "
                        + lineNum + ": " + e.getMessage());
            }
        }
    }
}
```

Round-trip test:

```java
TransactionService a = new TransactionService();
a.addTransaction(new IncomeTransaction (1, new BigDecimal("100"),
        LocalDate.now(), "X"));
a.addTransaction(new ExpenseTransaction(2, new BigDecimal("50"),
        LocalDate.now(), "Y"));
a.exportToCSV("tmp.csv");

TransactionService b = new TransactionService();
b.importFromCSV("tmp.csv");
System.out.println(b.getAll().size());   // 2  ✓ round-trip preserved
System.out.println(b.calculateTotalByType("INCOME"));   // 100
System.out.println(b.calculateTotalByType("EXPENSE"));  //  50
```

Note: `FileReader` throws `FileNotFoundException` (which is an `IOException`). Catch it in your caller and print a clear "file not found at path: ..." rather than letting the raw stacktrace fly.

</details>

---

### TICKET-F031: Validate on Add
**File:** `backend/src/main/java/com/smartbudget/service/TransactionService.java`

**Description:** Add validation rules in the service before adding a transaction.

**What**
- Two extra guards at the top of `addTransaction(BaseTransaction t)`: throw `IllegalArgumentException` if `t` is null, and throw `InvalidTransactionException` if `t.getDescription()` is null or blank.

**Why**
- Defence in depth — `BaseTransaction`'s constructor catches malformed objects, but the service guards against missing or whitespace-only descriptions that the model layer doesn't care about but the dashboard does (a blank row is useless to a user).

**Observe**
- `svc.addTransaction(null)` throws `IllegalArgumentException: transaction must not be null`; passing a transaction with `description = "   "` throws `InvalidTransactionException: description must not be blank...`.
- A valid transaction still lands in `getAll()`.

**Instructions:**
- Before adding to the list, check:
  - Transaction is not null (throw IllegalArgumentException)
  - Description is not blank
- The amount and date validation already happens in BaseTransaction's constructor

**Acceptance Criteria:**
- [ ] Adding null throws an exception
- [ ] Adding a transaction with blank description is rejected
- [ ] Valid transactions are still added successfully

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Two extra guards at the top of `addTransaction`: null-check the argument, and check `description` isn't blank. Amount/date validation already runs inside `BaseTransaction`'s constructor (F021), so don't duplicate it here.

</details>

<details>
<summary><b>Hint 2 — Updated addTransaction</b></summary>

```java
public void addTransaction(BaseTransaction t) {
    if (t == null) {
        throw new IllegalArgumentException("transaction must not be null");
    }
    if (t.getDescription() == null || t.getDescription().isBlank()) {
        throw new InvalidTransactionException("description must not be blank");
    }
    transactions.add(t);
}
```

`String.isBlank()` (Java 11+) returns `true` for null-ish strings: `""`, `"   "`, etc.

</details>

<details>
<summary><b>Hint 3 — Full solution + tests</b></summary>

```java
public void addTransaction(BaseTransaction t) {
    if (t == null) {
        throw new IllegalArgumentException("transaction must not be null");
    }
    if (t.getDescription() == null || t.getDescription().isBlank()) {
        throw new InvalidTransactionException(
            "description must not be blank for transaction id=" + t.getTxnId());
    }
    transactions.add(t);
}
```

Failing tests:

```java
TransactionService svc = new TransactionService();

// 1. null
try {
    svc.addTransaction(null);
} catch (IllegalArgumentException e) {
    System.out.println("Rejected null: " + e.getMessage());
}

// 2. blank description
try {
    svc.addTransaction(new IncomeTransaction(1, new BigDecimal("100"),
            LocalDate.now(), "   "));
} catch (InvalidTransactionException e) {
    System.out.println("Rejected blank desc: " + e.getMessage());
}

// 3. negative amount — caught by BaseTransaction's constructor, not here
try {
    svc.addTransaction(new IncomeTransaction(2, new BigDecimal("-10"),
            LocalDate.now(), "ok"));
} catch (InvalidTransactionException e) {
    System.out.println("Rejected negative amount: " + e.getMessage());
}

// 4. valid
svc.addTransaction(new IncomeTransaction(3, new BigDecimal("500"),
        LocalDate.now(), "Salary"));
System.out.println(svc.getAll().size());   // 1 — only the valid one
```

Defence-in-depth principle: validation in the model layer (F021) catches bad construction; validation in the service layer (F031) catches bad business operations. Both layers, both guards.

</details>

---

## End-of-Day Checklist

- [ ] `BaseTransaction` is abstract with validated constructor
- [ ] `IncomeTransaction` and `ExpenseTransaction` extend it
- [ ] `InvalidTransactionException` is a custom RuntimeException
- [ ] Polymorphism works -- one list holds both types
- [ ] TransactionService has CRUD, filtering, totals, CSV import/export
- [ ] You can explain: abstract class, inheritance, polymorphism, protected vs private, defensive copy

---

*Tomorrow (Day 4): You will connect to a real database using JDBC and learn HashMap, Streams, and testing.*
