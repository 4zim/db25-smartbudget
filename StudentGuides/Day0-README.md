# Day 0 -- Welcome to SmartBudget

> Deutsche Bank | TDI 2026 | Foundation Track

---

## What is SmartBudget?

SmartBudget is a **personal finance tracker** that lets users:

- Record income and expense transactions
- Categorize spending (Food, Rent, Salary, etc.)
- Set savings goals and track progress
- View summaries and charts of their financial activity

You will build this application from the ground up over 10 days, starting with a database and ending with a fully containerized, CI/CD-enabled web application.

---

## The FinTech Context

In the financial services industry, applications that track money must be:

- **Accurate** -- financial calculations use `BigDecimal`, never `double` (which can produce errors like `0.1 + 0.2 = 0.30000000000000004`)
- **Secure** -- user data is protected, SQL injection is prevented, input is validated
- **Auditable** -- every transaction is tracked with timestamps, user IDs, and categories
- **Reliable** -- the application handles errors gracefully instead of crashing

SmartBudget teaches all of these principles in a hands-on way.

---

## Technology Stack

| Layer | Technology | When You Use It |
|-------|-----------|-----------------|
| Database | PostgreSQL | Day 1 |
| Language | Java 25 | Day 2 onwards |
| OOP Concepts | Abstract classes, Inheritance, Polymorphism | Day 3 |
| Database Access | Raw JDBC (DriverManager, PreparedStatement) | Day 4 |
| Testing | JUnit 5 + Mockito | Day 4, Day 6 |
| Framework | Spring Boot 3.x | Day 5 onwards |
| ORM | Spring Data JPA + Hibernate | Day 5 |
| REST API | Spring Web (@RestController) | Day 6 |
| Frontend (static) | HTML, CSS, JavaScript | Day 7 |
| Frontend (dynamic) | React 19 + Vite | Day 8-9 |
| Charting | Recharts | Day 9 |
| Containers | Docker, docker-compose | Day 10 |
| CI/CD | GitHub Actions | Day 10 |
| Reverse Proxy | nginx | Day 10 |

---

## Project Architecture

```
+-------------------+         +-------------------+         +-------------------+
|                   |  HTTP   |                   |  JPA    |                   |
|   React Frontend  | ------> |  Spring Boot API  | ------> |   PostgreSQL DB   |
|   (Port 5173)     |  JSON   |  (Port 8080)      |  SQL    |   (Port 5432)     |
|                   |         |                   |         |                   |
+-------------------+         +-------------------+         +-------------------+
```

### Backend Layers (Spring Boot)

```
HTTP Request
    |
    v
[Controller]  -- handles HTTP, extracts params, returns JSON
    |
    v
[Service]     -- business logic, validation, calculations
    |
    v
[Repository]  -- database access via Spring Data JPA
    |
    v
[Entity]      -- Java class mapped to a database table
    |
    v
[Database]    -- PostgreSQL (production) or H2 (development)
```

---

## The 10-Day Journey

| Day | Sprint | Theme | What You Build |
|-----|--------|-------|----------------|
| 1 | 0 | SQL Foundations | Database tables, seed data, SQL queries, ER diagram |
| 2 | 1 | Java Basics | POJOs (Plain Old Java Objects), console menu application |
| 3 | 2 | OOP | Abstract classes, inheritance, polymorphism, CSV I/O |
| 4 | 3 | JDBC + Testing | Raw database access, HashMap, Streams, JUnit + Mockito |
| 5 | 4 | Spring Boot | JPA entities, repositories, H2 console, Spring configuration |
| 6 | 5 | REST APIs | Controllers, service layer, integration tests, Postman |
| 7 | 6 | HTML/CSS/JS | Static frontend pages (no framework) |
| 8 | 7 | React Basics | React hooks, replace mock data with real API calls |
| 9 | 8 | React Polish | Filters, charts, edit, contribute, toast notifications |
| 10 | 9 | DevOps | Docker, docker-compose, GitHub Actions CI/CD, final demo |

---

## What is Already Working on Day 1

The starter code gives you a **working application** from the very first day.

### Backend (Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

Visit these URLs to see real data:

| URL | What You See |
|-----|-------------|
| http://localhost:8080/api/transactions | 15 seed transactions as JSON |
| http://localhost:8080/api/users | 5 seed users as JSON |
| http://localhost:8080/api/categories | 5 categories as JSON |
| http://localhost:8080/api/goals/user/1 | 4 savings goals as JSON |
| http://localhost:8080/h2-console | Visual database browser |
| http://localhost:8080/actuator/health | `{"status":"UP"}` |

H2 Console settings: JDBC URL `jdbc:h2:mem:smartbudget`, Username `sa`, Password *(leave blank)*

### Frontend (React)

```bash
cd frontend
npm install
npm run dev
```

Visit http://localhost:5173 to see the full UI with mock data. Every page is navigable. Each page shows a **yellow TODO banner** telling you exactly which ticket connects it to the real API.

---

## Project Structure

```
smartbudget/
|-- backend/
|   |-- src/main/java/com/smartbudget/
|   |   |-- config/        CorsConfig.java             (provided)
|   |   |-- entity/        User, Category, Transaction, SavingsGoal (provided)
|   |   |-- repository/    4 JPA repositories           (provided, you add queries)
|   |   |-- controller/    4 REST controllers            (provided, you refactor)
|   |   |-- model/         BaseTransaction, Income/Expense (YOU BUILD - Day 3)
|   |   |-- service/       TransactionService, SavingsGoalService (YOU BUILD - Day 3/6)
|   |   |-- dao/           DatabaseConnection, TransactionDAO (YOU BUILD - Day 4)
|   |   |-- exception/     Custom exceptions              (YOU BUILD - Day 3/6)
|   |   +-- console/       Main.java console menu          (YOU BUILD - Day 2)
|   |-- src/test/          JUnit + Mockito tests           (YOU BUILD - Day 4/6)
|   +-- Dockerfile                                         (YOU BUILD - Day 10)
|
|-- frontend/
|   |-- src/
|   |   |-- components/    Navbar, Feedback (provided), MonthlySummaryChart (YOU BUILD)
|   |   |-- hooks/         useBudgetAPI.js               (YOU BUILD - Day 8)
|   |   |-- pages/         4 pages with mock data         (provided, you replace mock)
|   |   +-- styles/        global.css                     (provided)
|   |-- Dockerfile                                         (YOU BUILD - Day 10)
|   +-- nginx.conf                                         (YOU BUILD - Day 10)
|
|-- db/                    SQL scripts                     (YOU BUILD - Day 1)
|-- docker-compose.yml                                     (YOU BUILD - Day 10)
+-- .github/workflows/    CI/CD pipeline                   (YOU BUILD - Day 10)
```

---

## Ticket System

Every task is tracked as a ticket: **TICKET-F001** through **TICKET-F116**.

Each ticket has:
- A unique ID (e.g., TICKET-F021)
- A day assignment (Day 1 through Day 10)
- A sprint number (Sprint 0 through Sprint 9)
- A description in the source code with the WHAT / HOW / WHY / OBSERVE format

Look for `TODO TICKET-FXXX` comments in the source files -- they tell you exactly what to build and how.

---

## Prerequisites

Before Day 1, make sure you have:

- [ ] Java 25+ installed (`java -version`)
- [ ] Maven 3.8+ installed (`mvn -version`)
- [ ] Node.js 22+ and npm installed (`node -v`, `npm -v`)
- [ ] PostgreSQL 15+ installed and running (`psql --version`)
- [ ] An IDE (IntelliJ IDEA recommended, VS Code also works)
- [ ] Git installed (`git --version`)
- [ ] Postman or any REST client installed
- [ ] Docker Desktop installed (for Day 10)

---

## How to Use the TODO Comments

Every file you need to modify contains structured TODO comments:

```
// TODO TICKET-F021: Step 1 -- Declare fields
// WHAT: [What this step accomplishes]
// HOW:  [Specific instructions on how to implement it]
// WHY:  [Why this approach is used]
// OBSERVE: [How to verify your implementation works]
```

Follow the steps in order. After each step, run the OBSERVE check to verify it works before moving on.

---

*Good luck! You are about to build a real-world financial application from scratch.*
