# Day 9 -- React Polish & Features (Sprint 8)

> TICKET-F095 through TICKET-F107

---

## Overview

Today you add **advanced features** to the React frontend:

1. Filter transactions by type, date range, and search text
2. Build a monthly summary bar chart using Recharts
3. Add edit transaction functionality
4. Add contribute-to-goal functionality
5. Add toast notifications and UI polish

By the end of Day 9, SmartBudget looks and feels like a real production application.

---

## Key Concepts

- **Client-side filtering**: Filter data in React state without calling the API again
- **Recharts**: A React charting library built on D3
- **Controlled forms for editing**: Pre-populate form fields with existing data
- **Toast notifications**: Brief success/error messages that auto-dismiss
- **UI/UX polish**: Consistent spacing, loading states, empty states, confirmation dialogs

---

## Tickets

### TICKET-F095: Filter by Transaction Type
**File:** `frontend/src/pages/TransactionList.jsx`

**Description:** Add a dropdown filter for INCOME / EXPENSE / ALL.

**What**
- A `<select>` control above the transaction table that narrows the visible rows to INCOME, EXPENSE, or all of them.

**Why**
- Users scanning for one type of activity (e.g. "what did I spend?") shouldn't have to read past unrelated rows; this is the cheapest UX win possible.

**Observe**
- Selecting "Expense" hides every INCOME row in real time without a network call; the "Showing X of Y" counter updates on the same render.

**Instructions:**
1. Add a `<select>` dropdown above the table with options: All, Income, Expense
2. Store the selected filter in `useState`
3. Filter the transactions array before rendering:
   ```js
   const filtered = typeFilter === "ALL"
     ? transactions
     : transactions.filter(t => t.type === typeFilter);
   ```
4. Render `filtered` instead of `transactions`

**Acceptance Criteria:**
- [ ] Dropdown shows 3 options: All, Income, Expense
- [ ] Selecting "Income" shows only income transactions
- [ ] Selecting "Expense" shows only expense transactions
- [ ] Selecting "All" shows everything
- [ ] Transaction count updates to reflect filtered results

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`useState("ALL")` for the filter value. Controlled `<select>` reading/writing it. Filter the txns array right before `.map`. Don't re-fetch — this is client-side filtering on data you already have.

</details>

<details>
<summary><b>Hint 2 — In the component</b></summary>

```jsx
const [typeFilter, setTypeFilter] = useState("ALL");

const filtered = typeFilter === "ALL"
  ? transactions
  : transactions.filter(t => t.type === typeFilter);

return (
  <>
    <select value={typeFilter} onChange={e => setTypeFilter(e.target.value)}>
      <option value="ALL">All</option>
      <option value="INCOME">Income only</option>
      <option value="EXPENSE">Expense only</option>
    </select>
    <p>Showing {filtered.length} of {transactions.length}</p>
    {filtered.map(t => <TransactionRow key={t.txnId} txn={t} />)}
  </>
);
```

</details>

<details>
<summary><b>Hint 3 — Full integration</b></summary>

```jsx
import { useState } from "react";
import { useTransactionData } from "../hooks/useBudgetAPI";
import TransactionRow from "../components/TransactionRow";

export default function TransactionList() {
  const { data: txns = [], loading, error } = useTransactionData();
  const [typeFilter, setTypeFilter] = useState("ALL");

  if (loading) return <Spinner />;
  if (error)   return <ErrorBanner message={error.message} />;

  const filtered = typeFilter === "ALL"
      ? txns
      : txns.filter(t => t.type === typeFilter);

  return (
    <main className="page">
      <h2>Transactions</h2>

      <div className="filter-bar">
        <label>
          Type:
          <select value={typeFilter} onChange={e => setTypeFilter(e.target.value)}>
            <option value="ALL">All</option>
            <option value="INCOME">Income</option>
            <option value="EXPENSE">Expense</option>
          </select>
        </label>
        <span>Showing {filtered.length} of {txns.length}</span>
      </div>

      <table>
        <thead><tr><th>Date</th><th>Description</th><th>Category</th>
                   <th>Type</th><th>Amount</th><th></th></tr></thead>
        <tbody>
          {filtered.length === 0
            ? <tr><td colSpan="6">No transactions match this filter.</td></tr>
            : filtered.map(t =>
                <TransactionRow key={t.txnId} txn={t}
                                onDelete={id => console.log("delete", id)} />)}
        </tbody>
      </table>
    </main>
  );
}
```

`txns` (server data) is the source of truth — `filtered` is derived per render. React re-runs the filter on every state change; that's cheap for any list under thousands of rows. For huge lists, wrap in `useMemo` so the filter only re-computes when its inputs change.

</details>

---

### TICKET-F096: Filter by Date Range
**File:** `frontend/src/pages/TransactionList.jsx`

**Description:** Add date range inputs to filter transactions.

**What**
- Two `<input type="date">` fields (From / To) that further restrict the already-type-filtered list to a date window.

**Why**
- Month-end reviews, tax periods, and "what happened last week?" all need a date scope; the type filter alone can't answer those.

**Observe**
- Setting From=2026-02-01 with the type filter on Expense shows only February expense rows; clearing both date inputs widens the list back out without losing the type filter.

**Instructions:**
1. Add two `<input type="date">` fields: From and To
2. Store both dates in useState
3. Chain the filter: first by type, then by date range
4. A transaction matches if its date is >= from AND <= to
5. If no dates selected, show all transactions

**Acceptance Criteria:**
- [ ] Two date pickers appear in the filter bar
- [ ] Setting both dates filters transactions to that range
- [ ] Clearing dates shows all transactions again
- [ ] Date filter works together with the type filter (both apply)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Two more `useState`s — `from` and `to` — both `""` initially. Two `<input type="date">` bound to them. Chain a `.filter` after the type filter, comparing `t.txnDate` against `from` and `to`. Empty string means "no constraint" — let it through.

</details>

<details>
<summary><b>Hint 2 — Filter chain</b></summary>

```jsx
const [typeFilter, setTypeFilter] = useState("ALL");
const [from,       setFrom]       = useState("");
const [to,         setTo]         = useState("");

const filtered = txns
  .filter(t => typeFilter === "ALL" || t.type === typeFilter)
  .filter(t => !from || t.txnDate >= from)
  .filter(t => !to   || t.txnDate <= to);
```

ISO date strings (`"2026-01-15"`) compare lexically with `>=` and `<=` — no need to parse to `Date`.

</details>

<details>
<summary><b>Hint 3 — Full filter bar + clear button</b></summary>

```jsx
const [typeFilter, setTypeFilter] = useState("ALL");
const [from,       setFrom]       = useState("");
const [to,         setTo]         = useState("");

const filtered = txns
  .filter(t => typeFilter === "ALL" || t.type === typeFilter)
  .filter(t => !from || t.txnDate >= from)
  .filter(t => !to   || t.txnDate <= to);

return (
  <main className="page">
    <h2>Transactions</h2>

    <div className="filter-bar"
         style={{ display: "flex", gap: "1rem", flexWrap: "wrap",
                  background: "#fff", padding: "1rem",
                  borderRadius: 8, marginBottom: "1rem",
                  boxShadow: "0 1px 4px rgba(0,0,0,.08)" }}>
      <label>Type
        <select value={typeFilter} onChange={e => setTypeFilter(e.target.value)}>
          <option value="ALL">All</option>
          <option value="INCOME">Income</option>
          <option value="EXPENSE">Expense</option>
        </select>
      </label>

      <label>From
        <input type="date" value={from} onChange={e => setFrom(e.target.value)} />
      </label>

      <label>To
        <input type="date" value={to}   onChange={e => setTo(e.target.value)} />
      </label>

      <button onClick={() => { setTypeFilter("ALL"); setFrom(""); setTo(""); }}>
        Clear filters
      </button>

      <span style={{ alignSelf: "center" }}>
        Showing {filtered.length} of {txns.length}
      </span>
    </div>

    {/* table renders `filtered` */}
  </main>
);
```

Validation note: if a user sets `from > to`, you'd see an empty result. You could call that out ("From cannot be after To") — not in the acceptance criteria but nice UX.

</details>

---

### TICKET-F097: Search by Description
**File:** `frontend/src/pages/TransactionList.jsx`

**Description:** Add a text search input to filter by description.

**What**
- A case-insensitive substring search box over `transaction.description`, chained after the type and date filters.

**Why**
- Type + date narrows the list; free-text search is what gets a user to a single row ("grocery", "Amazon refund") without scrolling.

**Observe**
- Typing "salary" with no other filters set leaves only rows whose description contains "salary"/"Salary"/"SALARY"; clearing the box restores the previously-filtered set.

**Instructions:**
1. Add a text input with placeholder "Search transactions..."
2. Filter transactions where `description.toLowerCase().includes(searchText.toLowerCase())`
3. Chain with existing type and date filters

**Acceptance Criteria:**
- [ ] Typing "salary" shows only transactions with "salary" in the description
- [ ] Search is case-insensitive
- [ ] Clearing the search shows all transactions
- [ ] All 3 filters (type, date, search) work together

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Third `useState("")` for the search text. One more `.filter()` step: `t.description?.toLowerCase().includes(search.toLowerCase())`. Use optional chaining (`?.`) so a null description doesn't crash.

</details>

<details>
<summary><b>Hint 2 — Filter chain</b></summary>

```jsx
const [search, setSearch] = useState("");

const filtered = txns
  .filter(t => typeFilter === "ALL" || t.type === typeFilter)
  .filter(t => !from || t.txnDate >= from)
  .filter(t => !to   || t.txnDate <= to)
  .filter(t => !search ||
               t.description?.toLowerCase().includes(search.toLowerCase()));

return (
  <input type="search" placeholder="Search transactions..."
         value={search} onChange={e => setSearch(e.target.value)} />
);
```

`type="search"` gives the input a built-in clear (×) button in most browsers.

</details>

<details>
<summary><b>Hint 3 — Full search input + perf with useMemo</b></summary>

```jsx
import { useState, useMemo } from "react";

const [typeFilter, setTypeFilter] = useState("ALL");
const [from,       setFrom]       = useState("");
const [to,         setTo]         = useState("");
const [search,     setSearch]     = useState("");

const filtered = useMemo(() => {
  const q = search.trim().toLowerCase();
  return txns
    .filter(t => typeFilter === "ALL" || t.type === typeFilter)
    .filter(t => !from || t.txnDate >= from)
    .filter(t => !to   || t.txnDate <= to)
    .filter(t => !q    || (t.description ?? "").toLowerCase().includes(q));
}, [txns, typeFilter, from, to, search]);
```

`useMemo` skips re-computing when the dependencies haven't changed. With small lists you won't notice the difference; it's the right habit for big ones.

```jsx
<input
  type="search"
  placeholder="Search transactions…"
  value={search}
  onChange={e => setSearch(e.target.value)}
  style={{ flex: 1, minWidth: "200px" }}
/>
```

Test combinations:
- Type=Expense + From=2026-02-01 + Search="grocer" → only expense rows from Feb onwards containing "grocer".
- Clear search → all expense rows from Feb onwards.
- Clear filters button (added in F098) → back to everything.

</details>

---

### TICKET-F098: Filter Bar Component
**File:** `frontend/src/pages/TransactionList.jsx`

**Description:** Organize all filters into a clean filter bar UI.

**What**
- A single `<FilterBar>` component that owns the type/date/search inputs, a Clear Filters button, and the "Showing X of Y" counter.

**Why**
- Three loose controls scattered above the table look unfinished and bloat `TransactionList`; a named component sets visual hierarchy and isolates the filter UI for reuse/testing.

**Observe**
- All four controls sit inside one styled container; Clear Filters resets every input in one click; the bar wraps to a column under ~600px viewport width.

**Instructions:**
1. Group the type dropdown, date inputs, and search input in a styled container
2. Add a "Clear Filters" button that resets all filters
3. Show the count of filtered results: "Showing X of Y transactions"

**Acceptance Criteria:**
- [ ] All filters are in a visually grouped bar
- [ ] Clear Filters resets everything
- [ ] Result count updates as filters change
- [ ] Filter bar is responsive (stacks on mobile)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Extract the filter UI into a `<FilterBar>` component with props for state + setters. Style with flexbox + a media query for mobile. The Clear Filters button calls all the setters at once.

</details>

<details>
<summary><b>Hint 2 — Component skeleton</b></summary>

```jsx
// src/components/FilterBar.jsx
export default function FilterBar({
  typeFilter, setTypeFilter,
  from, setFrom, to, setTo,
  search, setSearch,
  visibleCount, totalCount
}) {
  function clearAll() {
    setTypeFilter("ALL"); setFrom(""); setTo(""); setSearch("");
  }
  return (
    <div className="filter-bar">
      {/* inputs */}
      <button onClick={clearAll}>Clear filters</button>
      <span>Showing {visibleCount} of {totalCount}</span>
    </div>
  );
}
```

Page now:
```jsx
<FilterBar
  typeFilter={typeFilter} setTypeFilter={setTypeFilter}
  from={from} setFrom={setFrom} to={to} setTo={setTo}
  search={search} setSearch={setSearch}
  visibleCount={filtered.length} totalCount={txns.length}
/>
```

</details>

<details>
<summary><b>Hint 3 — Full FilterBar + CSS</b></summary>

```jsx
// src/components/FilterBar.jsx
import "./FilterBar.css";

export default function FilterBar({
  typeFilter, setTypeFilter,
  from, setFrom, to, setTo,
  search, setSearch,
  visibleCount, totalCount,
}) {
  function clearAll() {
    setTypeFilter("ALL");
    setFrom(""); setTo("");
    setSearch("");
  }
  const hasFilters = typeFilter !== "ALL" || from || to || search;

  return (
    <div className="filter-bar" role="search">
      <label>
        Type
        <select value={typeFilter} onChange={e => setTypeFilter(e.target.value)}>
          <option value="ALL">All</option>
          <option value="INCOME">Income</option>
          <option value="EXPENSE">Expense</option>
        </select>
      </label>
      <label>
        From <input type="date" value={from} onChange={e => setFrom(e.target.value)} />
      </label>
      <label>
        To <input type="date" value={to}   onChange={e => setTo(e.target.value)} />
      </label>
      <label className="grow">
        Search
        <input type="search" placeholder="Description contains…"
               value={search} onChange={e => setSearch(e.target.value)} />
      </label>
      <button onClick={clearAll} disabled={!hasFilters}>
        Clear filters
      </button>
      <span className="count">
        Showing <b>{visibleCount}</b> of <b>{totalCount}</b>
      </span>
    </div>
  );
}
```

```css
/* src/components/FilterBar.css */
.filter-bar {
  display: flex; flex-wrap: wrap; gap: 1rem; align-items: end;
  background: #fff; padding: 1rem; border-radius: 8px;
  margin-bottom: 1rem; box-shadow: 0 1px 4px rgba(0,0,0,.08);
}
.filter-bar label  { display: flex; flex-direction: column;
                     font-size: .85rem; color: #555; }
.filter-bar .grow  { flex: 1; min-width: 180px; }
.filter-bar input,
.filter-bar select { padding: .4rem .6rem; border: 1px solid #ccc;
                     border-radius: 4px; }
.filter-bar .count { margin-left: auto; align-self: center;
                     color: #555; font-size: .9rem; }

@media (max-width: 600px) {
  .filter-bar label { width: 100%; }
  .filter-bar .count { width: 100%; text-align: right; }
}
```

`disabled={!hasFilters}` — the button greys out when there's nothing to clear (subtle UX win).

</details>

---

### TICKET-F099: Transaction Count Badge
**File:** `frontend/src/components/Navbar.jsx` or `TransactionList.jsx`

**Description:** Show the total transaction count as a badge.

**What**
- A small coloured pill next to the "Transactions" nav link (or page heading) showing the live `txns.length`.

**Why**
- Gives users an at-a-glance sense of dataset size and silently confirms that fresh data has loaded after add/delete operations.

**Observe**
- The badge number matches the row count in the table; deleting a row decrements the badge on the next render without a page reload.

**Instructions:**
- Display the count next to the "Transactions" nav link or at the top of the list page
- Update dynamically as transactions are added/deleted

**Acceptance Criteria:**
- [ ] Count badge shows the current number of transactions
- [ ] Count updates after add or delete operations

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

The badge re-renders whenever the underlying data changes. Pass the count down from wherever the txns live, or have the Navbar pull it from a hook of its own. Style as a small coloured pill on the nav link.

</details>

<details>
<summary><b>Hint 2 — Two placement options</b></summary>

Option A — inside the TransactionList header:

```jsx
<h2>Transactions <span className="badge">{txns.length}</span></h2>
```

Option B — on the Navbar (Navbar also calls the hook so it has its own count):

```jsx
const { data: txns = [] } = useTransactionData();
<NavLink to="/transactions">Transactions <span className="badge">{txns.length}</span></NavLink>
```

Option B causes two GETs to `/api/transactions` (one for Navbar, one for the page) — fine for foundation, but in production you'd hoist the data into a context.

</details>

<details>
<summary><b>Hint 3 — Badge with CSS</b></summary>

```jsx
// src/components/Navbar.jsx
import { useTransactionData } from "../hooks/useBudgetAPI";

export default function Navbar() {
  const { data: txns = [] } = useTransactionData();

  return (
    <header className="app-header">
      <h1 className="app-title">SmartBudget</h1>
      <nav className="app-nav">
        <NavLink to="/" end>Dashboard</NavLink>
        <NavLink to="/transactions">
          Transactions <span className="badge">{txns.length}</span>
        </NavLink>
        <NavLink to="/add">Add</NavLink>
        <NavLink to="/goals">Goals</NavLink>
      </nav>
    </header>
  );
}
```

```css
.badge {
  display: inline-block;
  background: var(--db-gold);
  color: #000;
  font-size: .75rem;
  font-weight: 700;
  padding: .1rem .45rem;
  border-radius: 9999px;
  margin-left: .35rem;
  vertical-align: middle;
}
```

Better: lift the txns state into a `BudgetContext` provider so Navbar and TransactionList share one fetch. But that's Day-9 stretch territory; the duplicate hook call works.

</details>

---

### TICKET-F100: Monthly Summary Chart
**File:** `frontend/src/components/MonthlySummaryChart.jsx`

**Description:** Build a bar chart showing monthly income vs expenses using Recharts.

**What**
- A `MonthlySummaryChart` component that aggregates raw transactions by month and renders one green income bar and one red expense bar per month inside a Recharts `ResponsiveContainer`.

**Why**
- Tabular data hides trends; a side-by-side bar chart makes "am I spending more than I earn?" answerable in one glance.

**Observe**
- With 3 months of seed data the chart shows 3 month groups on the X-axis; hovering any bar shows a tooltip with the exact £ value; resizing the window rescales the chart smoothly.

**Instructions (follow the 2 TODOs in MonthlySummaryChart.jsx):**

**Step 1 -- Aggregate data:**
- Accept `transactions` as a prop
- Group transactions by month (use `txnDate.substring(0, 7)` to get "YYYY-MM")
- For each month, sum INCOME and EXPENSE amounts separately
- Build an array like: `[{ month: "2026-01", income: 3500, expense: 1200 }, ...]`

**Step 2 -- Render chart:**
- Import `BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer` from recharts
- Wrap in `<ResponsiveContainer width="100%" height={300}>`
- Use `<BarChart data={aggregatedData}>`
- Add two `<Bar>` elements: one for income (green), one for expense (red)

**Acceptance Criteria:**
- [ ] Chart renders on the Dashboard page
- [ ] Bars show income (green) and expense (red) per month
- [ ] Hovering shows a tooltip with exact values
- [ ] Legend shows which color is income and which is expense
- [ ] Chart is responsive (resizes with the container)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`npm install recharts`. Aggregate the transaction array into one row per month: `{ month: "2026-01", income: 3500, expense: 70 }`. Render with `<BarChart>` inside `<ResponsiveContainer>`. Two `<Bar>` elements stack-or-group your two series.

</details>

<details>
<summary><b>Hint 2 — Aggregation + chart</b></summary>

```jsx
import { useMemo } from "react";
import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend,
         ResponsiveContainer } from "recharts";

export default function MonthlySummaryChart({ transactions = [] }) {

  const data = useMemo(() => {
    const map = {};
    for (const t of transactions) {
      const month = (t.txnDate || "").substring(0, 7);     // "2026-01"
      if (!month) continue;
      if (!map[month]) map[month] = { month, income: 0, expense: 0 };
      const amt = Number(t.amount);
      if (t.type === "INCOME")  map[month].income   += amt;
      if (t.type === "EXPENSE") map[month].expense  += amt;
    }
    return Object.values(map).sort((a, b) => a.month.localeCompare(b.month));
  }, [transactions]);

  return (
    <ResponsiveContainer width="100%" height={300}>
      <BarChart data={data}>
        <XAxis dataKey="month" />
        <YAxis />
        <Tooltip />
        <Legend />
        <Bar dataKey="income"  fill="#2e7d32" name="Income" />
        <Bar dataKey="expense" fill="#c62828" name="Expense" />
      </BarChart>
    </ResponsiveContainer>
  );
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution + formatting tooltip</b></summary>

```bash
cd frontend
npm install recharts
```

```jsx
// src/components/MonthlySummaryChart.jsx
import { useMemo } from "react";
import {
  BarChart, Bar, XAxis, YAxis,
  Tooltip, Legend, ResponsiveContainer, CartesianGrid
} from "recharts";

export default function MonthlySummaryChart({ transactions = [] }) {
  const data = useMemo(() => {
    const map = {};
    for (const t of transactions) {
      const month = (t.txnDate ?? "").substring(0, 7);   // "2026-01"
      if (!month) continue;
      if (!map[month]) map[month] = { month, income: 0, expense: 0 };
      const amt = Number(t.amount) || 0;
      if (t.type === "INCOME")  map[month].income   += amt;
      if (t.type === "EXPENSE") map[month].expense  += amt;
    }
    return Object.values(map)
      .sort((a, b) => a.month.localeCompare(b.month))
      .map(d => ({
        ...d,
        income:  Math.round(d.income  * 100) / 100,
        expense: Math.round(d.expense * 100) / 100,
      }));
  }, [transactions]);

  if (data.length === 0) {
    return <p style={{ color: "#666" }}>No data to chart yet.</p>;
  }

  return (
    <ResponsiveContainer width="100%" height={320}>
      <BarChart data={data} margin={{ top: 16, right: 24, left: 0, bottom: 0 }}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="month" />
        <YAxis tickFormatter={v => "£" + v} />
        <Tooltip
          formatter={(value, key) =>
            ["£" + Number(value).toFixed(2), key]}
        />
        <Legend />
        <Bar dataKey="income"  fill="#2e7d32" name="Income" />
        <Bar dataKey="expense" fill="#c62828" name="Expense" />
      </BarChart>
    </ResponsiveContainer>
  );
}
```

`ResponsiveContainer` listens to its parent's width and resizes the chart on window resize. `Tooltip.formatter` controls the on-hover values. The aggregation runs once per render via `useMemo` — only recomputes when `transactions` changes.

Test: open Dashboard with the F005 seed (3 months of data) → chart shows 3 month groups, hover any bar to see exact £ value, resize the window → bars rescale.

</details>

---

### TICKET-F101: Import Chart into Dashboard
**File:** `frontend/src/pages/Dashboard.jsx`

**Description:** Replace the chart placeholder with the real MonthlySummaryChart.

**What**
- The dashed placeholder box on `Dashboard.jsx` swapped out for `<MonthlySummaryChart transactions={txns} />` fed from the existing `useTransactionData` hook.

**Why**
- F100 built the chart in isolation; the Dashboard is where users actually look — until you wire it in, the chart isn't visible to anyone.

**Observe**
- Opening `/` shows the summary cards on top and the real bar chart below — no dashed placeholder, no layout shift, no extra fetch (Dashboard already calls the hook).

**Instructions:**
1. Import `MonthlySummaryChart` component
2. Replace the dashed placeholder box with `<MonthlySummaryChart transactions={transactions} />`

**Acceptance Criteria:**
- [ ] Chart appears on the Dashboard where the placeholder was
- [ ] Chart shows data from the real transactions
- [ ] Dashboard layout is not broken by the chart

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Import the chart in `Dashboard.jsx`. Pass `transactions` (from your `useTransactionData` hook) as a prop. Delete the dashed placeholder. Done.

</details>

<details>
<summary><b>Hint 2 — Dashboard integration</b></summary>

```jsx
import MonthlySummaryChart from "../components/MonthlySummaryChart";
import { useTransactionData } from "../hooks/useBudgetAPI";

export default function Dashboard() {
  const { data: txns = [], loading, error } = useTransactionData();

  if (loading) return <Spinner />;
  if (error)   return <ErrorBanner message={error.message} />;

  return (
    <main className="page">
      <h2>Dashboard</h2>
      {/* ... summary cards ... */}
      <section style={{ background: "#fff", padding: "1rem",
                        borderRadius: 8, marginTop: "2rem",
                        boxShadow: "0 1px 4px rgba(0,0,0,.08)" }}>
        <h3>Monthly Income vs Expenses</h3>
        <MonthlySummaryChart transactions={txns} />
      </section>
    </main>
  );
}
```

</details>

<details>
<summary><b>Hint 3 — Full Dashboard with chart</b></summary>

```jsx
// src/pages/Dashboard.jsx
import { useMemo } from "react";
import MonthlySummaryChart from "../components/MonthlySummaryChart";
import Spinner             from "../components/Spinner";
import ErrorBanner         from "../components/ErrorBanner";
import { useTransactionData } from "../hooks/useBudgetAPI";

export default function Dashboard() {
  const { data: txns = [], loading, error } = useTransactionData();

  const totals = useMemo(() => {
    let income = 0, expenses = 0;
    for (const t of txns) {
      const amt = Number(t.amount) || 0;
      if (t.type === "INCOME")  income   += amt;
      if (t.type === "EXPENSE") expenses += amt;
    }
    return { income, expenses, net: income - expenses };
  }, [txns]);

  if (loading) return <Spinner />;
  if (error)   return <ErrorBanner message={error.message} />;

  const fmt = n => "£" + Number(n).toFixed(2);

  return (
    <main className="page">
      <h2>Dashboard</h2>

      <section className="cards">
        <article className="card"><h3>Total Income</h3>
          <p className="card-value income">{fmt(totals.income)}</p></article>
        <article className="card"><h3>Total Expenses</h3>
          <p className="card-value expense">{fmt(totals.expenses)}</p></article>
        <article className="card"><h3>Net Balance</h3>
          <p className={"card-value " + (totals.net < 0 ? "expense" : "")}>
            {fmt(totals.net)}</p></article>
      </section>

      <section className="chart-card"
               style={{ background: "#fff", padding: "1.25rem",
                        borderRadius: 8, marginTop: "2rem",
                        boxShadow: "0 1px 4px rgba(0,0,0,.08)" }}>
        <h3 style={{ marginTop: 0 }}>Monthly Income vs Expenses</h3>
        <MonthlySummaryChart transactions={txns} />
      </section>
    </main>
  );
}
```

Open Dashboard — three cards on top, chart below with bars per month, no layout breakage.

</details>

---

### TICKET-F102: Edit Transaction
**File:** `frontend/src/pages/TransactionList.jsx` + controller

**Description:** Add the ability to edit an existing transaction.

**What**
- An inline-edit row driven by `editingId` state, plus a `PUT /api/transactions/{id}` endpoint and `service.update(...)` on the backend that mutate the row in place.

**Why**
- Until now the only fix for a wrong entry was delete-and-recreate (losing the original `txnId` and audit trail); real users need to correct typos without losing the row identity.

**Observe**
- Clicking Edit on a row swaps its cells for inputs pre-filled with current values; Save fires PUT, closes the editor, and the row re-renders with the new values from the refetch; Cancel reverts without any HTTP call.

**Instructions:**
1. Add an "Edit" button to each table row
2. Clicking Edit opens an inline edit form (or navigates to a pre-filled form)
3. The form is pre-populated with the existing transaction data
4. On submit, call `PUT /api/transactions/{id}` with updated data
5. Refresh the list after successful edit

**Backend:** Implement the PUT endpoint in TransactionController (TICKET-F102 TODO is already there)

**Acceptance Criteria:**
- [ ] Each row has an Edit button
- [ ] Clicking Edit shows a form with current values pre-filled
- [ ] Submitting the form updates the transaction in the database
- [ ] The list refreshes to show the updated data
- [ ] Cancel returns to the normal view without changes

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Use `editingId` state — when it equals a row's `txnId`, render that row as inputs instead of cells. Save fires `PUT /api/transactions/{id}` and triggers a refetch. Backend: add `@PutMapping("/{id}")` to `TransactionController` that delegates to `service.update(...)`.

</details>

<details>
<summary><b>Hint 2 — Backend PUT + frontend inline edit</b></summary>

Backend (`TransactionController.java`):

```java
@PutMapping("/{id}")
public Transaction update(@PathVariable Long id,
                          @RequestBody Transaction t) {
    return service.update(id, t.getAmount(), t.getTxnDate(),
                          t.getDescription(), t.getType());
}
```

Frontend (`TransactionList.jsx`):

```jsx
const [editingId,   setEditingId]   = useState(null);
const [editValues,  setEditValues]  = useState({});

function startEdit(t) {
  setEditingId(t.txnId);
  setEditValues({ amount: t.amount, description: t.description, type: t.type });
}
async function saveEdit(id) {
  await fetch(`/api/transactions/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(editValues),
  });
  setEditingId(null);
  refetch();         // need to add refetch to the hook (or hard reload)
}

// In the row:
editingId === t.txnId
  ? <td><input value={editValues.amount}
               onChange={e => setEditValues(v => ({...v, amount: e.target.value}))}/></td>
  : <td>£{Number(t.amount).toFixed(2)}</td>
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

**Backend** — add to `TransactionService.java` (already in F063):

```java
@Transactional
public Transaction update(Long id, BigDecimal amount, LocalDate date,
                          String description, String type) {
    Transaction t = txnRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Transaction " + id));
    if (amount != null)      { /* validate */ t.setAmount(amount); }
    if (date != null)        t.setTxnDate(date);
    if (description != null) t.setDescription(description);
    if (type != null)        t.setType(type);
    return txnRepo.save(t);
}
```

`TransactionController.java`:

```java
@PutMapping("/{id}")
public Transaction update(@PathVariable Long id,
                          @RequestBody Transaction body) {
    return service.update(id, body.getAmount(), body.getTxnDate(),
                          body.getDescription(), body.getType());
}
```

**Frontend** — give the hook a `refetch`:

```jsx
// src/hooks/useBudgetAPI.js — extend useFetch
function useFetch(url) {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState(null);
  const [version, setVersion] = useState(0);     // bump to refetch

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    fetch(url)
      .then(r => r.json())
      .then(d => { if (!cancelled) setData(d); })
      .catch(e => { if (!cancelled) setError(e); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [url, version]);

  return { data, loading, error, refetch: () => setVersion(v => v + 1) };
}
```

Inline-edit row (`TransactionList.jsx`):

```jsx
const { data: txns = [], refetch } = useTransactionData();
const [editingId,  setEditingId]  = useState(null);
const [editValues, setEditValues] = useState({});

function startEdit(t) {
  setEditingId(t.txnId);
  setEditValues({
    amount:      String(t.amount),
    description: t.description ?? "",
    type:        t.type,
  });
}

async function saveEdit(t) {
  try {
    const res = await fetch(`/api/transactions/${t.txnId}`, {
      method:  "PUT",
      headers: { "Content-Type": "application/json" },
      body:    JSON.stringify({
        amount:      parseFloat(editValues.amount),
        txnDate:     t.txnDate,
        description: editValues.description,
        type:        editValues.type,
      }),
    });
    if (!res.ok) throw new Error("HTTP " + res.status);
    setEditingId(null);
    refetch();
  } catch (err) {
    alert("Update failed: " + err.message);
  }
}

// In the table row map:
{txns.map(t => editingId === t.txnId ? (
  <tr key={t.txnId}>
    <td>{t.txnDate}</td>
    <td><input value={editValues.description}
               onChange={e => setEditValues(v => ({...v, description: e.target.value}))}/></td>
    <td>{t.category?.name}</td>
    <td>
      <select value={editValues.type}
              onChange={e => setEditValues(v => ({...v, type: e.target.value}))}>
        <option>INCOME</option><option>EXPENSE</option>
      </select>
    </td>
    <td><input type="number" step="0.01" value={editValues.amount}
               onChange={e => setEditValues(v => ({...v, amount: e.target.value}))}/></td>
    <td>
      <button onClick={() => saveEdit(t)}>Save</button>
      <button onClick={() => setEditingId(null)}>Cancel</button>
    </td>
  </tr>
) : (
  <tr key={t.txnId}>
    <td>{t.txnDate}</td><td>{t.description}</td><td>{t.category?.name}</td>
    <td>{t.type}</td>
    <td className={t.type === "INCOME" ? "amount income" : "amount expense"}>
      £{Number(t.amount).toFixed(2)}
    </td>
    <td>
      <button onClick={() => startEdit(t)}>Edit</button>
      <button onClick={() => onDelete(t.txnId)}>Delete</button>
    </td>
  </tr>
))}
```

Cancel closes editing without calling the API; Save fires PUT then refetches so the row updates to reflect the persisted values.

</details>

---

### TICKET-F103: Contribute to Savings Goal
**File:** `frontend/src/pages/SavingsGoals.jsx`

**Description:** Add a "Contribute" button to each savings goal.

**What**
- A mini-form on each `GoalCard` that posts `PUT /api/goals/{id}/contribute` with a positive amount and refetches the goals list so the progress bar repaints.

**Why**
- The goals page so far is read-only — users can see targets but can't move the needle; this ticket turns the page from a report into a tool.

**Observe**
- Entering £100 against a £200/£1000 goal then clicking Contribute pushes the bar to £300/£1000 (30%); over-contributing surfaces the backend's 400 error in red under the form; zero or negative amounts are blocked client-side before any HTTP call.

**Instructions:**
1. Add a Contribute button on each goal card
2. Clicking it shows an input for the contribution amount
3. On submit, call `PUT /api/goals/{id}/contribute` with the amount
4. Progress bar should update after contribution
5. Use the `contribute` function from `useSavingsGoals` hook

**Acceptance Criteria:**
- [ ] Contribute button appears on each goal card
- [ ] User can enter an amount and submit
- [ ] Progress bar updates to reflect the new amount
- [ ] Over-contributing (exceeding target) shows an error
- [ ] Zero or negative contributions are rejected

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Add a small `<ContributeForm>` per goal card with one number input + a button. On submit, `PUT /api/goals/{id}/contribute` body `{amount}`, then refetch the goals. The backend validation from F062 already handles the negative/over-target cases — surface that error in the UI.

</details>

<details>
<summary><b>Hint 2 — Per-card mini-form</b></summary>

```jsx
function GoalCard({ goal, onContribute }) {
  const [amount, setAmount] = useState("");
  const [busy,   setBusy]   = useState(false);
  const [err,    setErr]    = useState(null);

  async function submit(e) {
    e.preventDefault();
    setBusy(true); setErr(null);
    try {
      await onContribute(goal.goalId, parseFloat(amount));
      setAmount("");
    } catch (e) { setErr(e.message); }
    finally     { setBusy(false); }
  }
  // ... render card + form
}
```

In the page:

```jsx
const { data: goals = [], refetch } = useSavingsGoals();

async function contribute(id, amount) {
  const res = await fetch(`/api/goals/${id}/contribute`, {
    method:  "PUT",
    headers: { "Content-Type": "application/json" },
    body:    JSON.stringify({ amount }),
  });
  if (!res.ok) {
    const body = await res.json().catch(() => null);
    throw new Error(body?.message || `HTTP ${res.status}`);
  }
  refetch();
}

return goals.map(g => <GoalCard key={g.goalId} goal={g} onContribute={contribute} />);
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```jsx
// src/pages/SavingsGoals.jsx
import { useState } from "react";
import { useSavingsGoals } from "../hooks/useBudgetAPI";
import Spinner             from "../components/Spinner";
import ErrorBanner         from "../components/ErrorBanner";

export default function SavingsGoals() {
  const { data: goals = [], loading, error, refetch } = useSavingsGoals();

  async function contribute(id, amount) {
    const res = await fetch(`/api/goals/${id}/contribute`, {
      method:  "PUT",
      headers: { "Content-Type": "application/json" },
      body:    JSON.stringify({ amount }),
    });
    if (!res.ok) {
      const body = await res.json().catch(() => null);
      throw new Error(body?.message || `HTTP ${res.status}`);
    }
    refetch();
  }

  if (loading) return <Spinner />;
  if (error)   return <ErrorBanner message={error.message} />;

  return (
    <main className="page">
      <h2>Savings Goals</h2>
      {goals.map(g =>
        <GoalCard key={g.goalId} goal={g} onContribute={contribute} />)}
    </main>
  );
}

function GoalCard({ goal, onContribute }) {
  const target  = Number(goal.targetAmount);
  const current = Number(goal.currentAmount);
  const pct     = Math.min(100, Math.max(0, target ? current / target * 100 : 0));
  const colour  = pct < 33 ? "#c62828" : pct < 66 ? "#f9a825" : "#2e7d32";

  const [amount, setAmount] = useState("");
  const [busy,   setBusy]   = useState(false);
  const [err,    setErr]    = useState(null);

  async function submit(e) {
    e.preventDefault();
    const value = parseFloat(amount);
    if (!Number.isFinite(value) || value <= 0) {
      setErr("Enter a positive amount");
      return;
    }
    setBusy(true); setErr(null);
    try {
      await onContribute(goal.goalId, value);
      setAmount("");
    } catch (e) {
      setErr(e.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <article style={{ background: "#fff", padding: "1rem", margin: "1rem 0",
                      borderRadius: 8, boxShadow: "0 1px 4px rgba(0,0,0,.08)" }}>
      <h3 style={{ margin: 0 }}>{goal.goalName}</h3>
      <p style={{ margin: ".5rem 0" }}>
        £{current.toFixed(2)} of £{target.toFixed(2)} ({pct.toFixed(0)}%)
      </p>
      <div style={{ background: "#eee", height: 12, borderRadius: 6, overflow: "hidden" }}>
        <div style={{ width: `${pct}%`, height: "100%", background: colour,
                      transition: "width .4s ease" }} />
      </div>

      <form onSubmit={submit} style={{ display: "flex", gap: ".5rem", marginTop: "1rem" }}>
        <input type="number" step="0.01" min="0.01"
               placeholder="Contribute…"
               value={amount} onChange={e => setAmount(e.target.value)}
               disabled={busy}
               style={{ flex: 1, padding: ".4rem .6rem",
                        border: "1px solid #ccc", borderRadius: 4 }} />
        <button type="submit" disabled={busy || !amount}
                style={{ background: "#003366", color: "#fff", border: 0,
                         borderRadius: 4, padding: ".4rem 1rem", cursor: "pointer" }}>
          {busy ? "..." : "Contribute"}
        </button>
      </form>

      {err && <p style={{ color: "#c62828", marginTop: ".5rem" }}>{err}</p>}
    </article>
  );
}
```

Test the over-target path: a goal at £1500/£1500. Try to contribute £100 — backend returns 400 "Contribution exceeds target by 100" → red error appears under the form. Test the zero case: enter 0 → client-side message catches it before any HTTP request fires.

</details>

---

### TICKET-F104: Toast Notifications
**File:** `frontend/src/pages/*.jsx` + `frontend/src/components/Feedback.jsx`

**Description:** Add toast notifications for success/error feedback.

**What**
- A `<Toast>` component (green for success, red for error) driven by a `useState({type, message})` that auto-dismisses after 3 seconds and is fired from every create/edit/delete/contribute handler.

**Why**
- Silent success leaves users unsure whether anything happened; an `alert()` is jarring and modal. Toast is the standard middle ground — confirms the action without blocking the UI.

**Observe**
- Deleting a row pops a green "Deleted" toast top-right that slides in, holds ~3s, then slides out; a failed PUT pops a red toast with the server error message instead.

**Instructions:**
1. Use the `Toast` component from Feedback.jsx
2. Show success toast after: creating a transaction, deleting, editing, contributing
3. Show error toast when API calls fail
4. Toast should auto-dismiss after 3 seconds

**Acceptance Criteria:**
- [ ] Green toast appears for successful operations
- [ ] Red toast appears for errors
- [ ] Toast auto-dismisses after 3 seconds
- [ ] Multiple toasts stack (or replace the previous one)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

A toast = `useState({type, message})`, set on success/error, cleared by a `setTimeout` after 3s. Render a fixed-position div somewhere top-right. Use the existing `<Toast>` component from `Feedback.jsx` if it's already there, or write a tiny one.

</details>

<details>
<summary><b>Hint 2 — useToast hook + component</b></summary>

```jsx
// src/components/Toast.jsx
import { useEffect } from "react";
import "./Toast.css";

export default function Toast({ toast, onDone }) {
  useEffect(() => {
    if (!toast) return;
    const id = setTimeout(onDone, 3000);
    return () => clearTimeout(id);
  }, [toast, onDone]);

  if (!toast) return null;
  return <div className={`toast toast--${toast.type}`}>{toast.message}</div>;
}
```

```css
.toast { position: fixed; top: 1rem; right: 1rem;
         padding: .75rem 1.25rem; border-radius: 6px;
         color: #fff; box-shadow: 0 4px 14px rgba(0,0,0,.15);
         z-index: 1000; animation: slideIn .3s ease; }
.toast--success { background: #2e7d32; }
.toast--error   { background: #c62828; }
@keyframes slideIn { from { transform: translateX(110%); } to { transform: none; } }
```

In each page:

```jsx
const [toast, setToast] = useState(null);

async function handleDelete(id) {
  try { await api.delete(id); setToast({ type:"success", message:"Deleted" }); }
  catch (e) { setToast({ type:"error", message: e.message }); }
}

return <>
  <Toast toast={toast} onDone={() => setToast(null)} />
  {/* ... rest of UI */}
</>;
```

</details>

<details>
<summary><b>Hint 3 — Global toast via context</b></summary>

For tidiness, lift Toast state into a global context so any component can fire one without prop-drilling:

```jsx
// src/contexts/ToastContext.jsx
import { createContext, useCallback, useContext, useState } from "react";
import Toast from "../components/Toast";

const Ctx = createContext(() => {});

export function ToastProvider({ children }) {
  const [toast, setToast] = useState(null);

  const show = useCallback((type, message) => {
    setToast({ type, message, id: Date.now() });
  }, []);

  return (
    <Ctx.Provider value={show}>
      {children}
      <Toast toast={toast} onDone={() => setToast(null)} />
    </Ctx.Provider>
  );
}

export const useToast = () => useContext(Ctx);
```

Wrap `<App />`:

```jsx
// main.jsx
import { ToastProvider } from "./contexts/ToastContext";

ReactDOM.createRoot(document.getElementById("root")).render(
  <ToastProvider>
    <App />
  </ToastProvider>
);
```

Any component:

```jsx
import { useToast } from "../contexts/ToastContext";
const toast = useToast();

async function handleSave() {
  try { await saveSomething(); toast("success", "Saved!"); }
  catch (e) { toast("error", e.message); }
}
```

Replace-on-second-toast is the default (one piece of state). For a stacking queue, use `setToasts(arr => [...arr, newToast])` and render `arr.map(...)` instead.

</details>

---

### TICKET-F105: Empty States
**File:** All pages

**Description:** Show friendly messages when there is no data.

**What**
- A reusable `<EmptyState>` component (title, body, optional CTA link) rendered on every page when its list is empty, including the filtered-to-nothing case on TransactionList.

**Why**
- A blank page reads as "broken" or "still loading"; a clear "no data yet — here's the next step" message turns the empty state into onboarding instead.

**Observe**
- A fresh database loads the Dashboard with a centred card reading "Welcome to SmartBudget — Add your first transaction" plus an "+ Add Transaction" button; setting filters that match no rows on TransactionList shows a "No matches" card instead of an empty table.

**Instructions:**
- TransactionList with no transactions: "No transactions yet. Add your first one!"
- SavingsGoals with no goals: "No savings goals found."
- Dashboard with no data: "Start by adding some transactions."
- Filtered results with no matches: "No transactions match your filters."

**Acceptance Criteria:**
- [ ] Each page has a meaningful empty state message
- [ ] Empty states are styled (not just plain text)
- [ ] Empty states include a call-to-action where appropriate

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Branch right before rendering the data UI: `if (data.length === 0) return <EmptyState ... />;`. Style the empty state as a centred card with an icon-or-emoji-or-nothing, friendly text, and a `<Link>` button when there's a sensible next step (e.g., "Add your first transaction").

</details>

<details>
<summary><b>Hint 2 — Reusable EmptyState</b></summary>

```jsx
// src/components/EmptyState.jsx
import { Link } from "react-router-dom";

export default function EmptyState({ title, body, ctaLabel, ctaTo }) {
  return (
    <div style={style.box}>
      <h3 style={{ margin: 0 }}>{title}</h3>
      <p style={{ color: "#666" }}>{body}</p>
      {ctaTo && (
        <Link to={ctaTo} style={style.btn}>{ctaLabel}</Link>
      )}
    </div>
  );
}
const style = {
  box: { background: "#fff", padding: "2rem", borderRadius: 8,
         textAlign: "center", boxShadow: "0 1px 4px rgba(0,0,0,.08)",
         maxWidth: 480, margin: "2rem auto" },
  btn: { display: "inline-block", marginTop: "1rem",
         background: "#003366", color: "#fff", padding: ".5rem 1rem",
         borderRadius: 4, textDecoration: "none" },
};
```

</details>

<details>
<summary><b>Hint 3 — Use across pages</b></summary>

```jsx
// TransactionList — no data at all
if (txns.length === 0) return (
  <EmptyState
    title="No transactions yet"
    body="Start tracking your money — add your first transaction."
    ctaLabel="+ Add Transaction"
    ctaTo="/add" />
);

// TransactionList — filtered to nothing
if (filtered.length === 0) return (
  <EmptyState
    title="No matches"
    body={`No transactions match your current filters.`}
    ctaLabel="Clear filters"
    ctaTo="#"
  />   // wire onClick to clearFilters instead of using a Link in this case
);

// SavingsGoals — no goals
if (goals.length === 0) return (
  <EmptyState
    title="No savings goals"
    body="Set a goal and start saving towards it."
  />
);

// Dashboard — no data at all
if (txns.length === 0) return (
  <EmptyState
    title="Welcome to SmartBudget"
    body="Add your first transaction to see income, expenses, and trends."
    ctaLabel="+ Add Transaction"
    ctaTo="/add" />
);
```

Why empty states matter: a blank page makes users wonder if the app is broken. A clear message ("no data yet — here's what to do") flips it into onboarding.

</details>

---

### TICKET-F106: Number Formatting
**File:** All pages

**Description:** Format currency values consistently across the app.

**What**
- A `formatCurrency(amount)` helper in `src/utils/format.js` built on `Intl.NumberFormat("en-GB", {style:"currency", currency:"GBP"})`, called from every place that renders money.

**Why**
- Hand-rolled `"£" + n.toFixed(2)` drifts (no thousands separators, inconsistent symbol, NaN leaks); one helper guarantees the same output everywhere and makes a future currency swap a one-file change.

**Observe**
- Dashboard, table rows, and goal cards all render amounts as `£3,500.00` (two decimals, comma thousands separator, leading £); passing `null`/`undefined`/`NaN` to the helper returns `£0.00` instead of `£NaN`.

**Instructions:**
- Create a utility function: `formatCurrency(amount)` that returns formatted string
- Use `Intl.NumberFormat` or `toFixed(2)` with currency symbol
- Apply to all monetary values: dashboard stats, table amounts, goal amounts

**Acceptance Criteria:**
- [ ] All amounts show 2 decimal places
- [ ] Currency symbol is consistent (GBP or EUR)
- [ ] Negative values are handled properly
- [ ] Large numbers include thousand separators (optional)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`Intl.NumberFormat` is the right tool — it handles currency symbols, locale separators, and decimal places in one call. Put it in `src/utils/format.js` and import wherever you display money.

</details>

<details>
<summary><b>Hint 2 — Helper</b></summary>

```js
// src/utils/format.js
const moneyFmt = new Intl.NumberFormat("en-GB", {
  style:    "currency",
  currency: "GBP",
});

export function formatCurrency(amount) {
  if (amount == null || isNaN(amount)) return moneyFmt.format(0);
  return moneyFmt.format(Number(amount));
}
```

Usage:
```jsx
import { formatCurrency } from "../utils/format";

<p>{formatCurrency(transaction.amount)}</p>
//   £3,500.00      ← thousands separator, 2 dp, currency symbol
```

</details>

<details>
<summary><b>Hint 3 — Full util + integration</b></summary>

```js
// src/utils/format.js
const GBP = new Intl.NumberFormat("en-GB", {
  style:    "currency",
  currency: "GBP",
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
});

export function formatCurrency(amount) {
  const n = Number(amount);
  if (!Number.isFinite(n)) return GBP.format(0);
  return GBP.format(n);
}

/** Returns "+£100.00" or "-£100.00" with sign. */
export function formatSignedCurrency(amount) {
  const n = Number(amount);
  if (!Number.isFinite(n)) return GBP.format(0);
  return (n >= 0 ? "+" : "") + GBP.format(n);
}

const DATE_FMT = new Intl.DateTimeFormat("en-GB", {
  day:   "2-digit",
  month: "short",
  year:  "numeric",
});
export function formatDate(iso) {
  if (!iso) return "";
  return DATE_FMT.format(new Date(iso));   // "01 Jan 2026"
}
```

Apply everywhere:

```jsx
import { formatCurrency, formatDate } from "../utils/format";

// Dashboard cards
<p className="card-value income">{formatCurrency(totals.income)}</p>
// → £3,500.00

// TransactionRow
<td>{formatDate(t.txnDate)}</td>          // "01 Jan 2026"
<td className="amount">
  {formatCurrency(t.amount)}              // £45.20
</td>

// SavingsGoals
<p>{formatCurrency(current)} of {formatCurrency(target)}</p>
```

`Intl.NumberFormat` is built into every modern browser, handles every locale + currency, and is faster than rolling your own. To switch currencies later, you only edit one file.

</details>

---

### TICKET-F107: Accessibility & Polish
**File:** All pages

**Description:** Final UI/UX improvements.

**What**
- `aria-label`s on icon-only buttons and unlabelled inputs, visible focus rings via `:focus-visible`, a skip-link, and `role="status"`/`role="alert"` regions for toasts and form errors.

**Why**
- Keyboard-only and screen-reader users currently can't navigate the app reliably; these changes also lift the Lighthouse Accessibility score and surface contrast bugs before a real audit finds them.

**Observe**
- Tab from the URL bar moves focus through Navbar → page inputs → buttons in reading order with a visible outline on each; Enter activates the focused control; Chrome Lighthouse Accessibility audit scores 90+.

**Instructions:**
1. Add `aria-label` attributes to buttons and form inputs
2. Ensure keyboard navigation works (tab through inputs)
3. Add `title` attributes to buttons
4. Verify color contrast meets WCAG AA standards
5. Check all pages on mobile viewport

**Acceptance Criteria:**
- [ ] All interactive elements have aria-labels
- [ ] Tab key moves focus through all inputs and buttons
- [ ] The app is usable without a mouse
- [ ] No color contrast issues (test with browser dev tools)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Three concrete a11y wins: (1) every `<button>` that contains only an icon gets `aria-label`. (2) every input has a visible `<label>` (or `aria-labelledby` if labels are visual but separate). (3) keyboard test — tab through the whole app, hit Enter on every button, can you do everything? Run the Chrome **Lighthouse** Accessibility audit for an automatic pass.

</details>

<details>
<summary><b>Hint 2 — Common fixes</b></summary>

```jsx
// Icon-only buttons need labels
<button aria-label="Delete transaction" onClick={...}>🗑</button>

// Inputs without text labels
<input type="search" aria-label="Search transactions" />

// Decorative icons should be hidden from screen readers
<span aria-hidden="true">›</span>

// Loading region
<div role="status" aria-live="polite">Loading...</div>

// Error region
<div role="alert">Something went wrong</div>
```

For keyboard nav: make sure `:focus-visible` is styled (you already did in Day 7 F076 — don't `outline: none` anywhere). Test by clicking the URL bar then pressing Tab — focus should move through Navbar → page inputs → buttons in a sensible order.

</details>

<details>
<summary><b>Hint 3 — Full audit checklist</b></summary>

```jsx
// Navbar
<NavLink to="/transactions" aria-current="page">Transactions</NavLink>

// AddTransactionForm — every input has a label, errors are announced
<label htmlFor="amount">Amount (£)</label>
<input id="amount" name="amount"
       aria-invalid={!!errors.amount}
       aria-describedby={errors.amount ? "amount-error" : undefined}
       ... />
{errors.amount && (
  <span id="amount-error" role="alert" className="error">
    {errors.amount}
  </span>
)}

// TransactionRow — icon-only button
<button aria-label={`Delete transaction ${txn.txnId}`} onClick={...}>×</button>

// Filter bar
<input type="search" aria-label="Search by description" ... />

// Toast — already in F104
<div role="status" aria-live="polite" className="toast toast--success">Saved!</div>

// Skip-link at the top of <body>
<a href="#main-content" className="skip-link">Skip to main content</a>
<main id="main-content"> ... </main>
```

```css
/* Make sure focus is always visible */
:focus-visible {
  outline: 2px solid #003366;
  outline-offset: 2px;
}

/* Skip link — hidden until focused */
.skip-link {
  position: absolute; left: 0; top: -40px;
  background: #003366; color: #fff;
  padding: .5rem 1rem; text-decoration: none;
  transition: top .2s;
}
.skip-link:focus { top: 0; }
```

**Auto-audit:**
1. Chrome DevTools → Lighthouse tab → Accessibility → Analyse.
2. Target 90+ score.
3. Fix any contrast warnings — usually means a `#666` on `#fff` background drops below 4.5:1; bump to `#555` or `#444`.

**Manual keyboard test:**
- Click address bar, then Tab repeatedly. Can you reach every link, input, and button?
- On a button, can you Enter / Space to activate it?
- On the form, can you fill and submit using only the keyboard?
- Are focused elements visibly different from un-focused ones?

If yes to all four — the app is keyboard-usable.

</details>

---

## End-of-Day Checklist

- [ ] 3 filters working together on TransactionList (type, date, search)
- [ ] Monthly summary chart rendered on Dashboard with Recharts
- [ ] Edit transaction functionality works
- [ ] Contribute to savings goal works with progress bar update
- [ ] Toast notifications for all operations
- [ ] Empty states on all pages
- [ ] Consistent number formatting
- [ ] You can explain: client-side filtering, Recharts, controlled editing, toast pattern

---

*Tomorrow (Day 10): You will containerize the app with Docker and set up CI/CD with GitHub Actions.*
