# Day 6 -- REST APIs & Service Layer (Sprint 5)

> TICKET-F056 through TICKET-F068

---

## Overview

Today you build the **service layer** and **refactor the controllers** to use proper architecture. You will also write integration tests and test with Postman.

By the end of Day 6, the backend follows the standard 3-layer architecture: Controller -> Service -> Repository.

---

## Key Concepts

- **Service layer**: Contains business logic (validation, calculations, error handling)
- **Controller refactoring**: Move from "controller calls repository directly" to "controller calls service"
- **@Service annotation**: Tells Spring to manage this class as a bean
- **@RestController**: Marks a class as an HTTP endpoint handler
- **@RequestBody**: Parses incoming JSON into a Java object
- **@PathVariable**: Extracts values from the URL path
- **Integration tests**: Test the full HTTP request/response cycle with MockMvc
- **GlobalExceptionHandler**: Catches exceptions and returns proper HTTP error responses

---

## Architecture Before vs After

```
BEFORE (provided code):                AFTER (your refactored code):
Controller -> Repository               Controller -> Service -> Repository
(no validation, no error handling)      (validation, exceptions, business rules)
```

---

## Tickets

### TICKET-F056: TransactionController -- Annotations & GET All
**File:** `backend/src/main/java/com/smartbudget/controller/TransactionController.java`

**Description:** Add @RestController, @RequestMapping, and implement GET all transactions.

**What**
- A `TransactionController` class annotated `@RestController` + `@RequestMapping("/api/transactions")` with a `getAll()` method that returns `repo.findAll()` as JSON.

**Why**
- This is the HTTP entry point — without it Spring won't expose anything at `/api/transactions` and the React frontend on Day 8 has nothing to fetch from.

**Observe**
- `curl -i http://localhost:8080/api/transactions` returns `HTTP/1.1 200`, `Content-Type: application/json`, and a JSON array of the seeded transactions.

**Instructions (follow Steps 1 and 3 in the file):**
1. Add `@RestController` and `@RequestMapping("/api/transactions")` to the class
2. Implement a GET method that returns all transactions
3. Use `@GetMapping` annotation

**Acceptance Criteria:**
- [ ] GET http://localhost:8080/api/transactions returns JSON array
- [ ] Spring logs show "Mapped ... /api/transactions"
- [ ] Response Content-Type is application/json

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Two annotations on the class — `@RestController` + `@RequestMapping("/api/transactions")` — and one annotation on a no-arg method — `@GetMapping`. Inject `TransactionRepository` (or `TransactionService` once you build it) via the constructor. Return `repo.findAll()` and Spring serialises it to JSON automatically (Jackson is on the classpath).

</details>

<details>
<summary><b>Hint 2 — Class shape</b></summary>

```java
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository repo;

    public TransactionController(TransactionRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Transaction> getAll() {
        return repo.findAll();
    }
}
```

`@RestController` = `@Controller` + `@ResponseBody` — every method returns the value as the HTTP body (JSON by default).

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
package com.smartbudget.controller;

import com.smartbudget.entity.Transaction;
import com.smartbudget.repository.TransactionRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository repo;

    public TransactionController(TransactionRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Transaction> getAll() {
        return repo.findAll();
    }
}
```

Verify:

```bash
curl -i http://localhost:8080/api/transactions
# HTTP/1.1 200
# Content-Type: application/json
# [
#   {"txnId":1, "user":{...}, "category":{...}, "amount":3500.00, ...},
#   ...
# ]
```

Spring log on startup:
```
Mapped "{[/api/transactions],methods=[GET]}" onto public List ...
```

Note: returning the entity directly works for now, but in real apps you'd use a DTO (DataTransferObject) — exposing the full entity reveals internal relationships (`@ManyToOne` round-trips, lazy-loading proxies) and creates tight coupling between API and DB. We'll get away with it for the foundation track.

</details>

---

### TICKET-F057: TransactionController -- POST Create
**File:** `backend/src/main/java/com/smartbudget/controller/TransactionController.java`

**Description:** Add POST endpoint for creating new transactions.

**What**
- A `create(@RequestBody Transaction t)` method annotated `@PostMapping` + `@ResponseStatus(HttpStatus.CREATED)` that saves the deserialised entity and returns it with the generated `txnId`.

**Why**
- Without a POST endpoint the frontend can never add data — the app would be read-only. `201 Created` is the correct status for "resource created" and signals to clients to expect a populated ID.

**Observe**
- `curl -i -X POST http://localhost:8080/api/transactions -H 'Content-Type: application/json' -d '{...}'` returns `HTTP/1.1 201` and a response body whose `txnId` is one greater than the previous max.

**Instructions (follow Step 4 in the file):**
1. Create a method with `@PostMapping` and `@ResponseStatus(HttpStatus.CREATED)`
2. Accept a `@RequestBody Transaction` parameter
3. Delegate to the service's `create()` method
4. Return the saved entity

**Acceptance Criteria:**
- [ ] POST to /api/transactions with valid JSON returns HTTP 201
- [ ] The response body contains the created transaction with a generated ID
- [ ] POST with invalid data (negative amount) returns HTTP 400 (after service validation)
- [ ] Test with Postman: send JSON body like `{"user":{"userId":1},"category":{"categoryId":1},"amount":100,"txnDate":"2026-05-01","description":"Test","type":"EXPENSE"}`

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`@PostMapping`, `@ResponseStatus(HttpStatus.CREATED)`, `@RequestBody Transaction t`. Spring uses Jackson to parse the incoming JSON into a `Transaction` object. Call `repo.save(t)` (or `service.create(...)` once the service exists) and return the saved entity — `save()` populates the auto-generated ID.

</details>

<details>
<summary><b>Hint 2 — Method body</b></summary>

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Transaction create(@RequestBody Transaction t) {
    return repo.save(t);
}
```

For validation now, you can do an inline check; we move it to the service in F063:

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Transaction create(@RequestBody Transaction t) {
    if (t.getAmount() == null || t.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new InvalidTransactionException("amount must be > 0");
    }
    return repo.save(t);
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution + curl</b></summary>

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Transaction create(@RequestBody Transaction t) {
    if (t.getAmount() == null || t.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new InvalidTransactionException("amount must be > 0");
    }
    return repo.save(t);
}
```

```bash
curl -i -X POST http://localhost:8080/api/transactions \
  -H 'Content-Type: application/json' \
  -d '{
        "user":     {"userId":1},
        "category": {"categoryId":1},
        "amount":   100.50,
        "txnDate":  "2026-05-01",
        "description": "Lunch",
        "type":     "EXPENSE"
      }'

# HTTP/1.1 201
# {"txnId":26, "user":{...}, "category":{...}, "amount":100.5, ...}
```

Postman: New Request → POST → URL `http://localhost:8080/api/transactions` → Body tab → raw → JSON. Paste the same body. Send. Should see `201 Created`.

Negative-amount path:
```bash
curl -i -X POST http://localhost:8080/api/transactions \
  -H 'Content-Type: application/json' \
  -d '{"user":{"userId":1},"category":{"categoryId":1},"amount":-50,...}'
# HTTP/1.1 400  (once F065 GlobalExceptionHandler is in place)
```

Why nested `{"user":{"userId":1}}` rather than `"userId":1`? Because the entity has `private User user;` (not `private Long userId;`). Jackson needs the JSON shape to mirror the Java structure — we'd reshape this with a DTO in a real API.

</details>

---

### TICKET-F058: TransactionController -- GET by User
**File:** `backend/src/main/java/com/smartbudget/controller/TransactionController.java`

**Description:** Add GET endpoint filtered by user ID.

**What**
- A `getByUser(@PathVariable Long userId)` method on `@GetMapping("/user/{userId}")` that returns one user's transactions via `findByUser_UserIdOrderByTxnDateDesc(userId)`.

**Why**
- Bulk `/api/transactions` is fine for an admin view, but the dashboard on Day 8 needs per-user filtering and pagination needs to happen on the DB side, not in JavaScript.

**Observe**
- `curl /api/transactions/user/1` returns only user 1's rows in newest-first order; `curl /api/transactions/user/999` returns `[]` with `HTTP/1.1 200` (not 404).

**Instructions (follow Step 5 in the file):**
1. Create a method with `@GetMapping("/user/{userId}")`
2. Accept `@PathVariable Long userId`
3. Delegate to service or repository's `findByUser_UserIdOrderByTxnDateDesc(userId)`

**Acceptance Criteria:**
- [ ] GET /api/transactions/user/1 returns only user 1's transactions
- [ ] GET /api/transactions/user/999 returns an empty array (not an error)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`@GetMapping("/user/{userId}")` + `@PathVariable Long userId`. The path placeholder name must match the parameter name (or rename with `@PathVariable("userId")`). Delegate to your F051 query method.

</details>

<details>
<summary><b>Hint 2 — Method body</b></summary>

```java
@GetMapping("/user/{userId}")
public List<Transaction> getByUser(@PathVariable Long userId) {
    return repo.findByUser_UserIdOrderByTxnDateDesc(userId);
}
```

A non-existent userId returns `[]` (empty list), not 404 — "no transactions for this user" is a valid answer.

</details>

<details>
<summary><b>Hint 3 — Full solution + curl</b></summary>

```java
@GetMapping("/user/{userId}")
public List<Transaction> getByUser(@PathVariable Long userId) {
    return repo.findByUser_UserIdOrderByTxnDateDesc(userId);
}
```

```bash
curl http://localhost:8080/api/transactions/user/1     # → list
curl http://localhost:8080/api/transactions/user/999   # → []
```

Spring log on startup:
```
Mapped "{[/api/transactions/user/{userId}],methods=[GET]}" onto ...
```

If you want 404 for "user doesn't exist at all" (vs "user exists but has no txns"), look up the user first:

```java
@GetMapping("/user/{userId}")
public List<Transaction> getByUser(@PathVariable Long userId) {
    if (!userRepository.existsById(userId)) {
        throw new ResourceNotFoundException("User " + userId + " not found");
    }
    return repo.findByUser_UserIdOrderByTxnDateDesc(userId);
}
```

For this ticket the simpler version is enough — the criterion says empty array is correct.

</details>

---

### TICKET-F059: TransactionController -- DELETE
**File:** `backend/src/main/java/com/smartbudget/controller/TransactionController.java`

**Description:** Add DELETE endpoint to remove a transaction.

**What**
- A void `delete(@PathVariable Long id)` method on `@DeleteMapping("/{id}")` annotated `@ResponseStatus(HttpStatus.NO_CONTENT)` that checks `existsById` then `deleteById`.

**Why**
- Completes the CRUD surface and proves the GlobalExceptionHandler wiring: missing IDs become 404 instead of silently returning 204.

**Observe**
- `curl -i -X DELETE /api/transactions/1` returns `HTTP/1.1 204` with no body; `curl -i -X DELETE /api/transactions/9999` returns `HTTP/1.1 404` with a `ResourceNotFoundException` message.

**Instructions (follow Step 6 in the file):**
1. Create a void method with `@DeleteMapping("/{id}")`
2. Add `@ResponseStatus(HttpStatus.NO_CONTENT)` (HTTP 204)
3. Delegate to service's `delete()` method

**Acceptance Criteria:**
- [ ] DELETE /api/transactions/1 returns HTTP 204 (no body)
- [ ] The transaction no longer appears in GET /api/transactions
- [ ] DELETE /api/transactions/999 returns HTTP 404 (after GlobalExceptionHandler)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`@DeleteMapping("/{id}")`, `@ResponseStatus(HttpStatus.NO_CONTENT)`, method returns `void`. Check existence first with `existsById` and throw `ResourceNotFoundException` if it's missing — the GlobalExceptionHandler (F065) converts that to 404.

</details>

<details>
<summary><b>Hint 2 — Method body</b></summary>

```java
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable Long id) {
    if (!repo.existsById(id)) {
        throw new ResourceNotFoundException("Transaction " + id + " not found");
    }
    repo.deleteById(id);
}
```

204 No Content means "we did the thing; there's nothing to return". Don't return a body.

</details>

<details>
<summary><b>Hint 3 — Full solution + curl</b></summary>

```java
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable Long id) {
    if (!repo.existsById(id)) {
        throw new ResourceNotFoundException("Transaction " + id + " not found");
    }
    repo.deleteById(id);
}
```

Verify:

```bash
curl -i -X DELETE http://localhost:8080/api/transactions/1
# HTTP/1.1 204
# (no body)

curl -i -X DELETE http://localhost:8080/api/transactions/9999
# HTTP/1.1 404
# {"error":"Transaction 9999 not found"}     (once F065 is in place)

curl -s http://localhost:8080/api/transactions | jq 'length'
# 24       — one fewer than before
```

Without the `existsById` guard, deleting a missing id silently returns 204 (Spring Data's `deleteById` is a no-op when the id doesn't exist). The check turns "did nothing" into a clear 404 — better UX for API clients.

</details>

---

### TICKET-F060: UserController -- Refactor
**File:** `backend/src/main/java/com/smartbudget/controller/UserController.java`

**Description:** Refactor the user controller to use proper annotations and service layer.

**What**
- A `UserController` annotated `@RestController` + `@RequestMapping("/api/users")` with `getAll`, `create` (201), and `getById` using `findById(id).orElseThrow(...)`.

**Why**
- The Day 2/3 starter wired this controller directly to a HashMap; refactoring to JPA + `orElseThrow` is what turns this into a real REST resource and proves the 3-layer pattern generalises beyond `Transaction`.

**Observe**
- `curl /api/users` returns all 5 seeded users; `curl /api/users/999` returns `HTTP/1.1 404`; `curl -X POST /api/users -d '{"name":"Frank","email":"frank@bank.com"}'` returns `HTTP/1.1 201`.

**Instructions (follow the TODOs in the file):**
1. Add @RestController and @RequestMapping
2. Inject the repository (or service if you build one)
3. Implement GET all, POST create, GET by ID with `orElseThrow`

**Acceptance Criteria:**
- [ ] GET /api/users returns all users
- [ ] POST /api/users creates a new user and returns HTTP 201
- [ ] GET /api/users/1 returns user 1
- [ ] GET /api/users/999 returns HTTP 404

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Same template as `TransactionController`. Three endpoints: GET all, POST create (201), GET by id with `findById(id).orElseThrow(() -> new ResourceNotFoundException(...))`. The `orElseThrow` pattern converts an `Optional.empty()` into an exception your GlobalExceptionHandler turns into 404.

</details>

<details>
<summary><b>Hint 2 — Class shape</b></summary>

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) { this.repo = repo; }

    @GetMapping
    public List<User> getAll() { return repo.findAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User u) { return repo.save(u); }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User " + id + " not found"));
    }
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution + bonus by-email lookup</b></summary>

```java
package com.smartbudget.controller;

import com.smartbudget.entity.User;
import com.smartbudget.exception.ResourceNotFoundException;
import com.smartbudget.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<User> getAll() {
        return repo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User u) {
        if (u.getEmail() != null && repo.existsByEmail(u.getEmail())) {
            throw new InvalidTransactionException(
                "Email already taken: " + u.getEmail());
        }
        return repo.save(u);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User " + id + " not found"));
    }

    @GetMapping("/by-email/{email}")
    public User getByEmail(@PathVariable String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No user with email: " + email));
    }
}
```

```bash
curl http://localhost:8080/api/users                    # all
curl http://localhost:8080/api/users/1                  # one
curl http://localhost:8080/api/users/999                # 404
curl -X POST http://localhost:8080/api/users \
     -H 'Content-Type: application/json' \
     -d '{"name":"Frank","email":"frank@bank.com"}'     # 201
```

`orElseThrow(() -> ...)` is more readable than `if (opt.isEmpty()) throw ...; return opt.get();` — same behaviour, fewer lines.

</details>

---

### TICKET-F061: SavingsGoalController -- GET by User
**File:** `backend/src/main/java/com/smartbudget/controller/SavingsGoalController.java`

**Description:** Implement the savings goals controller.

**What**
- A `SavingsGoalController` mapped to `/api/goals` with a `byUser(@PathVariable Long userId)` method on `@GetMapping("/user/{userId}")` delegating to `SavingsGoalRepository.findByUser_UserId(userId)`.

**Why**
- Goals are the secondary domain — without this endpoint the Day 8 "Goals" page can't list a user's targets, and the contribute endpoint (F062) has nothing to read back.

**Observe**
- `curl /api/goals/user/1` returns a JSON array where each element carries `goalName`, `targetAmount`, `currentAmount`, and `deadline`.

**Instructions (follow the TODOs in the file):**
1. Add annotations and inject the repository/service
2. GET /api/goals/user/{userId} -- returns goals for a user

**Acceptance Criteria:**
- [ ] GET /api/goals/user/1 returns user 1's savings goals
- [ ] Each goal includes target amount, current amount, and deadline

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Same skeleton as TransactionController.getByUser. `@GetMapping("/user/{userId}")` + `@PathVariable Long userId`. Delegate to `SavingsGoalRepository.findByUser_UserId(userId)` (the provided repo already exposes this).

</details>

<details>
<summary><b>Hint 2 — Class shape</b></summary>

```java
@RestController
@RequestMapping("/api/goals")
public class SavingsGoalController {

    private final SavingsGoalRepository repo;

    public SavingsGoalController(SavingsGoalRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/user/{userId}")
    public List<SavingsGoal> byUser(@PathVariable Long userId) {
        return repo.findByUser_UserId(userId);
    }
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```java
package com.smartbudget.controller;

import com.smartbudget.entity.SavingsGoal;
import com.smartbudget.repository.SavingsGoalRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class SavingsGoalController {

    private final SavingsGoalRepository repo;

    public SavingsGoalController(SavingsGoalRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/user/{userId}")
    public List<SavingsGoal> byUser(@PathVariable Long userId) {
        return repo.findByUser_UserId(userId);
    }
}
```

```bash
curl http://localhost:8080/api/goals/user/1
# [
#   {"goalId":1, "user":{...}, "goalName":"Holiday Fund",
#    "targetAmount":2000.00, "currentAmount":450.00, "deadline":"2026-12-01"},
#   ...
# ]
```

</details>

---

### TICKET-F062: SavingsGoalController -- PUT Contribute
**File:** `backend/src/main/java/com/smartbudget/controller/SavingsGoalController.java`

**Description:** Add an endpoint to contribute money toward a savings goal.

**What**
- A `contribute(@PathVariable Long id, @RequestBody ContributionRequest body)` method on `@PutMapping("/{id}/contribute")` that adds the amount to `currentAmount`, rejecting `<= 0` and any total that exceeds `targetAmount`.

**Why**
- This is the first method that mutates state with real business rules — the place to demonstrate that validation belongs in the service/controller layer, not just the DB CHECK constraints from Day 1.

**Observe**
- `curl -X PUT /api/goals/1/contribute -d '{"amount":100}'` returns the updated goal with `currentAmount` increased by 100; the same call with `{"amount":0}` returns `HTTP/1.1 400`.

**Instructions (follow the TODOs in the file):**
1. PUT /api/goals/{id}/contribute
2. Accept contribution amount from the request body
3. Add the contribution to the goal's current amount
4. Validate: contribution must be > 0
5. Validate: current + contribution must not exceed target

**Acceptance Criteria:**
- [ ] PUT /api/goals/1/contribute with `{"amount": 100}` increases current amount
- [ ] Contribution of 0 or negative is rejected
- [ ] Over-contributing (exceeding target) is handled gracefully
- [ ] GET /api/goals/user/1 shows the updated amount after contributing

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`@PutMapping("/{id}/contribute")`. Read the contribution amount from a `Map<String,BigDecimal>` request body (or a tiny DTO record). Validate `> 0` and `current + contribution <= target`. Mutate `currentAmount`, save, return the updated goal.

</details>

<details>
<summary><b>Hint 2 — Method body</b></summary>

```java
public record ContributionRequest(BigDecimal amount) { }

@PutMapping("/{id}/contribute")
public SavingsGoal contribute(@PathVariable Long id,
                              @RequestBody ContributionRequest body) {
    SavingsGoal goal = repo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Goal " + id + " not found"));

    BigDecimal amount = body.amount();
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new InvalidTransactionException("Contribution must be > 0");
    }
    BigDecimal newTotal = goal.getCurrentAmount().add(amount);
    if (newTotal.compareTo(goal.getTargetAmount()) > 0) {
        throw new InvalidTransactionException(
            "Would exceed target by "
            + newTotal.subtract(goal.getTargetAmount()));
    }
    goal.setCurrentAmount(newTotal);
    return repo.save(goal);
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution + tests</b></summary>

```java
public record ContributionRequest(BigDecimal amount) { }

@PutMapping("/{id}/contribute")
public SavingsGoal contribute(@PathVariable Long id,
                              @RequestBody ContributionRequest body) {
    SavingsGoal goal = repo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Goal " + id + " not found"));

    BigDecimal amount = body.amount();
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new InvalidTransactionException("Contribution must be > 0");
    }

    BigDecimal newTotal = goal.getCurrentAmount().add(amount);
    if (newTotal.compareTo(goal.getTargetAmount()) > 0) {
        BigDecimal over = newTotal.subtract(goal.getTargetAmount());
        throw new InvalidTransactionException(
            "Contribution exceeds target by " + over);
    }

    goal.setCurrentAmount(newTotal);
    return repo.save(goal);
}
```

```bash
# Happy path
curl -X PUT http://localhost:8080/api/goals/1/contribute \
     -H 'Content-Type: application/json' -d '{"amount": 100}'
# {"goalId":1, ..., "currentAmount":550.00, ...}

# Zero contribution
curl -i -X PUT http://localhost:8080/api/goals/1/contribute \
     -H 'Content-Type: application/json' -d '{"amount": 0}'
# 400  "Contribution must be > 0"

# Over the target (goal 1 target=2000, current=550 → contributing 9999 overflows)
curl -i -X PUT http://localhost:8080/api/goals/1/contribute \
     -H 'Content-Type: application/json' -d '{"amount": 9999}'
# 400  "Contribution exceeds target by ..."

# Missing goal
curl -i -X PUT http://localhost:8080/api/goals/999/contribute \
     -H 'Content-Type: application/json' -d '{"amount": 50}'
# 404  "Goal 999 not found"
```

Why `record` for the request body? Java 14+ records give you immutable carriers with zero boilerplate — perfect for "the shape of an incoming JSON". Jackson handles them out of the box.

</details>

---

### TICKET-F063: TransactionService -- Spring @Service
**File:** `backend/src/main/java/com/smartbudget/service/TransactionService.java`

**Description:** Add @Service annotation and implement CRUD methods using JPA repositories.

**What**
- A `TransactionService` class annotated `@Service` with constructor-injected `TransactionRepository`, `UserRepository`, `CategoryRepository`, exposing `getAll`, `getById`, `getByUserId`, `create`, `delete`, `update` — all using JPA repos (no more HashMap).

**Why**
- The service is where business rules live. Centralising validation here means every controller path goes through the same checks, and `@Transactional` gives you rollback for free on `RuntimeException`.

**Observe**
- Spring startup logs include `Creating bean: transactionService`; a POST with `amount: -50` returns `HTTP/1.1 400` regardless of which controller path triggered it.

**Instructions (follow the Day 6 TODOs in TransactionService.java):**

**Step 1 -- @Service + DI:**
- Add `@Service` above the class
- Inject `TransactionRepository`, `UserRepository`, `CategoryRepository` via constructor

**Step 2 -- CRUD methods:**
- `getAll()` -- delegates to `repo.findAll()`
- `getById(Long id)` -- uses `repo.findById(id).orElseThrow()`
- `getByUserId(Long userId)` -- uses the custom query from F051
- `create(...)` -- validates amount > 0, looks up User and Category, builds Transaction, saves
- `delete(Long id)` -- checks `existsById`, throws if not found
- `update(Long id, ...)` -- finds existing, updates fields, saves

**Acceptance Criteria:**
- [ ] App starts with @Service -- check logs for "Creating bean: transactionService"
- [ ] POST with negative amount returns HTTP 400
- [ ] GET non-existent ID returns HTTP 404
- [ ] All CRUD operations work through the service layer

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Add `@Service` to the class. Inject the three repositories via constructor (Spring auto-wires by type). Migrate every CRUD method to use the repositories instead of the plain HashMap. The controller (F056-F060) then calls the service instead of the repository directly.

</details>

<details>
<summary><b>Hint 2 — Service skeleton</b></summary>

```java
@Service
public class TransactionService {

    private final TransactionRepository txnRepo;
    private final UserRepository        userRepo;
    private final CategoryRepository    categoryRepo;

    public TransactionService(TransactionRepository t,
                              UserRepository u,
                              CategoryRepository c) {
        this.txnRepo = t; this.userRepo = u; this.categoryRepo = c;
    }

    public List<Transaction> getAll()                    { return txnRepo.findAll(); }
    public Transaction       getById(Long id)            { return txnRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction " + id)); }
    public List<Transaction> getByUserId(Long userId)    {
        return txnRepo.findByUser_UserIdOrderByTxnDateDesc(userId);
    }

    public Transaction create(Long userId, Long categoryId,
                              BigDecimal amount, LocalDate date,
                              String desc, String type) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("amount must be > 0");
        }
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User " + userId));
        Category cat = categoryRepo.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category " + categoryId));

        Transaction t = new Transaction();
        t.setUser(user); t.setCategory(cat);
        t.setAmount(amount); t.setTxnDate(date);
        t.setDescription(desc); t.setType(type);
        return txnRepo.save(t);
    }

    public void delete(Long id) {
        if (!txnRepo.existsById(id)) {
            throw new ResourceNotFoundException("Transaction " + id);
        }
        txnRepo.deleteById(id);
    }
}
```

Then update controller to call `service.create(...)` instead of `repo.save(t)`.

</details>

<details>
<summary><b>Hint 3 — Full solution + transactional notes</b></summary>

```java
package com.smartbudget.service;

import com.smartbudget.entity.*;
import com.smartbudget.exception.*;
import com.smartbudget.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository txnRepo;
    private final UserRepository        userRepo;
    private final CategoryRepository    categoryRepo;

    public TransactionService(TransactionRepository txnRepo,
                              UserRepository userRepo,
                              CategoryRepository categoryRepo) {
        this.txnRepo      = txnRepo;
        this.userRepo     = userRepo;
        this.categoryRepo = categoryRepo;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAll() {
        return txnRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Transaction getById(Long id) {
        return txnRepo.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Transaction " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Transaction> getByUserId(Long userId) {
        return txnRepo.findByUser_UserIdOrderByTxnDateDesc(userId);
    }

    @Transactional
    public Transaction create(Long userId, Long categoryId,
                              BigDecimal amount, LocalDate date,
                              String description, String type) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("amount must be > 0");
        }
        if (type == null || (!"INCOME".equals(type) && !"EXPENSE".equals(type))) {
            throw new InvalidTransactionException(
                "type must be 'INCOME' or 'EXPENSE'");
        }
        if (date != null && date.isAfter(LocalDate.now())) {
            throw new InvalidTransactionException("date cannot be in the future");
        }

        User user = userRepo.findById(userId).orElseThrow(
            () -> new ResourceNotFoundException("User " + userId + " not found"));
        Category category = categoryRepo.findById(categoryId).orElseThrow(
            () -> new ResourceNotFoundException("Category " + categoryId + " not found"));

        Transaction t = new Transaction();
        t.setUser(user);
        t.setCategory(category);
        t.setAmount(amount);
        t.setTxnDate(date != null ? date : LocalDate.now());
        t.setDescription(description);
        t.setType(type);
        return txnRepo.save(t);
    }

    @Transactional
    public void delete(Long id) {
        if (!txnRepo.existsById(id)) {
            throw new ResourceNotFoundException("Transaction " + id + " not found");
        }
        txnRepo.deleteById(id);
    }

    @Transactional
    public Transaction update(Long id, BigDecimal amount, LocalDate date,
                              String description, String type) {
        Transaction t = getById(id);
        if (amount != null) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidTransactionException("amount must be > 0");
            }
            t.setAmount(amount);
        }
        if (date != null) t.setTxnDate(date);
        if (description != null) t.setDescription(description);
        if (type != null) t.setType(type);
        return txnRepo.save(t);
    }
}
```

`@Transactional` is the killer feature of the service layer:
- Wraps each method in a DB transaction.
- Auto-rollback on `RuntimeException` (Mockito tests confirm this).
- `readOnly = true` is a hint Hibernate uses for performance (no dirty checking).

Now refactor the controller — instead of:
```java
return repo.save(t);
```
do:
```java
return service.create(t.getUser().getUserId(),
                      t.getCategory().getCategoryId(),
                      t.getAmount(), t.getTxnDate(),
                      t.getDescription(), t.getType());
```

Validation now happens in one place (the service), regardless of how the controller is shaped.

</details>

---

### TICKET-F064: Integration Test Setup
**File:** `backend/src/test/java/com/smartbudget/controller/TransactionControllerTest.java`

**Description:** Set up MockMvc integration test class.

**What**
- A `TransactionControllerTest` class annotated `@SpringBootTest` + `@AutoConfigureMockMvc` with an `@Autowired MockMvc mockMvc` field and one smoke test hitting `GET /api/transactions`.

**Why**
- MockMvc fires real HTTP requests through the full Spring stack without binding a port, so each test verifies controller + service + repository + JSON serialisation together in around 1 second.

**Observe**
- `./mvnw test` reports the test class loaded and the smoke test passes — `Tests run: 1, Failures: 0, Errors: 0, Skipped: 0`.

**Instructions (follow the TODOs in the file):**
1. Add `@SpringBootTest` and `@AutoConfigureMockMvc`
2. Inject `MockMvc` with `@Autowired`
3. Import `static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*`

**Acceptance Criteria:**
- [ ] Test class compiles and runs with `mvn test`
- [ ] MockMvc can make HTTP requests to the embedded server

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Top of class: `@SpringBootTest` + `@AutoConfigureMockMvc`. Field: `@Autowired MockMvc mockMvc;`. MockMvc lets you fire HTTP requests at your controllers without starting a real server. Static imports give you the readable DSL: `mockMvc.perform(get("/api/transactions")).andExpect(status().isOk());`.

</details>

<details>
<summary><b>Hint 2 — Class skeleton</b></summary>

```java
package com.smartbudget.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void getAll_returns200() throws Exception {
        mockMvc.perform(get("/api/transactions"))
               .andExpect(status().isOk());
    }
}
```

Run: `./mvnw test` — should report 1 green test.

</details>

<details>
<summary><b>Hint 3 — Full setup + warming the DB</b></summary>

```java
package com.smartbudget.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void getAll_returns200AndJsonArray() throws Exception {
        mockMvc.perform(get("/api/transactions"))
               .andExpect(status().isOk())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(15));   // 15 seeded txns
    }
}
```

`@SpringBootTest` boots the whole context (slow but real). `@AutoConfigureMockMvc` configures the `MockMvc` bean so you don't have to build it manually. Together they let you assert end-to-end controller behaviour without actually starting Tomcat on a port.

Note: this test depends on `data.sql` loading 15 rows. With `ddl-auto=create-drop` the DB is reset between test classes; H2 is in-memory so it's fast (~1s for full context boot in this project).

Pitfall: if you also use `@DataJpaTest` or `@WebMvcTest` for narrower tests, you must NOT combine them with `@SpringBootTest` — pick one.

</details>

---

### TICKET-F065: GlobalExceptionHandler
**File:** `backend/src/main/java/com/smartbudget/exception/GlobalExceptionHandler.java` (create this file)

**Description:** Create a centralized exception handler using @RestControllerAdvice.

**What**
- Two new files — `exception/ResourceNotFoundException.java` (extends `RuntimeException`) and `exception/GlobalExceptionHandler.java` annotated `@RestControllerAdvice` with `@ExceptionHandler` methods returning 400 for `InvalidTransactionException` and 404 for `ResourceNotFoundException`.

**Why**
- Without it, every uncaught exception becomes a 500 with a Whitelabel error page — useless to API clients. Centralising error mapping here means controllers stay thin and the response shape is consistent across endpoints.

**Observe**
- A POST with `amount: -50` returns `HTTP/1.1 400` with body `{"error":"Bad Request","message":"amount must be > 0",...}`; a GET on a missing id returns `HTTP/1.1 404` with the same envelope shape.

**Instructions:**
1. Create a class annotated with `@RestControllerAdvice`
2. Add methods annotated with `@ExceptionHandler` for:
   - `InvalidTransactionException` -- returns HTTP 400 Bad Request
   - `ResourceNotFoundException` -- returns HTTP 404 Not Found (create this exception class too)
3. Return a simple error response with the exception message

**Acceptance Criteria:**
- [ ] Throwing `InvalidTransactionException` in a controller returns HTTP 400 with a message
- [ ] Throwing `ResourceNotFoundException` returns HTTP 404 with a message
- [ ] Other uncaught exceptions return HTTP 500

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Create `exception/GlobalExceptionHandler.java` annotated `@RestControllerAdvice` (catches exceptions for every `@RestController`). One `@ExceptionHandler(...)` method per exception type. Each returns a `ResponseEntity<Map<String,String>>` with the right HTTP status. Also create `ResourceNotFoundException` if you haven't already.

</details>

<details>
<summary><b>Hint 2 — Class skeleton</b></summary>

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<Map<String,String>> badRequest(InvalidTransactionException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,String>> notFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }
}
```

And `ResourceNotFoundException.java`:

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String msg) { super(msg); }
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution + catch-all + verification</b></summary>

`exception/ResourceNotFoundException.java`:

```java
package com.smartbudget.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
```

`exception/GlobalExceptionHandler.java`:

```java
package com.smartbudget.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<Map<String,Object>> handleInvalid(InvalidTransactionException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "status",    400,
                    "error",     "Bad Request",
                    "message",   e.getMessage(),
                    "timestamp", LocalDateTime.now().toString()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "status",    404,
                    "error",     "Not Found",
                    "message",   e.getMessage(),
                    "timestamp", LocalDateTime.now().toString()));
    }

    /** Catch-all so the user never sees a raw stacktrace. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleOther(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status",    500,
                    "error",     "Internal Server Error",
                    "message",   e.getClass().getSimpleName() + ": " + e.getMessage(),
                    "timestamp", LocalDateTime.now().toString()));
    }
}
```

Verify:

```bash
# 400 — negative amount
curl -i -X POST http://localhost:8080/api/transactions \
     -H 'Content-Type: application/json' \
     -d '{"user":{"userId":1},"category":{"categoryId":1},"amount":-50,...}'
# HTTP/1.1 400
# {"status":400,"error":"Bad Request","message":"amount must be > 0",...}

# 404 — missing resource
curl -i http://localhost:8080/api/transactions/9999
# HTTP/1.1 404
# {"status":404,"error":"Not Found","message":"Transaction 9999 not found",...}
```

`@RestControllerAdvice` = `@ControllerAdvice` + `@ResponseBody`. The advice applies to all `@RestController` beans automatically. In production you'd log the 500 path (don't echo the message back blindly — it can leak internal info).

</details>

---

### TICKET-F066: Integration Test -- POST Valid Transaction
**File:** `backend/src/test/java/com/smartbudget/controller/TransactionControllerTest.java`

**Description:** Test creating a valid transaction through the API.

**What**
- A `createTransaction_validInput_returns201` test method that POSTs a well-formed JSON body via `mockMvc.perform(post("/api/transactions"))` and asserts `status().isCreated()` plus `jsonPath("$.txnId").isNotEmpty()`.

**Why**
- Regression-proofs the happy path — the moment someone breaks Jackson serialisation, the entity mapping, or the 201 status, this test fails before the change merges.

**Observe**
- `./mvnw test -Dtest=TransactionControllerTest#createTransaction_validInput_returns201` reports green; the assertion on `$.amount` matches the value posted in the request body.

**Instructions:**
1. Use `mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(jsonBody))`
2. Assert `.andExpect(status().isCreated())`
3. Assert the response body contains the expected fields

**Acceptance Criteria:**
- [ ] Test sends a POST request with valid JSON
- [ ] Response status is 201 Created
- [ ] Response body contains the transaction with a generated ID

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`mockMvc.perform(post("/api/transactions").contentType(APPLICATION_JSON).content(json))`. Use a text block (Java 15+) for the JSON. `andExpect(status().isCreated())` for the status, `andExpect(jsonPath("$.txnId").isNotEmpty())` to confirm the ID was auto-generated.

</details>

<details>
<summary><b>Hint 2 — Test method</b></summary>

```java
@Test
void createTransaction_validInput_returns201() throws Exception {
    String body = """
        {
          "user":     {"userId": 1},
          "category": {"categoryId": 1},
          "amount":   100.00,
          "txnDate":  "2026-05-01",
          "description": "Integration test",
          "type":     "INCOME"
        }
        """;

    mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.txnId").isNotEmpty())
           .andExpect(jsonPath("$.amount").value(100.00));
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution + GET round-trip</b></summary>

```java
@Test
void createTransaction_validInput_returns201AndPersists() throws Exception {
    String body = """
        {
          "user":     {"userId": 1},
          "category": {"categoryId": 1},
          "amount":   100.00,
          "txnDate":  "2026-05-01",
          "description": "MockMvc happy path",
          "type":     "INCOME"
        }
        """;

    // Act: create
    String response = mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.txnId").isNotEmpty())
           .andExpect(jsonPath("$.amount").value(100.00))
           .andExpect(jsonPath("$.description").value("MockMvc happy path"))
           .andReturn().getResponse().getContentAsString();

    // Extract the new id (Jackson-style):
    Long newId = new ObjectMapper().readTree(response).get("txnId").asLong();

    // Assert: GET round-trip
    mockMvc.perform(get("/api/transactions/" + newId))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.description").value("MockMvc happy path"));
}
```

Java text blocks (`"""..."""`) keep JSON readable inside a Java string — no `+ "\\n"` noise.

`jsonPath("$.foo")` evaluates a JSON path against the response body. `$` is the root; chained dots descend.

</details>

---

### TICKET-F067: Integration Test -- POST Invalid
**File:** `backend/src/test/java/com/smartbudget/controller/TransactionControllerTest.java`

**Description:** Test that invalid data returns appropriate error responses.

**What**
- A `createTransaction_negativeAmount_returns400` test that POSTs `"amount": -50` and asserts `status().isBadRequest()` plus `jsonPath("$.message", containsString("amount"))`.

**Why**
- The happy-path test in F066 proves the wire works; the negative test proves the service-layer validation and GlobalExceptionHandler from F063/F065 are actually invoked — otherwise the bad row would silently land in the DB.

**Observe**
- `./mvnw test` shows both tests green; if you delete the validation in `TransactionService.create`, this test fails with `Status expected:<400> but was:<201>`.

**Acceptance Criteria:**
- [ ] POST with negative amount returns HTTP 400
- [ ] Error response includes a meaningful message

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Same setup as F066 but the JSON has `"amount": -50`. Expect `status().isBadRequest()` and `jsonPath("$.message").value(containsString("amount"))`.

</details>

<details>
<summary><b>Hint 2 — Test method</b></summary>

```java
@Test
void createTransaction_negativeAmount_returns400() throws Exception {
    String body = """
        {
          "user":     {"userId": 1},
          "category": {"categoryId": 1},
          "amount":   -50.00,
          "txnDate":  "2026-05-01",
          "description": "should fail",
          "type":     "INCOME"
        }
        """;

    mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message",
                org.hamcrest.Matchers.containsString("amount")));
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution + 3 negative cases</b></summary>

```java
import static org.hamcrest.Matchers.containsString;

@Test
void createTransaction_negativeAmount_returns400() throws Exception {
    String body = """
        {"user":{"userId":1},"category":{"categoryId":1},
         "amount":-50.00,"txnDate":"2026-05-01",
         "description":"bad","type":"EXPENSE"}
        """;
    mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", containsString("amount")));
}

@Test
void createTransaction_missingUser_returns404() throws Exception {
    String body = """
        {"user":{"userId":9999},"category":{"categoryId":1},
         "amount":50.00,"txnDate":"2026-05-01",
         "description":"missing user","type":"EXPENSE"}
        """;
    mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.message", containsString("User 9999")));
}

@Test
void createTransaction_invalidType_returns400() throws Exception {
    String body = """
        {"user":{"userId":1},"category":{"categoryId":1},
         "amount":50.00,"txnDate":"2026-05-01",
         "description":"oops","type":"BOGUS"}
        """;
    mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isBadRequest());
}
```

These tests prove the whole stack works: controller → service validation → GlobalExceptionHandler → correct status + body. If any single layer regresses, one of these will fail.

</details>

---

### TICKET-F068: Postman Testing
**File:** N/A (use Postman)

**Description:** Test all API endpoints using Postman.

**What**
- A saved Postman collection `SmartBudget API` containing all 10 endpoints plus negative-case siblings, exported as `docs/postman/smartbudget-api.postman_collection.json` and committed.

**Why**
- MockMvc tests prove the code works in isolation; Postman proves the *running* server (your laptop, port 8080, real DB) responds correctly to the exact requests the React frontend will send on Day 8.

**Observe**
- The Collection Runner executes every request green, with 201/200/204 on happy paths and 400/404 on negative paths; the exported `.json` file appears in the repo.

**Instructions:**
1. Create a Postman collection called "SmartBudget API"
2. Add requests for every endpoint listed in the API Reference (see README.md)
3. Test happy paths and error cases
4. Save the collection for future use

**Acceptance Criteria:**
- [ ] All 10 API endpoints tested in Postman
- [ ] Happy path returns expected status codes (200, 201, 204)
- [ ] Error cases return 400 or 404 with messages
- [ ] Postman collection is saved

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Open Postman → New Collection → name it `SmartBudget API`. For each endpoint, create a new request: pick the HTTP verb, paste the URL, set body if needed. Save each one into the collection. Run the whole collection at the end via "Run collection" to confirm everything is green.

</details>

<details>
<summary><b>Hint 2 — Endpoints checklist</b></summary>

| # | Method | URL | Body |
|---|---|---|---|
| 1 | GET    | `/api/transactions` | — |
| 2 | GET    | `/api/transactions/user/1` | — |
| 3 | POST   | `/api/transactions` | `{user, category, amount, txnDate, description, type}` |
| 4 | DELETE | `/api/transactions/{id}` | — |
| 5 | GET    | `/api/users` | — |
| 6 | GET    | `/api/users/1` | — |
| 7 | POST   | `/api/users` | `{name, email}` |
| 8 | GET    | `/api/categories` | — |
| 9 | GET    | `/api/goals/user/1` | — |
| 10| PUT    | `/api/goals/1/contribute` | `{"amount": 100}` |

For each, also create a "negative case" sibling (e.g., `GET /api/transactions/9999` → 404).

</details>

<details>
<summary><b>Hint 3 — Full walkthrough + export</b></summary>

1. Create collection: **Collections → New → name `SmartBudget API`**.
2. Use a **collection variable** for the base URL: in the collection's Variables tab, add `baseUrl = http://localhost:8080`. Reference it in requests as `{{baseUrl}}/api/transactions`.
3. Add the 10 requests above. For POST/PUT requests:
   - Body tab → raw → JSON.
   - Paste the body, e.g.:
     ```json
     {
       "user":     {"userId": 1},
       "category": {"categoryId": 1},
       "amount":   75.00,
       "txnDate":  "2026-05-15",
       "description": "Postman test",
       "type":     "EXPENSE"
     }
     ```
4. Optional: add **Tests** scripts to assert status codes automatically.
   ```js
   pm.test("Status is 201", () => pm.response.to.have.status(201));
   pm.test("Has txnId", () => pm.expect(pm.response.json().txnId).to.be.a("number"));
   ```
5. **Run the collection** (Collection Runner) — see each request executed in sequence with pass/fail.
6. **Export**: click the collection menu → Export → Collection v2.1 (recommended) → save as `docs/postman/smartbudget-api.postman_collection.json` and commit it.

Suggested negative-case requests to add:
- `GET {{baseUrl}}/api/transactions/9999` → 404
- `POST {{baseUrl}}/api/transactions` with `amount: -10` → 400
- `PUT {{baseUrl}}/api/goals/1/contribute` body `{"amount": 0}` → 400

</details>

---

## End-of-Day Checklist

- [ ] TransactionService has @Service annotation with DI
- [ ] All controllers refactored to use service layer
- [ ] GlobalExceptionHandler catches and formats errors
- [ ] Integration tests pass with `mvn test`
- [ ] All endpoints tested with Postman
- [ ] You can explain: @Service, @RestController, @RequestBody, @PathVariable, @ExceptionHandler, MockMvc

---

*Tomorrow (Day 7): You will build static HTML/CSS/JS frontend pages.*
