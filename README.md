# SmartBudget – Personal Finance Tracker

> **Deutsche Bank | TDI 2026 | Foundation Track**

![CI](https://github.com/<your-org>/smartbudget/actions/workflows/ci.yml/badge.svg)

---

## Day 1 Quick Start

Both the backend and frontend work **out of the box** on Day 1.

### Backend
```bash
cd backend
mvn spring-boot:run
```
| URL | What you see |
|-----|-------------|
| http://localhost:8080/actuator/health | `{"status":"UP"}` |
| http://localhost:8080/api/transactions | JSON array of seed transactions |
| http://localhost:8080/api/users | JSON array of 5 seed users |
| http://localhost:8080/api/goals/user/1 | JSON array of savings goals |
| http://localhost:8080/h2-console | Visual DB browser (seed data pre-loaded) |

H2 console settings: JDBC URL `jdbc:h2:mem:smartbudget` · Username `sa` · Password *(blank)*

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Open **http://localhost:5173** — you will see the full UI with mock data.

---

## What You See on Day 1

### Backend — real data from the API
```
GET /api/transactions     → 15 seed transactions
GET /api/users            → 5 seed users
GET /api/categories       → 5 categories
GET /api/goals/user/1     → 4 savings goals
```

### Frontend — full UI with mock data
Every page is visible and navigable. Pages show hardcoded mock data with a
**yellow TODO banner** showing exactly which ticket connects it to the real API.

---

## Your Job — Sprint by Sprint

| Day | Sprint | What Students Build |
|-----|--------|---------------------|
| 1   | 0  | DB schema (`create_tables.sql`), seed data, 5 SQL queries, ER diagram |
| 2   | 1  | Java POJOs (User, Category, Transaction, SavingsGoal), console menu app |
| 3   | 2  | OOP: BaseTransaction → Income/Expense, TransactionService (plain Java), CSV I/O |
| 4   | 3  | JDBC DAO (DatabaseConnection, TransactionDAO), Streams/Lambdas, JUnit + Mockito |
| 5   | 4  | Spring: add custom queries to repositories, configure H2 console, seed data |
| 6   | 5  | Implement service layer, refactor controllers to use services, REST tests |
| 7   | 6  | HTML/CSS/JS static pages (separate from React) |
| 8   | 7  | React hooks (`useBudgetAPI.js`), replace mock data with real API calls |
| 9   | 8  | Filters, bar chart, edit transaction, contribute to goal, toast, polish |
| 10  | 9  | Docker, docker-compose, GitHub Actions CI/CD, final demo |

---

## Provided vs Student Builds

### [DONE] Provided (working Day 1)

| File | Why provided |
|------|-------------|
| `entity/` — all 4 `@Entity` classes | App must boot; students study annotations |
| `repository/` — all 4 repositories | App must serve data; students add custom queries |
| `controller/` — 4 basic controllers | API works from Day 1; students refactor Day 6 |
| `application.properties` | H2 + seed data configured |
| `data.sql` | 15 seed records visible immediately |
| `CorsConfig.java` | React can call API without CORS errors |
| `frontend/App.jsx` | Router with 4 routes |
| `frontend/Navbar.jsx` | Working navigation |
| `frontend/global.css` | DB Blue `#003366` theme |
| `frontend/pages/*` | Full UI with mock data + yellow TODO banners |
| `frontend/Feedback.jsx` | Spinner, Toast, ErrorMessage, TodoBanner |

### [TODO] Students Build

| File | Day | Tickets |
|------|-----|---------|
| `db/create_tables.sql` | 1 | F003, F004 |
| `db/seed_data.sql` | 1 | F004, F005 |
| `db/queries.sql` | 1 | F006, F007, F008 |
| `console/Main.java` | 2 | F016–F019 |
| `entity/*.java` (POJOs in `model/`) | 2 | F012–F015 |
| `model/BaseTransaction.java` | 3 | F021 |
| `model/IncomeTransaction.java` | 3 | F022 |
| `model/ExpenseTransaction.java` | 3 | F023 |
| `exception/InvalidTransactionException.java` | 3 | F024 |
| `service/TransactionService.java` | 3→6 | F026–F030, F032–F034, F063 |
| `dao/DatabaseConnection.java` | 4 | F035 |
| `dao/TransactionDAO.java` | 4 | F036–F039 |
| `test/TransactionServiceTest.java` | 4 | F040–F043 |
| `repository/*` custom queries | 5 | F050–F052 |
| `exception/GlobalExceptionHandler.java` | 6 | F065 |
| `service/SavingsGoalService.java` | 6 | F061, F062 |
| `controller/*` refactored to use services | 6 | F056–F063 |
| `test/TransactionControllerTest.java` | 6 | F064–F066 |
| HTML/CSS/JS static pages | 7 | F069–F081 |
| `hooks/useBudgetAPI.js` | 8 | F083, F091 |
| Replace mock data in all 4 pages | 8 | F085–F090 |
| `MonthlySummaryChart.jsx` | 9 | F097 |
| Filters, edit, contribute, polish | 9 | F095–F104 |
| `backend/Dockerfile` | 10 | F105 |
| `frontend/Dockerfile` + `nginx.conf` | 10 | F106 |
| `docker-compose.yml` | 10 | F107, F108 |
| `.github/workflows/ci.yml` | 10 | F110–F112 |

---

## Project Structure

```
smartbudget/
├── backend/
│   ├── src/main/java/com/smartbudget/
│   │   ├── config/     CorsConfig.java           [DONE] provided
│   │   ├── entity/     User, Category, Transaction, SavingsGoal [DONE] provided
│   │   ├── repository/ 4 JPA repositories        [DONE] provided (students add custom queries)
│   │   ├── controller/ 4 basic REST controllers   [DONE] provided (students refactor Day 6)
│   │   ├── model/      BaseTransaction, Income/ExpenseTransaction  [TODO] student
│   │   ├── service/    TransactionService, SavingsGoalService      [TODO] student
│   │   ├── dao/        DatabaseConnection, TransactionDAO           [TODO] student
│   │   ├── exception/  Custom exceptions + GlobalExceptionHandler   [TODO] student
│   │   └── console/    Main.java (console menu)                     [TODO] student
│   ├── src/test/       JUnit + Mockito tests                        [TODO] student
│   └── Dockerfile                                                    [TODO] student Day 10
│
├── frontend/
│   ├── src/
│   │   ├── components/ Navbar [DONE]  MonthlySummaryChart [TODO]  Feedback [DONE]
│   │   ├── hooks/      useBudgetAPI.js   [TODO] student Day 8
│   │   ├── pages/      All 4 pages with mock data [DONE] (students replace mock → real)
│   │   └── styles/     global.css       [DONE] provided
│   ├── Dockerfile       [TODO] student Day 10
│   └── nginx.conf       [TODO] student Day 10
│
├── db/                  [TODO] student Day 1
├── docker-compose.yml   [TODO] student Day 10
└── .github/workflows/   [TODO] student Day 10
```

---

## API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | /api/transactions | All transactions |
| POST   | /api/transactions | Create transaction |
| GET    | /api/transactions/user/{userId} | Transactions by user |
| DELETE | /api/transactions/{id} | Delete transaction |
| GET    | /api/users | All users |
| POST   | /api/users | Create user |
| GET    | /api/categories | All categories |
| GET    | /api/goals/user/{userId} | Goals for user |
| PUT    | /api/goals/{id}/contribute | Contribute to goal |
| GET    | /actuator/health | Health check |

---

*Deutsche Bank | TDI 2026 Foundation Track*
