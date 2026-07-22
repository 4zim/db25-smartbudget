# Day 7 -- HTML/CSS Module 1 & 2 (Web Foundations)

> TICKET-F069 through TICKET-F076 (Sprint 6)

---

## Overview

Today you build the **static frontend** for SmartBudget using HTML and CSS. This is your bridge between the Spring Boot backend (Days 4-6) and the JavaScript/React frontend (Day 8).

By the end of Day 7 you will have:
- 4 static HTML pages (Dashboard, Transactions, Add Transaction, Savings)
- A shared `style.css` with the Deutsche Bank theme, responsive grid, and form styling

Tomorrow on Day 8 you will learn JavaScript and add the interactivity layer (form validation, fetch, delete, loading spinner) on top of these pages -- those tickets (F077-F081) live with the Day 8 JS module.

---

## Session Plan

### AM -- HTML/CSS Module 1: HTML

**Web Basics**
- Client-server model, HTTP request/response
- Dev environment setup (VS Code, browser dev tools)

**HTML Syntax & Elements**
- HTML syntax, elements, attributes
- Headings, paragraphs, lists, images, links

  Lab: **Semantic HTML Lab -- NYC Blog** (1h)

**Tables**
- `<table>`, `<thead>`, `<tbody>`, `<tr>`, `<th>`, `<td>`

**Forms**
- `<form>`, `<input>`, `<label>`, `<select>`, `<button>`

### PM -- HTML/CSS Module 2: CSS

**CSS Basics & Selectors**
- Inline vs external CSS
- Selectors: id, class, pseudo-class

  Lab: **CSS Selectors Lab -- Healthy Recipes** (1h)

**Visual Rules & Typography**
- Fonts, borders, colors

  Lab: **CSS Visual Rules Lab -- Olivia Woodruff Portfolio** (1h)

**Box Model & Positioning**
- Box Model, positioning, floats

  Lab: **CSS Box Model Lab -- Davie's Burgers** (1h)

**Layout & Responsive Design**
- Flexbox & Grid
- Responsive design & media queries

Break -- 20 min

---

## Sprint 6: Frontend -- HTML/CSS

Build the static frontend in a new folder alongside the React project:

```
smartbudget/
  frontend-static/    <-- create this folder
    index.html          (Dashboard)
    transactions.html   (Transaction list)
    add-transaction.html (Form)
    savings.html        (Savings goals)
    style.css           (Shared stylesheet)
```

> **Note:** The React frontend in `frontend/` is for Day 8. The static pages you build today are standalone. Tomorrow you will add `app.js` to this folder during the Day 8 AM JavaScript tickets.

---

## Tickets

### TICKET-F069: Create `index.html` -- Dashboard page layout
**File:** `frontend-static/index.html`

**Description:** Create the Dashboard HTML page with semantic structure.

**What**
- A semantic `index.html` with header, nav, and main containing three summary cards plus recent-transactions and chart sections.

**Why**
- This is the landing page every other page links back to and the surface JS will hydrate on Day 8 — its `id` hooks must exist before any fetch code runs.

**Observe**
- Opening `frontend-static/index.html` in Chrome shows three empty cards under a nav bar, and DevTools Elements panel reveals `#total-income`, `#total-expenses`, `#net-balance`.

**Instructions:**
1. Add `<!DOCTYPE html>`, `<html>`, `<head>`, `<body>`
2. Add `<meta charset="UTF-8">` and viewport meta
3. Link `style.css` and script `app.js` (with `defer`)
4. Add a `<nav>` with links to all 4 pages
5. Add sections: 3 summary cards (Income, Expenses, Net), Recent Transactions, Chart placeholder

**Acceptance Criteria:**
- [ ] Page opens without errors
- [ ] Navigation links to all 4 pages
- [ ] Semantic tags used (`<nav>`, `<main>`, `<section>`, `<header>`)
- [ ] Title and meta tags present

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Open VS Code, create the `frontend-static/` folder, and `touch index.html`. Inside the empty file type `!` and press Tab — Emmet generates the HTML5 boilerplate. Replace the `<title>`, add the `<link rel="stylesheet">` and `<script defer src="app.js">`, then structure the body with `<header>` + `<nav>` + `<main>` containing your 3 summary cards.

</details>

<details>
<summary><b>Hint 2 — Page skeleton</b></summary>

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>SmartBudget — Dashboard</title>
  <link rel="stylesheet" href="style.css">
  <script defer src="app.js"></script>
</head>
<body>
  <header>
    <h1>SmartBudget</h1>
    <nav>
      <a href="index.html"           class="active">Dashboard</a>
      <a href="transactions.html">Transactions</a>
      <a href="add-transaction.html">Add</a>
      <a href="savings.html">Savings</a>
    </nav>
  </header>
  <main>
    <section class="cards">
      <article class="card"><h3>Total Income</h3><p id="total-income">£0.00</p></article>
      <article class="card"><h3>Total Expenses</h3><p id="total-expenses">£0.00</p></article>
      <article class="card"><h3>Net Balance</h3><p id="net-balance">£0.00</p></article>
    </section>
  </main>
</body>
</html>
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>SmartBudget — Dashboard</title>
  <link rel="stylesheet" href="style.css">
  <script defer src="app.js"></script>
</head>
<body>
  <header>
    <h1>SmartBudget</h1>
    <nav>
      <a href="index.html"           class="active">Dashboard</a>
      <a href="transactions.html">Transactions</a>
      <a href="add-transaction.html">Add</a>
      <a href="savings.html">Savings</a>
    </nav>
  </header>

  <main>
    <section class="cards" aria-label="Summary">
      <article class="card">
        <h3>Total Income</h3>
        <p class="card-value income"   id="total-income">£0.00</p>
      </article>
      <article class="card">
        <h3>Total Expenses</h3>
        <p class="card-value expense"  id="total-expenses">£0.00</p>
      </article>
      <article class="card">
        <h3>Net Balance</h3>
        <p class="card-value"          id="net-balance">£0.00</p>
      </article>
    </section>

    <section aria-label="Recent transactions">
      <h2>Recent Transactions</h2>
      <ul id="recent-txns"></ul>
    </section>

    <section aria-label="Monthly chart">
      <h2>Monthly Summary</h2>
      <div id="chart-placeholder" class="placeholder">
        Chart loads here (Day 9 will replace this with Recharts)
      </div>
    </section>
  </main>
</body>
</html>
```

- `defer` on `<script>` runs JS after the DOM is parsed — no need for `DOMContentLoaded` listeners.
- Every "magic" element JS will touch has an `id` (`total-income`, `recent-txns`, etc.).
- Semantic tags: `<header>`, `<nav>`, `<main>`, `<section>`, `<article>` — screen readers and search engines understand the structure.

Open `frontend-static/index.html` in Chrome via `file://...` or `npx serve frontend-static`. You should see the three empty cards under the header.

</details>

---

### TICKET-F070: Create `transactions.html` -- Transaction list page
**File:** `frontend-static/transactions.html`

**Description:** Create the page that lists all transactions in a table.

**What**
- A `transactions.html` page with a 7-column table (ID, Date, Description, Category, Type, Amount, Actions) and an empty `<tbody id="txn-rows">` ready for JS injection.

**Why**
- The table is the canonical view of all user data; F078 will populate rows via fetch, so the markup hooks must exist now.

**Observe**
- Page loads showing only the styled header row — the body is intentionally empty until Day 8 JS runs.

**Instructions:**
1. Same boilerplate, nav, and script tag as `index.html`
2. Add an `<h1>Transactions</h1>` and a `<table>` with columns: ID, Date, Description, Category, Type, Amount, Actions
3. Use `<thead>` for headers, `<tbody id="txn-rows">` for body (rows are filled by JS in F078)
4. Add a "Loading..." `<div id="loading">` above the table (toggled in F081)

**Acceptance Criteria:**
- [ ] Table headers render
- [ ] `<tbody>` has an `id` JS can target
- [ ] Page links back to dashboard via nav

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Copy the boilerplate from `index.html`. Replace the `<main>` with `<h1>` + `<table>`. The `<tbody>` is intentionally empty — JS will fill it in F078. Don't forget `id="txn-rows"` on the tbody so JS can find it, and `id="loading"` on the spinner div.

</details>

<details>
<summary><b>Hint 2 — Table structure</b></summary>

```html
<main>
  <h1>Transactions</h1>
  <div id="loading">Loading transactions...</div>
  <table id="txn-table">
    <thead>
      <tr>
        <th>ID</th><th>Date</th><th>Description</th>
        <th>Category</th><th>Type</th><th>Amount</th><th>Actions</th>
      </tr>
    </thead>
    <tbody id="txn-rows"></tbody>
  </table>
</main>
```

The Actions column hosts the Delete button (added in F080).

</details>

<details>
<summary><b>Hint 3 — Full page</b></summary>

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>SmartBudget — Transactions</title>
  <link rel="stylesheet" href="style.css">
  <script defer src="app.js"></script>
</head>
<body>
  <header>
    <h1>SmartBudget</h1>
    <nav>
      <a href="index.html">Dashboard</a>
      <a href="transactions.html"  class="active">Transactions</a>
      <a href="add-transaction.html">Add</a>
      <a href="savings.html">Savings</a>
    </nav>
  </header>

  <main>
    <h2>All Transactions</h2>

    <div id="loading" hidden>Loading transactions...</div>
    <div id="error"   hidden class="error-msg"></div>

    <table id="txn-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Date</th>
          <th>Description</th>
          <th>Category</th>
          <th>Type</th>
          <th>Amount</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody id="txn-rows">
        <!-- filled by JavaScript in TICKET-F078 -->
      </tbody>
    </table>
  </main>
</body>
</html>
```

- `hidden` is a native HTML attribute (`display: none` by default). JS toggles it: `loading.hidden = false` while fetching.
- `id="txn-table"` lets CSS target the whole table for styling without affecting other tables on the page.
- The empty `<tbody>` is what the browser renders before JS runs — it's intentional and will look "broken" until Day 8.

</details>

---

### TICKET-F071: Create `add-transaction.html` -- Form with fields
**File:** `frontend-static/add-transaction.html`

**Description:** A form for adding a new transaction.

**What**
- An `add-transaction.html` form with 5 validated fields (Amount, Date, Description, Type, Category), proper `<label for>` pairing, and a submit button.

**Why**
- This is the only way users create new data; correct field types and `required` attributes let the browser enforce basic validation before any JS exists.

**Observe**
- Submitting the empty form triggers Chrome's native red-tooltip "Please fill out this field" — no JavaScript yet, pure HTML5 validation.

**Instructions:**
1. Form fields:
   - Amount (`type="number"`, `step="0.01"`, `min="0.01"`, `required`)
   - Date (`type="date"`, `required`)
   - Description (`type="text"`, `maxlength="200"`, `required`)
   - Type (`<select>`: INCOME / EXPENSE)
   - Category (`<select>`: Salary, Groceries, Rent, Utilities, Entertainment, Other)
2. Each input must have a matching `<label for="...">`
3. Submit button: `<button type="submit">Add Transaction</button>`
4. Add an empty `<div id="form-message"></div>` for success/error messages (filled by JS in F079)

**Acceptance Criteria:**
- [ ] All 5 fields render
- [ ] Browser-native validation works on submit (try leaving Amount empty)
- [ ] Labels are correctly tied to inputs
- [ ] Form has `id="add-form"` so JS can find it

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Wrap the 5 fields in `<form id="add-form">`. Pair each `<input>` with a `<label for="<inputId>">`. The browser does the basic validation (`required`, `min`, `maxlength`) — no JS yet, just submit and see Chrome's red tooltips.

</details>

<details>
<summary><b>Hint 2 — Form skeleton</b></summary>

```html
<form id="add-form" novalidate>
  <label for="amount">Amount</label>
  <input id="amount" name="amount" type="number" step="0.01" min="0.01" required>

  <label for="date">Date</label>
  <input id="date" name="date" type="date" required>

  <label for="description">Description</label>
  <input id="description" name="description" type="text" maxlength="200" required>

  <label for="type">Type</label>
  <select id="type" name="type" required>
    <option value="">--</option>
    <option value="INCOME">Income</option>
    <option value="EXPENSE">Expense</option>
  </select>

  <label for="category">Category</label>
  <select id="category" name="category" required>
    <option value="">--</option>
    <option>Salary</option><option>Groceries</option><option>Rent</option>
    <option>Utilities</option><option>Entertainment</option><option>Other</option>
  </select>

  <button type="submit">Add Transaction</button>
</form>

<div id="form-message" role="status"></div>
```

`novalidate` lets the JS handler (F077) take over. Remove it during testing today to see the native browser validation.

</details>

<details>
<summary><b>Hint 3 — Full page</b></summary>

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>SmartBudget — Add Transaction</title>
  <link rel="stylesheet" href="style.css">
  <script defer src="app.js"></script>
</head>
<body>
  <header>
    <h1>SmartBudget</h1>
    <nav>
      <a href="index.html">Dashboard</a>
      <a href="transactions.html">Transactions</a>
      <a href="add-transaction.html" class="active">Add</a>
      <a href="savings.html">Savings</a>
    </nav>
  </header>

  <main>
    <h2>Add Transaction</h2>

    <form id="add-form">
      <label for="amount">Amount (£)</label>
      <input id="amount" name="amount" type="number"
             step="0.01" min="0.01" required>

      <label for="date">Date</label>
      <input id="date" name="date" type="date" required>

      <label for="description">Description</label>
      <input id="description" name="description" type="text"
             maxlength="200" required>

      <label for="type">Type</label>
      <select id="type" name="type" required>
        <option value="" disabled selected>Choose type…</option>
        <option value="INCOME">Income</option>
        <option value="EXPENSE">Expense</option>
      </select>

      <label for="category">Category</label>
      <select id="category" name="category" required>
        <option value="" disabled selected>Choose category…</option>
        <option>Salary</option>
        <option>Groceries</option>
        <option>Rent</option>
        <option>Utilities</option>
        <option>Entertainment</option>
        <option>Other</option>
      </select>

      <button type="submit">Add Transaction</button>
    </form>

    <div id="form-message" role="status" aria-live="polite"></div>
  </main>
</body>
</html>
```

Try submitting the empty form — Chrome highlights the first invalid field with "Please fill out this field." This is built-in HTML5 form validation, no JavaScript involved.

`aria-live="polite"` makes screen readers announce updates to `#form-message` (helpful for blind users when JS writes "Saved!" there).

</details>

---

### TICKET-F072: Create `savings.html` -- Savings goals display
**File:** `frontend-static/savings.html`

**Description:** Page showing savings goals with progress bars.

**What**
- A `savings.html` page with a static `<article class="goal-card">` design reference and an empty `<div id="goals-list">` container for JS-driven cards.

**Why**
- The static card defines the visual contract — CSS (F073/F076) and Day 8 JS both need a concrete shape to target before real goals exist.

**Observe**
- Opening the page shows one example goal card with a 65%-filled progress bar; the empty `#goals-list` div sits underneath, invisible until JS populates it.

**Instructions:**
1. Add an empty `<div id="goals-list"></div>` (filled by JS later)
2. Add a static example card showing how a goal should look:
   ```html
   <article class="goal-card">
     <h3>Emergency Fund</h3>
     <p>$650 of $1,000 target</p>
     <div class="progress-bar">
       <div class="progress-fill" style="width: 65%"></div>
     </div>
     <p class="progress-text">65%</p>
   </article>
   ```
3. The static card is the design reference -- it can stay or be removed once JS populates real goals

**Acceptance Criteria:**
- [ ] Static example card renders with visible progress bar
- [ ] Container `<div id="goals-list">` is present

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Same boilerplate, nav, header. Inside `<main>`, drop in **one** static `<article class="goal-card">` as a design reference, then an empty `<div id="goals-list">` underneath. JS will replace the static example with real data later (or you can remove the example then).

</details>

<details>
<summary><b>Hint 2 — Goal card markup</b></summary>

```html
<main>
  <h2>Savings Goals</h2>

  <article class="goal-card">
    <h3>Emergency Fund</h3>
    <p>£650 of £1,000 target</p>
    <div class="progress-bar">
      <div class="progress-fill" style="width: 65%"></div>
    </div>
    <p class="progress-text">65%</p>
  </article>

  <div id="goals-list"></div>
</main>
```

Two divs implement the progress bar: outer container has the grey background; inner `progress-fill` has the coloured fill width.

</details>

<details>
<summary><b>Hint 3 — Full page</b></summary>

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>SmartBudget — Savings</title>
  <link rel="stylesheet" href="style.css">
  <script defer src="app.js"></script>
</head>
<body>
  <header>
    <h1>SmartBudget</h1>
    <nav>
      <a href="index.html">Dashboard</a>
      <a href="transactions.html">Transactions</a>
      <a href="add-transaction.html">Add</a>
      <a href="savings.html" class="active">Savings</a>
    </nav>
  </header>

  <main>
    <h2>Savings Goals</h2>

    <!-- Design reference; remove once JS populates real cards -->
    <article class="goal-card">
      <h3>Emergency Fund (example)</h3>
      <p>£650 of £1,000 target</p>
      <div class="progress-bar"><div class="progress-fill" style="width:65%"></div></div>
      <p class="progress-text">65% complete</p>
    </article>

    <div id="goals-list"></div>
  </main>
</body>
</html>
```

CSS for the progress bar (will be added in F073/F076 expansion):

```css
.goal-card { background: var(--card-bg); padding: 1rem; margin-bottom: 1rem;
             border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,.08); }
.progress-bar { height: 12px; background: #e0e0e0; border-radius: 6px; overflow: hidden; }
.progress-fill { height: 100%; background: var(--db-blue); transition: width .3s; }
```

</details>

---

### TICKET-F073: Write `style.css` -- Color scheme & responsive grid
**File:** `frontend-static/style.css`

**Description:** The shared stylesheet for all 4 pages.

**What**
- A `style.css` defining DB-Blue CSS variables, base body styles, and a 3-column grid for `.cards` that collapses to 1 column under 768px.

**Why**
- A single shared stylesheet keeps the four pages visually consistent; CSS variables make the DB-Blue theme change a one-line edit later.

**Observe**
- All four pages render with the same grey background and DB-Blue accent; resizing Chrome below 768px collapses the dashboard cards into a single stacked column.

**Instructions:**
1. Define CSS variables on `:root`:
   ```css
   :root {
     --db-blue: #003366;
     --db-blue-light: #1a4d80;
     --income: #2e7d32;
     --expense: #c62828;
     --bg: #f5f5f5;
     --card-bg: #ffffff;
   }
   ```
2. Body base styles: font-family, background `var(--bg)`, margin reset
3. Dashboard summary cards use CSS Grid: `display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem;`
4. Media query at `max-width: 768px` collapses to 1 column

**Acceptance Criteria:**
- [ ] All 4 pages link to the same `style.css`
- [ ] Dashboard cards lay out in a 3-column grid
- [ ] Cards collapse to 1 column on narrow screens

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Define CSS variables on `:root` so every other rule references them. The cards grid is `display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem;`. The mobile breakpoint at 768px swaps that to `1fr` (single column).

</details>

<details>
<summary><b>Hint 2 — Core styles</b></summary>

```css
:root {
  --db-blue:       #003366;
  --db-blue-light: #1a4d80;
  --income:        #2e7d32;
  --expense:       #c62828;
  --bg:            #f5f5f5;
  --card-bg:       #ffffff;
}

* { box-sizing: border-box; }
body { margin: 0; font-family: system-ui, -apple-system, sans-serif;
       background: var(--bg); color: #222; }
main { max-width: 1100px; margin: 1.5rem auto; padding: 0 1rem; }

.cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
}
.card {
  background: var(--card-bg);
  padding: 1.25rem;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,.08);
}
.card-value { font-size: 1.75rem; font-weight: 600; margin: .5rem 0 0; }
.card-value.income  { color: var(--income); }
.card-value.expense { color: var(--expense); }

@media (max-width: 768px) {
  .cards { grid-template-columns: 1fr; }
}
```

</details>

<details>
<summary><b>Hint 3 — Full base styles + responsive helpers</b></summary>

```css
/* ─────────────────────────────────────────────────────────
   SmartBudget — base stylesheet
   ───────────────────────────────────────────────────────── */

:root {
  --db-blue:        #003366;
  --db-blue-light:  #1a4d80;
  --db-gold:        #C8A951;
  --income:         #2e7d32;
  --expense:        #c62828;
  --bg:             #f5f5f5;
  --card-bg:        #ffffff;
  --text:           #222;
  --muted:          #666;
  --radius:         8px;
  --shadow:         0 1px 4px rgba(0,0,0,.08);
}

* { box-sizing: border-box; }
html, body { margin: 0; padding: 0; }
body {
  font-family: system-ui, -apple-system, "Segoe UI", Roboto, sans-serif;
  background: var(--bg);
  color: var(--text);
  line-height: 1.5;
}

main {
  max-width: 1100px;
  margin: 1.5rem auto;
  padding: 0 1rem;
}

h1, h2, h3 { margin-top: 0; }

/* ─── Dashboard cards ───────────────────────────────────── */
.cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 2rem;
}
.card {
  background: var(--card-bg);
  padding: 1.25rem;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
}
.card h3 { font-size: .9rem; color: var(--muted); text-transform: uppercase;
           letter-spacing: .04em; margin-bottom: .25rem; }
.card-value { font-size: 1.75rem; font-weight: 600; margin: 0; }
.card-value.income  { color: var(--income); }
.card-value.expense { color: var(--expense); }

/* ─── Responsive breakpoints ────────────────────────────── */
@media (max-width: 768px) {
  .cards { grid-template-columns: 1fr; }
  main { margin-top: 1rem; }
}
```

Test in Chrome DevTools → Toggle device toolbar → switch to iPhone width — cards stack vertically.

</details>

---

### TICKET-F074: Style navigation bar with active state
**File:** `frontend-static/style.css`

**Description:** Style the `<nav>` so it's visually clear and shows which page you're on.

**What**
- CSS rules giving `<nav>` a DB-Blue background, white links with hover state, and an `.active` modifier with a gold bottom border.

**Why**
- Users must always know which page they're on; a styled active state is the cheapest possible wayfinding signal.

**Observe**
- Clicking through the four pages shows the gold underline jump to the current page's link; hovering any other link darkens its background.

**Instructions:**
1. `nav` background: `var(--db-blue)`, padding `1rem`
2. `nav a`: white, no underline, padding `0.5rem 1rem`, margin-right
3. `nav a:hover`: background `var(--db-blue-light)`
4. On each HTML page, add `class="active"` to the current page's `<a>` tag and style `nav a.active` with a bottom border

**Acceptance Criteria:**
- [ ] Nav bar is DB-Blue with white links
- [ ] Hover changes the link's background
- [ ] Active page link is visually distinct

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Wrap the nav in a flex container so links sit in a row. Use `nav a.active` (set on each page's current link) for the bottom border. Keep links accessible — don't remove the focus outline, just style it.

</details>

<details>
<summary><b>Hint 2 — Nav rules</b></summary>

```css
header {
  background: var(--db-blue);
  color: white;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
header h1 { margin: 0; font-size: 1.25rem; }

nav { display: flex; gap: .25rem; }
nav a {
  color: white;
  text-decoration: none;
  padding: .5rem 1rem;
  border-radius: 4px;
  transition: background .15s;
}
nav a:hover  { background: var(--db-blue-light); }
nav a.active { border-bottom: 2px solid var(--db-gold); }
nav a:focus-visible { outline: 2px solid white; outline-offset: 2px; }
```

</details>

<details>
<summary><b>Hint 3 — Full nav styling + mobile-friendly</b></summary>

```css
/* ─── Header & navigation ──────────────────────────────── */
header {
  background: var(--db-blue);
  color: #fff;
  padding: 1rem 1.5rem;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
}
header h1 {
  margin: 0;
  font-size: 1.25rem;
  letter-spacing: .02em;
}

nav {
  display: flex;
  flex-wrap: wrap;
  gap: .25rem;
}
nav a {
  color: #fff;
  text-decoration: none;
  padding: .5rem 1rem;
  border-radius: 4px;
  font-weight: 500;
  transition: background .15s, border-color .15s;
}
nav a:hover {
  background: var(--db-blue-light);
}
nav a.active {
  background: var(--db-blue-light);
  border-bottom: 2px solid var(--db-gold);
}
nav a:focus-visible {
  outline: 2px solid #fff;
  outline-offset: 2px;
}

@media (max-width: 480px) {
  header { flex-direction: column; align-items: flex-start; }
  nav    { width: 100%; }
  nav a  { flex: 1; text-align: center; }
}
```

Remember to set `class="active"` manually on the current page's nav link in each HTML file (no JS needed). Or, on Day 8, do it dynamically:

```js
document.querySelectorAll('nav a').forEach(a => {
  if (a.href === location.href) a.classList.add('active');
});
```

</details>

---

### TICKET-F075: Style transaction table with alternating row colors
**File:** `frontend-static/style.css`

**Description:** Make the transactions table readable.

**What**
- CSS rules collapsing table borders, zebra-striping even rows, highlighting on hover, and colouring `.amount.income` green / `.amount.expense` red.

**Why**
- Dense tabular data is unreadable without alternating row colours; pre-defining income/expense colour classes lets F078 JS just toggle a class instead of inline styles.

**Observe**
- The transactions page renders alternating white/grey rows under a DB-Blue header row; hovering any row turns it pale blue.

**Instructions:**
1. `table { border-collapse: collapse; width: 100%; }`
2. `th, td { padding: 0.75rem; text-align: left; border-bottom: 1px solid #ddd; }`
3. `tbody tr:nth-child(even) { background: #fafafa; }` (zebra striping)
4. `tbody tr:hover { background: #eef5fb; }`
5. Add a `td.amount.income` (green) and `td.amount.expense` (red) class so JS can color amounts in F078

**Acceptance Criteria:**
- [ ] Headers are bold and visually separated
- [ ] Alternating row colors
- [ ] Hover highlight on rows

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`table { border-collapse: collapse; }` removes the gap between cell borders — essential for clean tables. Zebra-stripe via `tbody tr:nth-child(even)`. Add `.amount.income` / `.amount.expense` colour classes so JS can mark the amount cells.

</details>

<details>
<summary><b>Hint 2 — Table rules</b></summary>

```css
table {
  width: 100%;
  border-collapse: collapse;
  background: var(--card-bg);
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow);
}
th, td {
  padding: .75rem 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}
thead {
  background: var(--db-blue);
  color: #fff;
}
tbody tr:nth-child(even) { background: #fafafa; }
tbody tr:hover           { background: #eef5fb; }

td.amount.income  { color: var(--income);  font-weight: 600; text-align: right; }
td.amount.expense { color: var(--expense); font-weight: 600; text-align: right; }
```

</details>

<details>
<summary><b>Hint 3 — Full table styles + responsive scroll</b></summary>

```css
/* ─── Table ────────────────────────────────────────────── */
table {
  width: 100%;
  border-collapse: collapse;
  background: var(--card-bg);
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow);
  margin: 1rem 0;
}

thead {
  background: var(--db-blue);
  color: #fff;
}

th, td {
  padding: .75rem 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
  vertical-align: middle;
}

th {
  font-weight: 600;
  letter-spacing: .02em;
  text-transform: uppercase;
  font-size: .8rem;
}

tbody tr:nth-child(even) { background: #fafafa; }
tbody tr:hover           { background: #eef5fb; }
tbody tr:last-child td   { border-bottom: 0; }

/* Amount cells — coloured by INCOME / EXPENSE class (added by JS) */
td.amount         { text-align: right; font-variant-numeric: tabular-nums; }
td.amount.income  { color: var(--income);  font-weight: 600; }
td.amount.expense { color: var(--expense); font-weight: 600; }

/* Loading + error helpers */
#loading { padding: 1rem; color: var(--muted); font-style: italic; }
.error-msg {
  background: #ffebee; color: var(--expense);
  padding: .75rem 1rem; border-radius: var(--radius); margin: 1rem 0;
}

/* On small screens, wrap the whole table in a horizontal scroll container */
@media (max-width: 768px) {
  table { display: block; overflow-x: auto; white-space: nowrap; }
}
```

`font-variant-numeric: tabular-nums` makes numerals all the same width — amounts line up vertically. `border-collapse: collapse` is essential or you get visible double borders between cells.

</details>

---

### TICKET-F076: Style form inputs with focus states
**File:** `frontend-static/style.css`

**Description:** Style inputs on the Add Transaction form.

**What**
- CSS rules giving inputs/selects consistent padding and borders, a visible DB-Blue focus ring, red `:invalid` highlighting, and a styled submit button.

**Why**
- Visible focus rings are an accessibility requirement and invalid-state colouring tells users what's wrong before they read any error message.

**Observe**
- Tabbing through the Add Transaction form draws a blue outline around each focused field; submitting an empty form turns the offending input borders red.

**Instructions:**
1. All inputs and selects: `padding: 0.5rem`, `border: 1px solid #ccc`, `border-radius: 4px`, full-width
2. Labels stacked above inputs (`display: block`)
3. `input:focus, select:focus`: `outline: 2px solid var(--db-blue); outline-offset: 2px;`
4. `input:invalid` (after submit attempt): `border-color: var(--expense)`
5. Submit button: DB-Blue background, white text, hover state

**Acceptance Criteria:**
- [ ] Inputs have visible focus ring when clicked
- [ ] Invalid fields highlight in red on submit
- [ ] Form looks clean on desktop and mobile

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Style `input, select` together — same padding/border/radius. Stack labels above inputs with `display: block`. Use `:focus-visible` (not `:focus`) so the ring only shows for keyboard users, not mouse clicks. The browser sets `:invalid` automatically when a `required` field is empty after submit attempt.

</details>

<details>
<summary><b>Hint 2 — Form rules</b></summary>

```css
form { max-width: 480px; }
label { display: block; margin: 1rem 0 .25rem; font-weight: 500; }
input, select {
  width: 100%; padding: .5rem;
  border: 1px solid #ccc; border-radius: 4px;
  font: inherit;
}
input:focus, select:focus {
  outline: 2px solid var(--db-blue);
  outline-offset: 2px;
  border-color: var(--db-blue);
}
input:invalid { border-color: var(--expense); }

button[type="submit"] {
  margin-top: 1.5rem; padding: .75rem 1.5rem;
  background: var(--db-blue); color: #fff;
  border: none; border-radius: 4px;
  font-weight: 600; cursor: pointer;
}
button[type="submit"]:hover { background: var(--db-blue-light); }
```

</details>

<details>
<summary><b>Hint 3 — Full form styles + success/error message</b></summary>

```css
/* ─── Form ─────────────────────────────────────────────── */
form {
  max-width: 480px;
  background: var(--card-bg);
  padding: 1.5rem;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
}

label {
  display: block;
  margin: 1rem 0 .25rem;
  font-weight: 500;
  font-size: .9rem;
}
label:first-of-type { margin-top: 0; }

input, select {
  width: 100%;
  padding: .55rem .7rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font: inherit;
  background: #fff;
  transition: border-color .15s, box-shadow .15s;
}
input:focus-visible,
select:focus-visible {
  outline: none;
  border-color: var(--db-blue);
  box-shadow: 0 0 0 3px rgba(0, 51, 102, .15);
}

/* Show :invalid only AFTER the user has tried to submit */
form.was-submitted input:invalid,
form.was-submitted select:invalid {
  border-color: var(--expense);
  background: #fff5f5;
}

button[type="submit"] {
  margin-top: 1.5rem;
  padding: .75rem 1.75rem;
  background: var(--db-blue);
  color: #fff;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: background .15s;
}
button[type="submit"]:hover  { background: var(--db-blue-light); }
button[type="submit"]:active { transform: translateY(1px); }

#form-message {
  margin-top: 1rem;
  padding: .75rem 1rem;
  border-radius: var(--radius);
  display: none;
}
#form-message.success { display: block; background: #e8f5e9; color: var(--income); }
#form-message.error   { display: block; background: #ffebee; color: var(--expense); }
```

On Day 8 the JS will:
```js
form.classList.add('was-submitted');     // unlock the :invalid CSS
formMessage.classList.add('success');     // or 'error'
formMessage.textContent = 'Transaction saved';
```

Open all 4 pages on desktop and at iPhone width — every interactive element should have a visible focus ring when tabbed to, and labels should never be ambiguous about which input they belong to.

</details>

---

## End-of-Day Checklist

- [ ] 4 static HTML pages created in `frontend-static/`
- [ ] Shared `style.css` with DB-Blue theme, responsive grid, table/form styling
- [ ] Pages render correctly on both desktop and mobile widths
- [ ] You can explain: client-server model, semantic HTML, the box model, Flexbox vs Grid

---

*Tomorrow (Day 8): You learn JavaScript properly -- syntax, scope, arrays, objects, the DOM -- and apply it to today's pages (F077-F081) before moving to React.*
