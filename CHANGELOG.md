# SmartBudget – Starter Code Changelog
## Revamp: "Working Day 1" Edition

---

## Why This Revamp

The previous guided starter caused students to spend Day 1–5 fixing startup errors
before seeing anything. This revamp ensures:

- `mvn spring-boot:run` → starts cleanly on Day 1
- `npm run dev` → shows a full UI with mock data on Day 1
- Students see real JSON from the API on Day 1
- Every ticket is still genuinely built by students

---

## Section 1 — Backend Changes

### 1.1 Entity Classes — MOVED from Student Tickets to Provided

| File | Previous | Now | Reason |
|------|----------|-----|--------|
| `entity/User.java` | Empty shell, TICKET-F012/F046 | [DONE] Fully implemented | App cannot boot without `@Entity` |
| `entity/Category.java` | Empty shell, TICKET-F013/F047 | [DONE] Fully implemented | App cannot boot without `@Entity` |
| `entity/Transaction.java` | Empty shell, TICKET-F014/F048 | [DONE] Fully implemented | App cannot boot without `@Entity` |
| `entity/SavingsGoal.java` | Empty shell, TICKET-F015/F049 | [DONE] Fully implemented | App cannot boot without `@Entity` |

**Ticket adjustment:**
- `TICKET-F012–F015` (Day 2): Scope unchanged — students still write Java POJOs.
  These now live in `com.smartbudget.model` (plain Java, no JPA) instead of `entity`.
  The provided entity classes serve as reference examples for students to study.
- `TICKET-F046–F049` (Day 5): Scope changed from "Create @Entity class" →
  **"Study the provided @Entity class, understand all annotations, then add custom
  queries to the repository"**. Students annotate their model POJOs as a learning
  exercise but are not responsible for making JPA boot.

---

### 1.2 Repositories — MOVED from Student Tickets to Provided (partially)

| File | Previous | Now | Reason |
|------|----------|-----|--------|
| `UserRepository.java` | Empty interface placeholder | [DONE] Working — `findByEmail()` provided | Needed for controllers to serve data |
| `TransactionRepository.java` | Empty placeholder | [DONE] Working — `findByUser_UserIdOrderByTxnDateDesc()` provided | Needed for Day 1 API |
| `CategoryRepository.java` | Empty placeholder | [DONE] Working — basic `JpaRepository` provided | Needed for category dropdown |
| `SavingsGoalRepository.java` | Empty placeholder | [DONE] Working — `findByUser_UserId()` provided | Needed for goals endpoint |

**Ticket adjustment:**
- `TICKET-F050–F052` (Day 5): Scope changed from "Create repository interface" →
  **"Add custom query methods to the provided repository"**.
  Example: students add `findByType()`, `findByTxnDateBetween()`, `sumByUserAndType()`
  to `TransactionRepository`. The interface exists — students extend it.

---

### 1.3 Controllers — MOVED from Student Tickets to Provided (basic version)

| File | Previous | Now | Reason |
|------|----------|-----|--------|
| `TransactionController.java` | Empty shell | [DONE] Basic working controller (uses repo directly) | API must return data Day 1 |
| `UserController.java` | Empty shell | [DONE] Basic working controller | API must return data Day 1 |
| `SavingsGoalController.java` | Empty shell | [DONE] Basic working controller | API must return data Day 1 |
| `CategoryController.java` | Empty shell | [DONE] Basic working controller | Needed for React dropdown |

**Ticket adjustment:**
- `TICKET-F056–F063` (Day 6): Scope changed from "Build controller from scratch" →
  **"Refactor the provided basic controller to use your TransactionService"**.
  The provided controllers call repositories directly with no validation.
  After students implement `TransactionService`, they swap `repo.save(t)` for
  `service.create(userId, categoryId, amount, date, desc)` — adding real validation,
  error handling, and business rules.
- This is a better learning pattern: students see working code, then improve it.

---

### 1.4 application.properties — FULLY PRE-CONFIGURED

| Setting | Previous | Now |
|---------|----------|-----|
| `spring.datasource.url` | Commented out, student fills in | [DONE] Pre-configured |
| `spring.h2.console.enabled` | Commented out | [DONE] Pre-configured `true` |
| `spring.jpa.hibernate.ddl-auto` | Commented out | [DONE] Pre-configured `create-drop` |
| `spring.sql.init.mode` | `never` to avoid empty-script crash | [DONE] `always` with seed data |
| `spring.jpa.defer-datasource-initialization` | Commented out | [DONE] `true` — prevents table-not-found crash |

**Ticket adjustment:**
- `TICKET-F045` (Day 5): Scope changed from "Configure application.properties" →
  **"Verify the H2 console is working, explore seed data, understand each property"**.
  Students open H2 console, run SELECT queries, and study what each property does.

---

### 1.5 data.sql — PRE-FILLED with Seed Data

| Previous | Now |
|----------|-----|
| Empty file, student fills in for TICKET-F053 | [DONE] 15 real records (5 categories, 5 users, 4 goals, 15 transactions) |

**Ticket adjustment:**
- `TICKET-F053` (Day 5): Scope changed from "Write INSERT statements" →
  **"Verify seed data appears in H2 console. Extend the file with 10 more transactions
  of your own"**. Students still write SQL but start from working data.

---

### 1.6 OOP Model Classes — UNCHANGED (student builds)

These were student tasks before and remain student tasks:

| File | Ticket | Day |
|------|--------|-----|
| `model/BaseTransaction.java` | F021 | 3 |
| `model/IncomeTransaction.java` | F022 | 3 |
| `model/ExpenseTransaction.java` | F023 | 3 |
| `exception/InvalidTransactionException.java` | F024 | 3 |
| `service/TransactionService.java` | F026–F030, F032–F034, F063 | 3→6 |
| `service/SavingsGoalService.java` | F061, F062 | 6 |
| `exception/GlobalExceptionHandler.java` | F065 | 6 |
| `dao/DatabaseConnection.java` | F035 | 4 |
| `dao/TransactionDAO.java` | F036–F039 | 4 |
| `console/Main.java` | F016–F019 | 2 |
| `test/TransactionServiceTest.java` | F040–F043 | 4 |
| `test/TransactionControllerTest.java` | F064–F066 | 6 |

---

## Section 2 — Frontend Changes

### 2.1 Pages — MOVED from Empty Shells to Working UI with Mock Data

| File | Previous | Now | Reason |
|------|----------|-----|--------|
| `pages/Dashboard.jsx` | Empty shell | [DONE] Working UI, mock stats, chart placeholder | Students see UI on Day 1 |
| `pages/TransactionList.jsx` | Empty shell | [DONE] Working table, mock data, filter bar UI | Students see UI on Day 1 |
| `pages/AddTransactionForm.jsx` | Empty shell | [DONE] Working form with validation | Students see UI on Day 1 |
| `pages/SavingsGoals.jsx` | Empty shell | [DONE] Working cards with progress bars | Students see UI on Day 1 |

**Each page has:**
- Hardcoded `MOCK_*` constant at the top with realistic data
- Fully rendered working UI from Day 1
- Yellow `<TodoBanner>` showing exactly which ticket wires up the real API
- Action buttons (`Delete`, `Edit`, `Contribute`) that show `alert("TODO TICKET-F0XX")` until wired up

**Ticket adjustment:**
- `TICKET-F085–F090` (Day 8): Scope changed from "Build the component" →
  **"Replace MOCK_DATA with the real hook call"**.
  Students implement `useTransactionData()` → import it → delete the `MOCK_*` constant.
  Much clearer learning objective: one line replaces the mock, everything else stays.

---

### 2.2 New Component — TodoBanner

| File | Previous | Now |
|------|----------|-----|
| `components/Feedback.jsx` | Spinner, Toast, ErrorMessage | + `TodoBanner` component added |

A yellow banner visible on every page with mock data. Shows ticket number and task.
Students know exactly what to implement next just by looking at the UI.

---

### 2.3 Hooks — UNCHANGED (student builds)

| File | Ticket | Day |
|------|--------|-----|
| `hooks/useBudgetAPI.js` | F083, F091 | 8 |

Still a full shell. Students implement all 3 hooks:
`useTransactionData()`, `useSavingsGoals(userId)`, `useCategories()`

**Ticket adjustment:**
- `TICKET-F083` (Day 8): Scope unchanged — but now there is a clear visual reward.
  As soon as a student implements `useTransactionData()` and uses it in Dashboard,
  the yellow banner disappears and real DB data appears. Immediate feedback loop.

---

### 2.4 App.jsx + Navbar — FULLY PROVIDED

| File | Previous | Now |
|------|----------|-----|
| `App.jsx` | Empty shell | [DONE] Fully working Router with 4 routes |
| `Navbar.jsx` | Empty shell | [DONE] Fully working with active link highlighting |
| `global.css` | Empty with comments | [DONE] Full DB Blue `#003366` theme |

**Ticket adjustment:**
- `TICKET-F082` (Day 8): Scope changed from "Set up React Router" →
  **"Study the provided router, add a new `/categories` route"** or
  **"Add a 404 Not Found route as a catch-all"**.
- `TICKET-F084` (Day 8): Scope changed from "Build Navbar" →
  **"Extend the Navbar to show the currently logged-in user's name"**.

---

### 2.5 MonthlySummaryChart — UNCHANGED (student builds)

| File | Ticket | Day |
|------|--------|-----|
| `components/MonthlySummaryChart.jsx` | F097 | 9 |

Shell with clear TODO. Dashboard shows a dashed placeholder box where the chart goes.
When students implement it and import it into Dashboard, the placeholder is replaced.

---

## Section 3 — Day 10 (Docker & CI) — UNCHANGED

All Docker and CI files remain as shells students complete:

| File | Ticket | Status |
|------|--------|--------|
| `backend/Dockerfile` | F105 | Shell |
| `frontend/Dockerfile` | F106 | Shell |
| `frontend/nginx.conf` | F106 | Shell |
| `docker-compose.yml` | F107, F108 | Shell |
| `.github/workflows/ci.yml` | F110–F112 | Shell |

---

## Section 4 — Complete Ticket Impact Summary

### Tickets REMOVED from student builds (now provided)
| Ticket | Was | Now |
|--------|-----|-----|
| F046 | Create @Entity User | Study provided entity |
| F047 | Create @Entity Category | Study provided entity |
| F048 | Create @Entity Transaction | Study provided entity |
| F049 | Create @Entity SavingsGoal | Study provided entity |

### Tickets with REDUCED scope
| Ticket | Was | Now |
|--------|-----|-----|
| F045 | Configure application.properties from scratch | Verify pre-configured properties in H2 console |
| F050 | Create UserRepository interface | Add `findByEmail` + custom queries to provided interface |
| F051 | Create TransactionRepository interface | Add `findByType`, `findByDateBetween`, `sumByUserAndType` |
| F052 | Create CategoryRepository interface | Add `findByType` to provided interface |
| F053 | Write all data.sql INSERTs from scratch | Extend provided seed data with 10 more transactions |
| F056 | Build TransactionController from scratch | Refactor basic controller to use TransactionService |
| F057 | Build POST endpoint | Wire `@Valid` + delegate to `service.create()` |
| F058 | Build GET by user endpoint | Delegate to `service.getByUserId()` |
| F059 | Build DELETE endpoint | Add `ResourceNotFoundException` handling via service |
| F060 | Build UserController from scratch | Refactor to use UserService (or extend existing) |
| F061 | Build SavingsGoalController from scratch | Refactor to use SavingsGoalService |
| F062 | Build contribute endpoint | Wire contribute through service with validation |
| F082 | Set up React Router from scratch | Extend provided router (add a route or 404 page) |
| F083 | Build all hooks | Still implement all hooks — but visual reward is immediate |
| F084 | Build Navbar from scratch | Extend provided Navbar (add feature) |
| F085 | Build Dashboard component from scratch | Replace `MOCK_STATS` with `useTransactionData()` |
| F086 | Build TransactionList from scratch | Replace `MOCK_TRANSACTIONS` with `useTransactionData()` |
| F087 | Build delete button | Wire alert → real `fetch DELETE` |
| F088 | Build AddTransactionForm from scratch | Form provided — study controlled forms pattern |
| F089 | Implement POST submission | Replace `alert()` in `handleSubmit` with real `fetch POST` |
| F090 | Build SavingsGoals from scratch | Replace `MOCK_GOALS` with `useSavingsGoals()` |

### Tickets UNCHANGED
| Tickets | Day |
|---------|-----|
| F001–F010 (DB design, SQL, ER diagram) | 1 |
| F011–F020 (Java POJOs, console menu) | 2 |
| F021–F031 (OOP, TransactionService plain Java) | 3 |
| F032–F043 (JDBC DAO, Streams, JUnit) | 4 |
| F063–F068 (Spring service logic, REST tests, Postman) | 6 |
| F069–F081 (HTML/CSS/JS static pages) | 7 |
| F091–F104 (React polish: filters, chart, edit) | 8–9 |
| F105–F116 (Docker, CI/CD, final demo) | 10 |

---

## Section 5 — Student Experience Before vs After

### Before (previous guided starter)
```
Day 1: Run app → crash: "Not a managed type: SavingsGoal"
       Fix: add @Entity → crash: "Table CATEGORIES not found"
       Fix: defer-datasource-init → crash: "script must not be null"
       Fix: set sql.init.mode → finally starts → empty database
       Frontend: white screen (shells return nothing)
Result: Students spend Day 1 debugging config, see nothing
```

### After (this revamp)
```
Day 1: mvn spring-boot:run → starts in 3 seconds, no errors
       http://localhost:8080/api/transactions → real JSON with 15 records
       http://localhost:8080/h2-console → tables with seed data visible
       npm run dev → full UI with mock data, navigation works
       Every page shows a yellow banner: "TICKET-F085: replace mock with hook"
Result: Students see a working app immediately, know exactly what to build
```
