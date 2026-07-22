# Day 8 -- JavaScript & React Module 1 (JS + React Foundations)

> TICKET-F077 through TICKET-F094 (Sprint 7)

---

## Overview

Today is a two-part day:

1. **Morning -- JavaScript theory + interactivity on yesterday's pages.** Syntax, data types, control flow, functions, scope, arrays, objects, the DOM, event handling. Then you apply it to yesterday's HTML/CSS pages: form validation, fetch GET/POST, delete with confirmation, loading spinner (F077-F081).
2. **Afternoon -- React Module 1.** Components, JSX, props, styling, lifecycle. Then you switch to the SmartBudget project and fill in the React frontend that already exists in skeleton form (F082-F094).

By the end of Day 8, the static `frontend-static/` pages are fully interactive AND the React frontend renders real data from the backend (no more mock arrays). You can explain hooks, props, and JSX without looking at notes.

---

## Session Plan

### AM -- JavaScript Module: Interactivity

**JS Basics**
- Syntax, data types (string, number, boolean, null, undefined)

**Control Flow & Loops**
- `if`/`else`, `switch`
- `for`, `while`, `for...of`

**Functions & Scope**
- Function declarations, arrow functions
- Block scope vs function scope, hoisting

  Lab: **JavaScript Functions Lab -- Rock Paper Scissors** (1h)

**Arrays & Iterators**
- Arrays, `.map`, `.filter`, `.reduce`, `.forEach`

  Lab: **JavaScript Arrays Lab -- Secret Message** (1h)

**Objects**
- Object literals, dot vs bracket access, getters/setters

**DOM & Event Handling**
- `document.querySelector`, `addEventListener`
- Intro to React (why the DOM gets old fast)

### PM -- React Module 1: Foundations

**JSX & First Components**
- Component architecture, the Virtual DOM
- React project setup (Vite)
- First component with JSX

  Lab: **React JSX Lab -- Animal Fun Facts** (1h)

**Component Interactions & Props**
- Props, parent → child data flow
- Multiple components composing together

  Lab: **React Components Interacting Lab -- CodeyOverflow Forum** (1h)

**Styling & Lifecycle**
- Styling React components (CSS modules, inline styles, className)
- Component lifecycle basics

  Lab: **React Styles Lab -- Styling Rock, Paper, Scissors** (1h)

**Build Practice**

  Lab: **React Lab -- Appointment Planner** (1h)

Break -- 20 min

---

## Sprint 7 -- AM: Vanilla JS on Static Pages (F077-F081)

Now that you understand JavaScript, wire up yesterday's static pages. Create `frontend-static/app.js` and add it as a `<script defer>` tag in all 4 HTML files (if not already linked).

> **Backend must be running**: in another terminal, `cd backend && ./mvnw spring-boot:run`.

## Sprint 7 -- PM: React Frontend (F082-F094)

The React app already exists in `frontend/` as a skeleton with mock data. The afternoon Sprint 7 work is to:
1. Verify the project boots
2. Build / fill in each component
3. Replace mock data with real fetches to the Spring Boot backend
4. Polish with error states, loading states, and styling

> **Setup**: `cd frontend && npm install && npm run dev` -- the app should open on `http://localhost:5173` with yellow TODO banners on each page. Those banners disappear as you complete tickets.

---

## Tickets

### TICKET-F077: Write `app.js` -- Form validation
**File:** `frontend-static/app.js`

**Description:** Add client-side validation to yesterday's Add Transaction form.

**What**
- A submit listener on `#add-form` that calls `e.preventDefault()`, checks amount > 0, description non-empty, and date not in the future, writing failures to `#form-message`.

**Why**
- Catching obvious bad input in the browser saves a backend round-trip and gives the user instant feedback instead of a 400 response after the network delay.

**Observe**
- Click Submit with the form empty → page does not reload and "Amount must be greater than 0." appears in red inside `#form-message`.

**Instructions:**
1. Use `document.getElementById("add-form")` to grab the form. Guard with `if (form)` so this code only runs on `add-transaction.html`.
2. On `submit`:
   - `e.preventDefault()`
   - Read each field's value with `document.getElementById("amount").value` (etc.)
   - Validate: amount > 0, description not empty, date not in future
   - On failure: write error to `#form-message` and `return`
   - On success: continue to F079's submit logic
3. Show errors in red text inside `#form-message`

**Acceptance Criteria:**
- [ ] Submitting an empty form shows an error message
- [ ] Submitting amount = 0 shows an error
- [ ] Future-dated transactions show an error
- [ ] Page does NOT reload on submit (preventDefault works)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Grab the form with `getElementById("add-form")`. Wrap in `if (form)` so this code does nothing on pages without the form. Inside the submit listener: `e.preventDefault()` first, then read each field's value, run the checks, write any error message into `#form-message`, and `return` if invalid.

</details>

<details>
<summary><b>Hint 2 — Listener + 3 validations</b></summary>

```js
const form = document.getElementById("add-form");
if (form) {
  form.addEventListener("submit", (e) => {
    e.preventDefault();
    const msg = document.getElementById("form-message");

    const amount = parseFloat(document.getElementById("amount").value);
    const date   = document.getElementById("date").value;
    const desc   = document.getElementById("description").value.trim();

    if (isNaN(amount) || amount <= 0)        return showError(msg, "Amount must be > 0");
    if (!desc)                               return showError(msg, "Description is required");
    if (new Date(date) > new Date())         return showError(msg, "Date can't be in the future");

    // ... continue to POST (F079)
  });
}

function showError(el, text) {
  el.className = "error"; el.textContent = text;
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```js
// frontend-static/app.js  — TICKET-F077

const addForm = document.getElementById("add-form");
if (addForm) {
  const message = document.getElementById("form-message");

  addForm.addEventListener("submit", (e) => {
    e.preventDefault();
    addForm.classList.add("was-submitted");
    message.className = "";
    message.textContent = "";

    const amount   = parseFloat(document.getElementById("amount").value);
    const date     = document.getElementById("date").value;
    const desc     = document.getElementById("description").value.trim();
    const type     = document.getElementById("type").value;
    const category = document.getElementById("category").value;

    if (isNaN(amount) || amount <= 0) return setError("Amount must be greater than 0.");
    if (!date)                        return setError("Date is required.");
    if (new Date(date) > new Date())  return setError("Date cannot be in the future.");
    if (!desc)                        return setError("Description is required.");
    if (!type)                        return setError("Choose a type.");
    if (!category)                    return setError("Choose a category.");

    submitTransaction({ amount, date, description: desc, type, category });

    function setError(text) {
      message.className = "error";
      message.textContent = text;
    }
  });
}

// stub — implemented fully in F079
function submitTransaction(payload) {
  console.log("would POST", payload);
}
```

Test path: open `add-transaction.html`, click Submit with empty fields → "Amount must be greater than 0." Fill amount, leave date empty → date error. Set date to tomorrow → "Date cannot be in the future." Fill everything → no error (handler continues to F079).

</details>

---

### TICKET-F078: Write `app.js` -- Fetch GET `/api/transactions`
**File:** `frontend-static/app.js`

**Description:** Load transactions from the Spring Boot backend and render them into the table.

**What**
- A `fetch("http://localhost:8080/api/transactions")` block guarded by `#txn-rows`, parsing the JSON array and writing one `<tr>` per row into the tbody with type-based colour classes.

**Why**
- This is the read half of the front-to-back integration: the static HTML becomes live data instead of hand-typed sample rows.

**Observe**
- Open `transactions.html` with the backend running → rows from the database appear with income amounts green and expense amounts red; stop the backend and reload → a clear error message replaces the empty table.

**Instructions:**
1. Run only on `transactions.html`: guard with `if (document.getElementById("txn-rows"))`
2. Call `fetch("http://localhost:8080/api/transactions")`
3. `.then(res => res.json())` then loop over the array
4. For each row, build `<tr>` with `<td>` cells and append to `tbody`
5. Apply `class="amount income"` or `class="amount expense"` based on `txn.type`
6. Backend must be running -- print a clear error to `#loading` if the fetch fails

**Acceptance Criteria:**
- [ ] Open `transactions.html` -- rows from the database appear
- [ ] Income amounts green, expense amounts red
- [ ] No CORS errors (CorsConfig.java in the backend handles this)
- [ ] If backend is down, an error message shows instead of a blank table

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Guard with `if (document.getElementById("txn-rows"))` so this only runs on `transactions.html`. Call `fetch(...)`, parse JSON, then loop building `<tr>` rows by string-concatenating template literals and assigning `innerHTML`. Wrap in try/catch (or `.catch`) so backend-down doesn't leave a blank page.

</details>

<details>
<summary><b>Hint 2 — Skeleton</b></summary>

```js
const rowsEl = document.getElementById("txn-rows");
if (rowsEl) {
  fetch("http://localhost:8080/api/transactions")
    .then(r => { if (!r.ok) throw new Error("HTTP " + r.status); return r.json(); })
    .then(rows => {
      rowsEl.innerHTML = rows.map(t => `
        <tr>
          <td>${t.txnId}</td>
          <td>${t.txnDate}</td>
          <td>${t.description ?? ""}</td>
          <td>${t.category?.name ?? ""}</td>
          <td>${t.type}</td>
          <td class="amount ${t.type === "INCOME" ? "income" : "expense"}">
            £${Number(t.amount).toFixed(2)}
          </td>
          <td><button class="delete-btn" data-id="${t.txnId}">Delete</button></td>
        </tr>`).join("");
    })
    .catch(err => {
      document.getElementById("loading").textContent =
        "Could not load transactions: " + err.message;
    });
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution + escape helper</b></summary>

```js
// frontend-static/app.js  — TICKET-F078

const rowsEl    = document.getElementById("txn-rows");
const loadingEl = document.getElementById("loading");

if (rowsEl) {
  loadingEl.hidden = false;

  fetch("http://localhost:8080/api/transactions")
    .then(res => {
      if (!res.ok) throw new Error("HTTP " + res.status);
      return res.json();
    })
    .then(transactions => {
      if (transactions.length === 0) {
        rowsEl.innerHTML =
          `<tr><td colspan="7" style="text-align:center;color:#888;">
             No transactions yet
           </td></tr>`;
        return;
      }
      rowsEl.innerHTML = transactions.map(toRow).join("");
    })
    .catch(err => {
      rowsEl.innerHTML =
        `<tr><td colspan="7" class="error-msg">
           Could not load transactions: ${esc(err.message)}
         </td></tr>`;
    })
    .finally(() => { loadingEl.hidden = true; });   // F081 hides the spinner
}

function toRow(t) {
  const cls = t.type === "INCOME" ? "income" : "expense";
  return `
    <tr data-id="${t.txnId}">
      <td>${t.txnId}</td>
      <td>${t.txnDate}</td>
      <td>${esc(t.description ?? "")}</td>
      <td>${esc(t.category?.name ?? "")}</td>
      <td>${t.type}</td>
      <td class="amount ${cls}">£${Number(t.amount).toFixed(2)}</td>
      <td><button class="delete-btn" data-id="${t.txnId}">Delete</button></td>
    </tr>`;
}

function esc(s) {
  return String(s).replace(/[&<>"']/g, c =>
    ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
}
```

`esc()` prevents an XSS hole — if a description ever contained `<script>`, the browser would otherwise execute it. The DOM-API alternative (`document.createElement('td'); td.textContent = ...`) escapes automatically but is verbose.

CORS note: the backend's `CorsConfig.java` already permits `localhost:5173` and `localhost:3000`. If you serve the static pages via `file://`, browsers may still block; run `npx serve frontend-static` to host on `http://localhost:3000`.

</details>

---

### TICKET-F079: Write `app.js` -- Fetch POST `/api/transactions`
**File:** `frontend-static/app.js`

**Description:** Submit the validated form to the backend.

**What**
- A `submitTransaction(payload)` helper that POSTs the validated form as JSON to `/api/transactions`, resets the form on 2xx, and writes errors to `#form-message` on failure.

**Why**
- Without this the form is decorative — F077 just validates locally. The POST is what actually persists a row in the database.

**Observe**
- Submit a valid form → "Saved transaction #N" appears, fields clear; navigate to `transactions.html` → the new row is there. Stop the backend, submit again → error message shows and the form keeps its values.

**Instructions:**
1. After validation passes (continuation of F077), build a payload:
   ```js
   const payload = { amount, date, description, type, category };
   ```
2. `fetch("http://localhost:8080/api/transactions", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload) })`
3. On 2xx: write "Transaction added" to `#form-message`, clear the form with `form.reset()`
4. On failure: show error in `#form-message`

**Acceptance Criteria:**
- [ ] Submitting a valid form creates a row in the DB (check by visiting `transactions.html`)
- [ ] Success message appears
- [ ] Form clears after success
- [ ] Network error shows an error message, form does not clear

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Take the validated values from F077's listener and POST them to the backend. The body structure must match what the controller expects — for now we send a flat object and trust that you've also tweaked the controller to accept it, OR you build the nested `{user:{userId:1}, category:{categoryId:1}, ...}` shape directly. On 2xx → success message + `form.reset()`. On failure → keep the form filled and show the error.

</details>

<details>
<summary><b>Hint 2 — submitTransaction helper</b></summary>

```js
async function submitTransaction(payload) {
  const message = document.getElementById("form-message");
  try {
    const res = await fetch("http://localhost:8080/api/transactions", {
      method:  "POST",
      headers: { "Content-Type": "application/json" },
      body:    JSON.stringify({
        // Shape that the Spring entity expects:
        user:     { userId: 1 },              // hardcoded for now
        category: { categoryId: lookupCategoryId(payload.category) },
        amount:   payload.amount,
        txnDate:  payload.date,
        description: payload.description,
        type:     payload.type
      })
    });
    if (!res.ok) throw new Error("HTTP " + res.status);

    message.className   = "success";
    message.textContent = "Transaction saved!";
    document.getElementById("add-form").reset();
  } catch (err) {
    message.className   = "error";
    message.textContent = "Could not save: " + err.message;
  }
}

// Day-7's category options are strings; map them to seeded IDs
function lookupCategoryId(name) {
  return { Salary:1, Freelance:2, Food:3, Transport:4, Utilities:5 }[name] ?? 3;
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```js
// frontend-static/app.js  — TICKET-F079

async function submitTransaction(payload) {
  const message = document.getElementById("form-message");

  try {
    const res = await fetch("http://localhost:8080/api/transactions", {
      method:  "POST",
      headers: { "Content-Type": "application/json" },
      body:    JSON.stringify({
        user:        { userId: 1 },                 // demo: always user 1
        category:    { categoryId: idForCategory(payload.category) },
        amount:      payload.amount,
        txnDate:     payload.date,
        description: payload.description,
        type:        payload.type
      })
    });
    if (!res.ok) {
      const errorBody = await res.json().catch(() => ({}));
      throw new Error(errorBody.message || ("HTTP " + res.status));
    }
    const saved = await res.json();

    message.className   = "success";
    message.textContent = "Saved transaction #" + saved.txnId;
    document.getElementById("add-form").reset();
    document.getElementById("add-form").classList.remove("was-submitted");

  } catch (err) {
    message.className   = "error";
    message.textContent = "Could not save: " + err.message;
    // do NOT reset — user keeps their data and can retry
  }
}

const CATEGORY_IDS = {
  Salary: 1, Freelance: 2, Food: 3,
  Transport: 4, Utilities: 5,
  Entertainment: 3, Rent: 4, Other: 3,    // best-effort fallback
};
function idForCategory(name) { return CATEGORY_IDS[name] ?? 3; }
```

Round trip:
1. Open `add-transaction.html`, fill the form, submit → "Saved transaction #26" (or whatever id).
2. Open `transactions.html` → the new row appears at the top (or wherever your sort puts it).
3. Stop the backend, submit again → "Could not save: Failed to fetch" — form is **not** cleared.

In production, replace the hardcoded `user.userId: 1` with whatever auth/session you have. We hardcode here because Day 8 doesn't cover auth.

</details>

---

### TICKET-F080: Write `app.js` -- Delete transaction with confirmation
**File:** `frontend-static/app.js`

**Description:** Add a Delete button to each row in the transactions table.

**What**
- A delegated click listener on `#txn-rows` that pops a `confirm()` dialog, fires `DELETE /api/transactions/{id}`, and removes the `<tr>` from the DOM on success.

**Why**
- Event delegation means one listener handles every current and future row — re-rendering the table after fetches never breaks the delete handler.

**Observe**
- Click Delete on a row → confirm dialog; OK → the row vanishes and stays gone after a page reload; Cancel → nothing happens.

**Instructions:**
1. In the row-rendering loop from F078, append a 7th `<td>` with `<button class="delete-btn" data-id="${txn.txnId}">Delete</button>`
2. After all rows are rendered, attach a click listener (event delegation on tbody):
   ```js
   document.getElementById("txn-rows").addEventListener("click", (e) => {
     if (!e.target.matches(".delete-btn")) return;
     // ... confirm + DELETE
   });
   ```
3. `if (!confirm("Delete this transaction?")) return;`
4. `fetch("http://localhost:8080/api/transactions/" + id, { method: "DELETE" })`
5. On 2xx: remove the `<tr>` from the DOM

**Acceptance Criteria:**
- [ ] Each row has a Delete button
- [ ] Clicking shows a confirmation dialog
- [ ] After confirming, the row vanishes and is gone from the DB
- [ ] Cancelling the confirm dialog does nothing

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Use **event delegation** — one listener on `<tbody>` instead of one per row. The handler checks `e.target.matches('.delete-btn')`, calls `confirm(...)`, fires `DELETE`, and on success removes the row from the DOM. Event delegation matters because rows are re-rendered after fetches; per-row listeners would have to be re-attached every time.

</details>

<details>
<summary><b>Hint 2 — Listener + fetch</b></summary>

```js
document.getElementById("txn-rows")
  ?.addEventListener("click", async (e) => {
    if (!e.target.matches(".delete-btn")) return;

    const id = e.target.dataset.id;
    if (!confirm(`Delete transaction #${id}?`)) return;

    const res = await fetch(`http://localhost:8080/api/transactions/${id}`,
                            { method: "DELETE" });
    if (res.ok) {
      e.target.closest("tr").remove();
    } else {
      alert("Delete failed: HTTP " + res.status);
    }
  });
```

`?.` (optional chaining) means: only attach the listener if the element exists. `closest("tr")` walks up from the clicked button to its row.

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```js
// frontend-static/app.js — TICKET-F080

const tbody = document.getElementById("txn-rows");
if (tbody) {
  tbody.addEventListener("click", handleRowClick);
}

async function handleRowClick(e) {
  const btn = e.target.closest(".delete-btn");
  if (!btn) return;

  const id = btn.dataset.id;
  if (!confirm(`Delete transaction #${id}? This cannot be undone.`)) return;

  // Optimistic UI: disable the button while we wait
  btn.disabled    = true;
  btn.textContent = "Deleting…";

  try {
    const res = await fetch(`http://localhost:8080/api/transactions/${id}`,
                            { method: "DELETE" });
    if (!res.ok) throw new Error("HTTP " + res.status);

    // Remove the row from the DOM (no full re-fetch needed)
    btn.closest("tr").remove();
  } catch (err) {
    btn.disabled    = false;
    btn.textContent = "Delete";
    alert(`Could not delete transaction #${id}: ${err.message}`);
  }
}
```

Walkthrough:
- Click Delete on row 7 → confirm dialog.
- Cancel → nothing happens. ✓
- OK → `DELETE /api/transactions/7` fires → 204 → row disappears. ✓
- If backend returns 404 (already deleted), the alert message surfaces it.

Event delegation = one listener for the whole table, however many rows it has. Re-rendering rows after a fetch doesn't break the handler.

</details>

---

### TICKET-F081: Add loading spinner while fetching data
**File:** `frontend-static/app.js` (+ small CSS in `style.css`)

**Description:** Show a spinner while the GET request is in flight.

**What**
- A CSS-only `.spinner` rule plus a JS toggle that sets `loadingEl.hidden = false` before the F078 fetch and `true` in `.finally(...)`.

**Why**
- On a slow network the page would otherwise sit blank for seconds; a spinner tells the user the app is working, not broken.

**Observe**
- DevTools → Network → Slow 3G → reload `transactions.html` → spinner is visible for ~2 seconds, then disappears the moment rows render (or the error message replaces it).

**Instructions:**
1. In `style.css`, add a CSS-only spinner:
   ```css
   .spinner {
     width: 32px; height: 32px;
     border: 4px solid #eee;
     border-top-color: var(--db-blue);
     border-radius: 50%;
     animation: spin 1s linear infinite;
   }
   @keyframes spin { to { transform: rotate(360deg); } }
   ```
2. In `transactions.html`, replace the "Loading..." text inside `#loading` with `<div class="spinner"></div>`
3. In `app.js` (the fetch block from F078):
   - Before fetch: `loadingEl.style.display = "block"`
   - In `.finally(...)`: `loadingEl.style.display = "none"`

**Acceptance Criteria:**
- [ ] Spinner is visible while data loads (throttle network in DevTools to verify)
- [ ] Spinner disappears once the table is populated
- [ ] Spinner also disappears if the request fails

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

A spinner is one DIV + 6 lines of CSS (border + animation). Toggle visibility with `loadingEl.hidden = false` before the fetch and `loadingEl.hidden = true` in `.finally()` so it hides whether the fetch succeeds or errors.

</details>

<details>
<summary><b>Hint 2 — CSS + JS toggle</b></summary>

`style.css`:

```css
.spinner {
  width: 32px; height: 32px; margin: 1rem auto;
  border: 4px solid #eee;
  border-top-color: var(--db-blue);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
```

`transactions.html`:

```html
<div id="loading" hidden><div class="spinner"></div></div>
```

`app.js` (around the F078 fetch):

```js
loadingEl.hidden = false;
fetch(...)
  .then(...)
  .catch(...)
  .finally(() => { loadingEl.hidden = true; });
```

</details>

<details>
<summary><b>Hint 3 — Full spinner + DevTools test</b></summary>

```css
/* style.css — add near the bottom */
#loading {
  display: flex;
  justify-content: center;
  padding: 2rem;
}
#loading .spinner {
  width: 32px; height: 32px;
  border: 4px solid #eee;
  border-top-color: var(--db-blue);
  border-radius: 50%;
  animation: spin .9s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
```

```html
<!-- transactions.html -->
<div id="loading" hidden><div class="spinner" role="status" aria-label="Loading"></div></div>
```

```js
// app.js — wrap the F078 fetch
const loadingEl = document.getElementById("loading");
if (rowsEl) {
  loadingEl.hidden = false;
  rowsEl.innerHTML = "";

  fetch("http://localhost:8080/api/transactions")
    .then(res => res.json())
    .then(rows => { rowsEl.innerHTML = rows.map(toRow).join(""); })
    .catch(err => { rowsEl.innerHTML = errorRow(err); })
    .finally(() => { loadingEl.hidden = true; });
}
```

**Test it:** open Chrome DevTools → Network tab → Throttling dropdown → "Slow 3G". Reload `transactions.html`. You should see the spinner spin for ~2 seconds before the table appears. Set throttling back to "No throttling" when done.

The animation is GPU-accelerated (transforms), so it doesn't jank during page work.

</details>

---

### TICKET-F082: Create React app (verify Vite setup)
**File:** `frontend/` (provided)

**Description:** Get the React project running and understand its structure.

**What**
- A running Vite dev server (`npm run dev` from `frontend/`) plus a quick read of `package.json`, `vite.config.js`, `index.html`, and `src/main.jsx`.

**Why**
- Every subsequent React ticket (F083-F094) assumes the dev server boots, hot-reload works, and you know which file is the entry point.

**Observe**
- Terminal prints `Local: http://localhost:5173/`; opening that URL shows the React app with the yellow TODO banners, and editing any `.jsx` file updates the browser without a full reload.

**Instructions:**
1. `cd frontend && npm install`
2. `npm run dev` -- open the printed URL
3. Browse `package.json`, `vite.config.js`, `index.html`, `src/main.jsx`
4. Explain to your neighbour: how does `main.jsx` end up rendered in the browser?

**Acceptance Criteria:**
- [ ] `npm install` completes without errors
- [ ] Dev server starts and the app loads in the browser
- [ ] You can describe the role of `main.jsx`, `App.jsx`, and `index.html`
- [ ] Hot reload works -- edit a JSX file and the browser updates without a refresh

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`cd frontend && npm install` (Node 22+; `nvm use 22` if you have older). Then `npm run dev`. Vite prints `Local: http://localhost:5173/` — open it. You should see the React app with the yellow TODO banners. Hot-reload check: edit any `.jsx` file and watch the browser update without reloading.

</details>

<details>
<summary><b>Hint 2 — Project tour</b></summary>

```bash
cd frontend
node -v                # ≥ 18
npm install
npm run dev            # opens on http://localhost:5173
```

Key files:

```
frontend/
├── index.html              <- single <div id="root"> + <script type="module" src="/src/main.jsx">
├── package.json            <- scripts: dev, build, preview
├── vite.config.js          <- Vite config + /api proxy to localhost:8080
├── src/
│   ├── main.jsx            <- renders <App /> into #root
│   ├── App.jsx             <- routes (F083)
│   ├── components/         <- Navbar, TransactionRow, Spinner, ...
│   ├── pages/              <- Dashboard, TransactionList, AddTransactionForm, SavingsGoals
│   ├── hooks/              <- useBudgetAPI.js (F091)
│   └── global.css          <- DB-Blue theme
```

The path from browser → component: `index.html` → `main.jsx` → `<App />` → routes → page component.

</details>

<details>
<summary><b>Hint 3 — Full walkthrough + troubleshooting</b></summary>

```bash
# 1. Node version
node -v
# v20.x.x ✓   (if older: nvm install 22 && nvm use 22)

# 2. Install deps
cd frontend
npm install
# adds ~200MB of node_modules. First time is slow; later runs are cached.

# 3. Boot
npm run dev
#
#   VITE v5.x  ready in 423 ms
#
#   ➜  Local:   http://localhost:5173/
#   ➜  Network: use --host to expose
```

Open the URL — you'll see:
- Navbar with 4 links (Dashboard, Transactions, Add, Goals).
- Each page renders mock data + a yellow `TodoBanner` saying which ticket wires up the real API.
- Browser console: no errors.

**Hot-reload test:** edit `src/pages/Dashboard.jsx`, change a string, save. The browser updates in <500ms without refreshing or losing state. That's Vite's HMR (Hot Module Replacement).

**The render chain explained:**
1. Browser loads `index.html`.
2. The `<script type="module" src="/src/main.jsx">` tag fires.
3. `main.jsx` runs `ReactDOM.createRoot(document.getElementById('root')).render(<App />)`.
4. `<App />` returns the JSX tree (`<BrowserRouter><Navbar /><Routes>...`).
5. React reconciles that tree into actual DOM nodes inside `<div id="root">`.

**Common gotchas:**
- `npm install` hanging → try `npm install --no-audit --no-fund` or delete `node_modules` + `package-lock.json` and retry.
- Port 5173 in use → Vite auto-picks 5174. Or set `--port 5175`.
- Backend not running → API calls fail (we wire those up later, so for F082 just inspect the UI).

</details>

---

### TICKET-F083: Build `<App />` with React Router
**File:** `frontend/src/App.jsx`

**Description:** Wire up routing so each page has its own URL.

**What**
- An `App.jsx` that wraps `<Routes>` in `<BrowserRouter>` and maps `/`, `/transactions`, `/add`, `/goals`, plus a `*` catch-all 404.

**Why**
- Without routing the React app is one screen; routing gives each page a real URL so users can bookmark, deep-link, and use the back button.

**Observe**
- Visiting `localhost:5173/transactions` renders the TransactionList; `localhost:5173/junk` renders the 404; clicking Navbar links changes the URL without a full page reload.

**Instructions:**
1. The provided `App.jsx` skeleton already imports `BrowserRouter`, `Routes`, `Route`
2. Fill in routes for:
   - `/` -- Dashboard
   - `/transactions` -- TransactionList
   - `/add` -- AddTransactionForm
   - `/goals` -- SavingsGoals
3. Add a catch-all `*` route showing "404 - Page Not Found"

**Acceptance Criteria:**
- [ ] All 4 paths render their page component
- [ ] Navigating between pages does NOT cause a full reload (SPA behavior)
- [ ] Unknown URL shows the 404 component
- [ ] You can explain `BrowserRouter` vs `HashRouter`

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Wrap `<Routes>` in `<BrowserRouter>`. Each `<Route path="..." element={<Page />} />`. The wildcard `path="*"` is the catch-all and must come last.

</details>

<details>
<summary><b>Hint 2 — App skeleton</b></summary>

```jsx
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Navbar             from "./components/Navbar";
import Dashboard          from "./pages/Dashboard";
import TransactionList    from "./pages/TransactionList";
import AddTransactionForm from "./pages/AddTransactionForm";
import SavingsGoals       from "./pages/SavingsGoals";

export default function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/"              element={<Dashboard />} />
        <Route path="/transactions"  element={<TransactionList />} />
        <Route path="/add"           element={<AddTransactionForm />} />
        <Route path="/goals"         element={<SavingsGoals />} />
        <Route path="*"              element={<h1>404 — Page Not Found</h1>} />
      </Routes>
    </BrowserRouter>
  );
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution + Router comparison</b></summary>

```jsx
// src/App.jsx
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import Navbar             from "./components/Navbar";
import Dashboard          from "./pages/Dashboard";
import TransactionList    from "./pages/TransactionList";
import AddTransactionForm from "./pages/AddTransactionForm";
import SavingsGoals       from "./pages/SavingsGoals";

function NotFound() {
  return (
    <main style={{ padding: "2rem", textAlign: "center" }}>
      <h1>404 — Page Not Found</h1>
      <p>The page you’re looking for does not exist.</p>
      <Link to="/">← Back to Dashboard</Link>
    </main>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/"             element={<Dashboard />} />
        <Route path="/transactions" element={<TransactionList />} />
        <Route path="/add"          element={<AddTransactionForm />} />
        <Route path="/goals"        element={<SavingsGoals />} />
        <Route path="*"             element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  );
}
```

Verify:
- `localhost:5173/` → Dashboard
- `localhost:5173/transactions` → TransactionList
- `localhost:5173/junk` → "404 — Page Not Found"
- Clicking a Navbar link → URL changes, page re-renders, page does **not** reload.

**BrowserRouter vs HashRouter:**
- `BrowserRouter` uses the History API — URLs look normal (`/transactions`). Requires the web server to fall back to `index.html` for unknown paths (Vite dev server does this; production builds need a `try_files` rule in nginx — see Day 10).
- `HashRouter` puts the path in the URL fragment (`/#/transactions`). Works on any static host with zero server config because the fragment never hits the server. Ugly URLs but bulletproof for plain `file://` or simple static hosts.

We use `BrowserRouter` because the nginx config in Day 10 has a `try_files` fallback. Pick `HashRouter` if your host doesn't support that.

</details>

---

### TICKET-F084: Build `<Navbar />` component
**File:** `frontend/src/components/Navbar.jsx`

**Description:** A persistent navigation bar shown on every page.

**What**
- A `Navbar.jsx` component using `<NavLink>` for each route, rendered inside `App.jsx` above `<Routes>` so it appears on every page.

**Why**
- `<NavLink>` gives `isActive` for free, letting you highlight the current page without manually tracking the URL.

**Observe**
- The Navbar shows on Dashboard, Transactions, Add, and Goals; the link matching the current URL is visually distinct, and clicking links never triggers a full reload.

**Instructions:**
1. Use React Router's `<NavLink>` (not `<a>`) for each link
2. Active route gets a class via the `className` prop: `({ isActive }) => isActive ? "active" : ""`
3. Render `<Navbar />` inside `App.jsx` ABOVE `<Routes>` so it's visible on every page

**Acceptance Criteria:**
- [ ] Navbar is visible on all 4 pages
- [ ] Active link is visually distinguished
- [ ] Clicking a link changes the URL and the page without reload

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Use `NavLink` (not `Link` or `<a>`) so you get the `isActive` boolean for free. Pass a function to `className`: `({isActive}) => isActive ? "active" : ""`. Render `<Navbar />` from `App.jsx` ABOVE `<Routes>` so it persists across pages.

</details>

<details>
<summary><b>Hint 2 — Component skeleton</b></summary>

```jsx
import { NavLink } from "react-router-dom";

export default function Navbar() {
  const link = ({ isActive }) => "nav-link" + (isActive ? " active" : "");
  return (
    <header className="app-header">
      <h1>SmartBudget</h1>
      <nav>
        <NavLink to="/"             className={link} end>Dashboard</NavLink>
        <NavLink to="/transactions" className={link}>Transactions</NavLink>
        <NavLink to="/add"          className={link}>Add</NavLink>
        <NavLink to="/goals"        className={link}>Goals</NavLink>
      </nav>
    </header>
  );
}
```

`end` on the `/` link prevents it from matching every URL (otherwise "Dashboard" stays highlighted on `/add` etc.).

</details>

<details>
<summary><b>Hint 3 — Full Navbar + CSS</b></summary>

```jsx
// src/components/Navbar.jsx
import { NavLink } from "react-router-dom";
import "./Navbar.css";

export default function Navbar() {
  const linkClass = ({ isActive }) =>
    "nav-link" + (isActive ? " nav-link--active" : "");

  return (
    <header className="app-header">
      <h1 className="app-title">SmartBudget</h1>
      <nav className="app-nav">
        <NavLink to="/"             end className={linkClass}>Dashboard</NavLink>
        <NavLink to="/transactions"     className={linkClass}>Transactions</NavLink>
        <NavLink to="/add"              className={linkClass}>Add</NavLink>
        <NavLink to="/goals"            className={linkClass}>Goals</NavLink>
      </nav>
    </header>
  );
}
```

```css
/* src/components/Navbar.css */
.app-header {
  background: #003366;
  color: #fff;
  padding: 1rem 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
}
.app-title { margin: 0; font-size: 1.25rem; }
.app-nav   { display: flex; gap: .25rem; flex-wrap: wrap; }

.nav-link {
  color: #fff; text-decoration: none;
  padding: .5rem 1rem; border-radius: 4px;
  transition: background .15s;
}
.nav-link:hover            { background: #1a4d80; }
.nav-link--active          { background: #1a4d80;
                             border-bottom: 2px solid #C8A951; }
```

The `<Navbar />` lives in `App.jsx` above `<Routes>`, so it renders on every page. Clicking a NavLink changes the URL via React Router's history API — no full page reload, the URL bar updates, the highlighted link follows.

</details>

---

### TICKET-F085: Build `<Dashboard />` -- total income, expenses, balance
**File:** `frontend/src/pages/Dashboard.jsx`

**Description:** Replace the yellow TODO banner with real summary cards.

**What**
- A `Dashboard.jsx` page that reduces the mock transactions array into `totalIncome`, `totalExpenses`, and `netBalance`, then renders three coloured cards.

**Why**
- This proves you can derive UI state from data with plain JS reducers — the same pattern you'll use everywhere once real fetches land in F091.

**Observe**
- Navigate to `/` → three cards show numeric totals (Income green, Expenses red, Net DB-Blue), each formatted to two decimals with a currency prefix.

**Instructions:**
1. The skeleton has `const MOCK_TRANSACTIONS = [...]` -- you'll replace this in F091 once the hook is ready. For now, work with the mock array.
2. Compute three values:
   - `totalIncome` = sum of `amount` where `type === "INCOME"`
   - `totalExpenses` = sum of `amount` where `type === "EXPENSE"`
   - `netBalance` = income - expenses
3. Render three cards: Income (green), Expenses (red), Net (DB-Blue)
4. Format with `value.toFixed(2)` and a `$` prefix

**Acceptance Criteria:**
- [ ] Three cards render with computed values
- [ ] Income and Expenses use the right color
- [ ] Negative balance shows in red

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Reduce the mock array into two totals. `useMemo` so we don't re-sum on every keystroke unrelated to transactions. Render 3 `<div className="card">` elements; conditionally apply a colour class for the net balance based on its sign.

</details>

<details>
<summary><b>Hint 2 — Component shape</b></summary>

```jsx
import { useMemo } from "react";

export default function Dashboard() {
  const txns = MOCK_TRANSACTIONS;        // F091 swaps for the hook

  const { income, expenses, net } = useMemo(() => {
    let income = 0, expenses = 0;
    for (const t of txns) {
      if (t.type === "INCOME")  income   += Number(t.amount);
      if (t.type === "EXPENSE") expenses += Number(t.amount);
    }
    return { income, expenses, net: income - expenses };
  }, [txns]);

  const fmt = n => "£" + n.toFixed(2);

  return (
    <main className="dashboard">
      <div className="card"><h3>Income</h3>   <p className="income">{fmt(income)}</p></div>
      <div className="card"><h3>Expenses</h3> <p className="expense">{fmt(expenses)}</p></div>
      <div className="card"><h3>Net</h3>
        <p className={net < 0 ? "expense" : ""}>{fmt(net)}</p>
      </div>
    </main>
  );
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```jsx
// src/pages/Dashboard.jsx
import { useMemo } from "react";

const MOCK_TRANSACTIONS = [
  { txnId: 1, type: "INCOME",  amount: 3500.00 },
  { txnId: 2, type: "EXPENSE", amount:   45.20 },
  { txnId: 3, type: "EXPENSE", amount:   25.00 },
  { txnId: 4, type: "INCOME",  amount: 4200.00 },
];

export default function Dashboard() {
  const txns = MOCK_TRANSACTIONS;   // F091 will replace with useTransactionData()

  const totals = useMemo(() => {
    let income = 0, expenses = 0;
    for (const t of txns) {
      if (t.type === "INCOME")  income   += Number(t.amount);
      if (t.type === "EXPENSE") expenses += Number(t.amount);
    }
    return { income, expenses, net: income - expenses };
  }, [txns]);

  const fmt = n => "£" + n.toFixed(2);

  return (
    <main style={{ padding: "1.5rem", maxWidth: 1100, margin: "0 auto" }}>
      <h2>Dashboard</h2>
      <section
        style={{ display: "grid",
                 gridTemplateColumns: "repeat(3, 1fr)",
                 gap: "1rem" }}>
        <Card label="Total Income"   value={fmt(totals.income)}   color="green" />
        <Card label="Total Expenses" value={fmt(totals.expenses)} color="red" />
        <Card label="Net Balance"    value={fmt(totals.net)}
              color={totals.net < 0 ? "red" : "blue"} />
      </section>
    </main>
  );
}

function Card({ label, value, color }) {
  const palette = { green: "#2e7d32", red: "#c62828", blue: "#003366" };
  return (
    <article style={{ background: "#fff", padding: "1.25rem",
                      borderRadius: 8, boxShadow: "0 1px 4px rgba(0,0,0,.08)" }}>
      <h3 style={{ margin: 0, color: "#666", fontSize: ".9rem" }}>{label}</h3>
      <p style={{ margin: ".5rem 0 0", fontSize: "1.75rem",
                  fontWeight: 600, color: palette[color] }}>{value}</p>
    </article>
  );
}
```

The yellow TodoBanner from the skeleton can be removed once you've wired the real numbers in (will happen in F091).

Why `useMemo`? Pure performance — the totals are stable for the same `txns` reference; without memoization React would re-run the reduce on every parent re-render. With 4 rows it's irrelevant; with 1000+ rows, it matters.

</details>

---

### TICKET-F086: Build `<TransactionList />` -- fetch & display
**File:** `frontend/src/pages/TransactionList.jsx`

**Description:** Render a table of all transactions.

**What**
- A `TransactionList.jsx` page that maps the mock array to `<TransactionRow key={t.txnId} txn={t} />` inside a `<table>`, with an empty-state message when the array is empty.

**Why**
- This is the canonical "render a list" React pattern; using `key={txn.txnId}` lets React update only changed rows instead of re-rendering the whole table.

**Observe**
- Navigate to `/transactions` → a styled table renders one row per mock entry; the console shows no "missing key" warning; emptying the mock array swaps the table for "No transactions yet."

**Instructions:**
1. Use the mock array provided in the skeleton (real fetch comes in F091)
2. Render a `<table>` with header row and one `<TransactionRow />` per item (component built in F087)
3. Use `.map(txn => <TransactionRow key={txn.txnId} txn={txn} />)`
4. **Always set the `key` prop** -- React needs it for efficient re-renders

**Acceptance Criteria:**
- [ ] Table renders with all rows from the mock array
- [ ] No console warning about missing keys
- [ ] Empty state ("No transactions yet") shows if the array is empty

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`<table>` with `<thead>` + `<tbody>`. Map the array to `<TransactionRow key={t.txnId} txn={t} />` — and **always set `key`**. Render a different message when `txns.length === 0` (the "empty state").

</details>

<details>
<summary><b>Hint 2 — Component skeleton</b></summary>

```jsx
import TransactionRow from "../components/TransactionRow";

const MOCK_TRANSACTIONS = [
  { txnId: 1, txnDate: "2026-01-01", description: "Salary",
    category: { name: "Salary" }, type: "INCOME",  amount: 3500 },
  { txnId: 2, txnDate: "2026-01-08", description: "Groceries",
    category: { name: "Food"   }, type: "EXPENSE", amount: 45.2 },
];

export default function TransactionList() {
  const txns = MOCK_TRANSACTIONS;     // F091 swaps to useTransactionData()

  if (txns.length === 0) {
    return <p style={{ padding: "1rem" }}>No transactions yet.</p>;
  }

  return (
    <table>
      <thead><tr>
        <th>Date</th><th>Description</th><th>Category</th>
        <th>Type</th><th>Amount</th><th></th>
      </tr></thead>
      <tbody>
        {txns.map(t => (
          <TransactionRow key={t.txnId} txn={t}
            onDelete={id => console.log("delete", id)} />
        ))}
      </tbody>
    </table>
  );
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```jsx
// src/pages/TransactionList.jsx
import TransactionRow from "../components/TransactionRow";

const MOCK_TRANSACTIONS = [
  { txnId: 1, txnDate: "2026-01-01", description: "January salary",
    category: { name: "Salary"    }, type: "INCOME",  amount: 3500.00 },
  { txnId: 2, txnDate: "2026-01-08", description: "Groceries",
    category: { name: "Food"      }, type: "EXPENSE", amount:   45.20 },
  { txnId: 3, txnDate: "2026-01-15", description: "Bus pass",
    category: { name: "Transport" }, type: "EXPENSE", amount:   25.00 },
];

export default function TransactionList() {
  const txns = MOCK_TRANSACTIONS;       // F091 -> useTransactionData()

  return (
    <main style={{ padding: "1.5rem", maxWidth: 1100, margin: "0 auto" }}>
      <h2>Transactions</h2>

      {txns.length === 0 ? (
        <p style={{ color: "#666" }}>No transactions yet.</p>
      ) : (
        <table style={{ width: "100%", borderCollapse: "collapse",
                        background: "#fff", borderRadius: 8, overflow: "hidden",
                        boxShadow: "0 1px 4px rgba(0,0,0,.08)" }}>
          <thead style={{ background: "#003366", color: "#fff" }}>
            <tr>
              <th style={th}>Date</th>
              <th style={th}>Description</th>
              <th style={th}>Category</th>
              <th style={th}>Type</th>
              <th style={{ ...th, textAlign: "right" }}>Amount</th>
              <th style={th}></th>
            </tr>
          </thead>
          <tbody>
            {txns.map(t => (
              <TransactionRow
                key={t.txnId}
                txn={t}
                onDelete={id => console.log("delete", id)} />
            ))}
          </tbody>
        </table>
      )}
    </main>
  );
}

const th = { padding: ".75rem 1rem", textAlign: "left",
             fontSize: ".85rem", textTransform: "uppercase" };
```

**Why `key`?** React uses `key` to match old children to new children when the array changes (inserts, deletes, reorders). Without `key`, React falls back to "diff by position" — and any mid-array insert causes every subsequent row to re-render and lose its focus/state. Console warning `Warning: Each child in a list should have a unique "key" prop` is React begging you not to ship this.

Empty array now → "No transactions yet." Real fetch (F091) will show this when the DB is freshly seeded with zero.

</details>

---

### TICKET-F087: Build `<TransactionRow />` with delete button
**File:** `frontend/src/components/TransactionRow.jsx` (create)

**Description:** A single table row, with a Delete button.

**What**
- A `TransactionRow.jsx` function component that takes `txn` and `onDelete` props, renders one `<tr>` of cells, and calls `onDelete(txn.txnId)` from the Delete button.

**Why**
- Keeping the row a "dumb" component — data in, events out — makes it trivially reusable and testable; the parent decides what "delete" actually does.

**Observe**
- Each row in `/transactions` has a Delete button; clicking it logs the txnId to the console; income amounts render green, expenses red.

**Instructions:**
1. Component receives `txn` as a prop and an `onDelete` callback prop
2. Render one `<tr>` with cells for: date, description, category, type, amount, Delete button
3. Income amounts green, expense amounts red
4. Delete button calls `onDelete(txn.txnId)` when clicked
5. `<TransactionList />` passes `onDelete={(id) => console.log("delete", id)}` for now -- wired to fetch in F089/F091

**Acceptance Criteria:**
- [ ] Component renders one row from a `txn` prop
- [ ] Delete button click logs the txnId to the console
- [ ] Amount color matches the type

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Function component, single `<tr>` of `<td>` cells. Props: `txn` (the data) and `onDelete` (a callback). The button calls `onDelete(txn.txnId)`. Apply colour via a conditional `className`.

</details>

<details>
<summary><b>Hint 2 — Component shape</b></summary>

```jsx
export default function TransactionRow({ txn, onDelete }) {
  const amountClass = txn.type === "INCOME" ? "amount income" : "amount expense";
  return (
    <tr>
      <td>{txn.txnDate}</td>
      <td>{txn.description}</td>
      <td>{txn.category?.name}</td>
      <td>{txn.type}</td>
      <td className={amountClass}>£{Number(txn.amount).toFixed(2)}</td>
      <td><button onClick={() => onDelete(txn.txnId)}>Delete</button></td>
    </tr>
  );
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```jsx
// src/components/TransactionRow.jsx

export default function TransactionRow({ txn, onDelete }) {
  const isIncome = txn.type === "INCOME";
  const amountColor = isIncome ? "#2e7d32" : "#c62828";

  return (
    <tr>
      <td style={td}>{txn.txnDate}</td>
      <td style={td}>{txn.description}</td>
      <td style={td}>{txn.category?.name ?? "—"}</td>
      <td style={td}>{txn.type}</td>
      <td style={{ ...td, textAlign: "right",
                   color: amountColor, fontWeight: 600,
                   fontVariantNumeric: "tabular-nums" }}>
        £{Number(txn.amount).toFixed(2)}
      </td>
      <td style={td}>
        <button onClick={() => onDelete(txn.txnId)} style={delBtn}>
          Delete
        </button>
      </td>
    </tr>
  );
}

const td = { padding: ".75rem 1rem", borderBottom: "1px solid #eee" };
const delBtn = {
  background: "#fff", color: "#c62828",
  border: "1px solid #c62828", borderRadius: 4,
  padding: ".25rem .75rem", cursor: "pointer",
};
```

Click Delete on any row → console prints `delete <txnId>`. (F089/F091 will turn that callback into a real `fetch DELETE`.)

Why use a callback prop instead of the row doing the fetch itself? Separation: the row is a **dumb component** — it renders data and reports events. The parent decides what "delete" means (fetch? update local state? confirm dialog?). That makes the row trivially reusable and trivially testable.

</details>

---

### TICKET-F088: Build `<AddTransactionForm />` -- controlled form
**File:** `frontend/src/pages/AddTransactionForm.jsx`

**Description:** A controlled-component form.

**What**
- An `AddTransactionForm.jsx` page where every input's `value` reads from `useState` and `onChange` writes back, plus inline red validation messages.

**Why**
- Controlled components make React state the single source of truth, which is what lets you re-run validation on every keystroke and reset the form atomically in F089.

**Observe**
- Typing in any field updates the corresponding piece of state in React DevTools; invalid fields show a red message below them; the form does not yet submit (F089 wires that up).

**Instructions:**
1. Use `useState` for each field: amount, date, description, type, category
2. Each input's `value` reads from state; `onChange` writes back to state
3. Add a `<select>` for type (INCOME/EXPENSE) and category (same options as Day 7)
4. Add inline validation: red text below an invalid field

**Acceptance Criteria:**
- [ ] Typing in a field updates state (verify with React DevTools)
- [ ] Form fields are CONTROLLED (not uncontrolled) -- React state is the source of truth
- [ ] Validation messages appear when a field is invalid

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Controlled input pattern for every field: `value={field}` reads from state, `onChange={e => setField(e.target.value)}` writes back. State lives in `useState`. Show validation errors below each invalid field. Don't submit yet — F089 does that.

</details>

<details>
<summary><b>Hint 2 — Component skeleton</b></summary>

```jsx
import { useState } from "react";

export default function AddTransactionForm() {
  const [amount,      setAmount]      = useState("");
  const [date,        setDate]        = useState("");
  const [description, setDescription] = useState("");
  const [type,        setType]        = useState("");
  const [category,    setCategory]    = useState("");
  const [errors,      setErrors]      = useState({});

  function handleSubmit(e) {
    e.preventDefault();
    const errs = {};
    if (!amount || Number(amount) <= 0) errs.amount = "Must be > 0";
    if (!date)                          errs.date   = "Required";
    if (new Date(date) > new Date())    errs.date   = "Cannot be in future";
    if (!description.trim())            errs.description = "Required";
    if (!type)                          errs.type   = "Required";
    if (!category)                      errs.category = "Required";
    setErrors(errs);
    if (Object.keys(errs).length) return;
    // ... call API in F089
  }

  return (
    <form onSubmit={handleSubmit}>
      <label>Amount
        <input value={amount} onChange={e => setAmount(e.target.value)}
               type="number" step="0.01" />
        {errors.amount && <span className="err">{errors.amount}</span>}
      </label>
      {/* ...repeat for other fields */}
      <button type="submit">Add Transaction</button>
    </form>
  );
}
```

</details>

<details>
<summary><b>Hint 3 — Full controlled form</b></summary>

```jsx
// src/pages/AddTransactionForm.jsx
import { useState } from "react";

const CATEGORIES = ["Salary", "Freelance", "Food",
                    "Transport", "Utilities", "Entertainment", "Other"];

export default function AddTransactionForm() {
  const [form, setForm] = useState({
    amount: "", date: "", description: "", type: "", category: ""
  });
  const [errors, setErrors]   = useState({});
  const [message, setMessage] = useState(null);

  function update(field) {
    return e => setForm(f => ({ ...f, [field]: e.target.value }));
  }

  function validate() {
    const errs = {};
    if (!form.amount || Number(form.amount) <= 0)
      errs.amount = "Amount must be greater than 0";
    if (!form.date)                                errs.date = "Date is required";
    else if (new Date(form.date) > new Date())     errs.date = "Date cannot be in the future";
    if (!form.description.trim())                  errs.description = "Description is required";
    if (!form.type)                                errs.type = "Choose a type";
    if (!form.category)                            errs.category = "Choose a category";
    return errs;
  }

  function handleSubmit(e) {
    e.preventDefault();
    setMessage(null);
    const errs = validate();
    setErrors(errs);
    if (Object.keys(errs).length) return;

    // F089 will replace this:
    console.log("would submit", form);
  }

  return (
    <main style={{ maxWidth: 500, margin: "1.5rem auto", padding: "0 1rem" }}>
      <h2>Add Transaction</h2>

      <form onSubmit={handleSubmit} noValidate>
        <Field label="Amount (£)" error={errors.amount}>
          <input type="number" step="0.01" value={form.amount}
                 onChange={update("amount")} />
        </Field>

        <Field label="Date" error={errors.date}>
          <input type="date" value={form.date} onChange={update("date")} />
        </Field>

        <Field label="Description" error={errors.description}>
          <input type="text" maxLength={200} value={form.description}
                 onChange={update("description")} />
        </Field>

        <Field label="Type" error={errors.type}>
          <select value={form.type} onChange={update("type")}>
            <option value="">— select —</option>
            <option value="INCOME">Income</option>
            <option value="EXPENSE">Expense</option>
          </select>
        </Field>

        <Field label="Category" error={errors.category}>
          <select value={form.category} onChange={update("category")}>
            <option value="">— select —</option>
            {CATEGORIES.map(c => <option key={c}>{c}</option>)}
          </select>
        </Field>

        <button type="submit" style={btnStyle}>Add Transaction</button>
      </form>

      {message && <p>{message}</p>}
    </main>
  );
}

function Field({ label, error, children }) {
  return (
    <label style={{ display: "block", margin: "1rem 0" }}>
      <span style={{ display: "block", fontWeight: 500 }}>{label}</span>
      {children}
      {error && <span style={{ color: "#c62828", fontSize: ".85rem" }}>{error}</span>}
    </label>
  );
}

const btnStyle = {
  background: "#003366", color: "#fff",
  padding: ".75rem 1.5rem", border: 0, borderRadius: 4,
  fontWeight: 600, cursor: "pointer", marginTop: "1rem",
};
```

**Controlled** = React state is the single source of truth. Type → onChange → setState → React re-renders → input `value` updates. Compare with an **uncontrolled** form (refs only), where the DOM holds the truth and React reads it via `ref.current.value` — fine for tiny forms, painful for validation/dynamic UI.

Open React DevTools → select the component → expand `state` → watch each keystroke update `form.amount`. That confirms it's controlled.

</details>

---

### TICKET-F089: Implement form submission -- POST to `/api/transactions`
**File:** `frontend/src/pages/AddTransactionForm.jsx`

**Description:** Submit the form to the backend.

**What**
- An async `handleSubmit` on the F088 form that re-validates, POSTs the payload to `/api/transactions` via the Vite proxy, resets state on 2xx, and shows an error message on failure.

**Why**
- Submitting through the Vite proxy keeps requests same-origin in dev (no CORS dance) and mirrors what the nginx setup will do in Day 10's production build.

**Observe**
- Submitting a valid form shows "Saved transaction #N" and clears every field; visiting `/transactions` shows the new row. Stopping the backend and resubmitting shows an error and leaves the form values intact.

**Instructions:**
1. On submit: `e.preventDefault()`
2. Re-run validation. If invalid, do not submit.
3. `fetch("http://localhost:8080/api/transactions", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ amount: parseFloat(amount), date, description, type, category }) })`
4. On 2xx: clear the form state (set every field back to its initial value) and show a success message
5. On error: show a clear error message

**Acceptance Criteria:**
- [ ] Valid form creates a DB row
- [ ] Form clears on success
- [ ] Network error shows an error message; form does NOT clear

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Build the `fetch POST` inside `handleSubmit`. Wrap in try/catch so errors are caught. On success: reset every `useState` back to its initial value, show success message. On failure: keep the form intact and show error.

</details>

<details>
<summary><b>Hint 2 — Async submit</b></summary>

```jsx
async function handleSubmit(e) {
  e.preventDefault();
  const errs = validate();
  setErrors(errs);
  if (Object.keys(errs).length) return;

  try {
    const res = await fetch("/api/transactions", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        user:     { userId: 1 },
        category: { categoryId: idForCategory(form.category) },
        amount:   parseFloat(form.amount),
        txnDate:  form.date,
        description: form.description,
        type:     form.type,
      }),
    });
    if (!res.ok) throw new Error("HTTP " + res.status);

    setForm({ amount: "", date: "", description: "", type: "", category: "" });
    setMessage({ kind: "success", text: "Transaction saved!" });
  } catch (err) {
    setMessage({ kind: "error", text: "Save failed: " + err.message });
  }
}
```

`/api/...` works (no `localhost:8080` prefix) because `vite.config.js` proxies it to the backend.

</details>

<details>
<summary><b>Hint 3 — Full async submit + Vite proxy reminder</b></summary>

```jsx
async function handleSubmit(e) {
  e.preventDefault();
  setMessage(null);

  const errs = validate();
  setErrors(errs);
  if (Object.keys(errs).length) return;

  try {
    const res = await fetch("/api/transactions", {
      method:  "POST",
      headers: { "Content-Type": "application/json" },
      body:    JSON.stringify({
        user:        { userId: 1 },
        category:    { categoryId: idForCategory(form.category) },
        amount:      parseFloat(form.amount),
        txnDate:     form.date,
        description: form.description,
        type:        form.type,
      }),
    });

    if (!res.ok) {
      const body = await res.json().catch(() => null);
      throw new Error(body?.message || `HTTP ${res.status}`);
    }
    const saved = await res.json();

    // Reset on success
    setForm({ amount: "", date: "", description: "", type: "", category: "" });
    setErrors({});
    setMessage({ kind: "success", text: `Saved transaction #${saved.txnId}` });
  } catch (err) {
    // Keep form intact on failure
    setMessage({ kind: "error", text: `Could not save: ${err.message}` });
  }
}

const CATEGORY_IDS = {
  Salary: 1, Freelance: 2, Food: 3,
  Transport: 4, Utilities: 5,
};
function idForCategory(name) { return CATEGORY_IDS[name] ?? 3; }
```

Render `message`:

```jsx
{message && (
  <p style={{
    padding: ".75rem 1rem", borderRadius: 4,
    background: message.kind === "success" ? "#e8f5e9" : "#ffebee",
    color:      message.kind === "success" ? "#2e7d32" : "#c62828",
  }}>
    {message.text}
  </p>
)}
```

**Vite proxy:** `vite.config.js` should already contain:
```js
server: {
  proxy: { "/api": "http://localhost:8080" }
}
```
This lets the frontend call `/api/...` as if it were same-origin — Vite forwards the request to the Spring Boot server, avoiding CORS issues during dev. In production (Day 10), nginx handles the same proxying.

**Test path:**
1. Fill form, submit → "Saved transaction #X", form clears.
2. Navigate to `/transactions` → new row appears.
3. Stop backend, submit again → "Could not save: Failed to fetch", form stays filled (you don't lose your typing).

</details>

---

### TICKET-F090: Build `<SavingsGoals />` with progress bar
**File:** `frontend/src/pages/SavingsGoals.jsx`

**Description:** Render each savings goal as a card with a progress bar.

**What**
- A `SavingsGoals.jsx` page that maps mock goals to cards, each with name, current/target text, a progress bar whose width = `Math.min(100, current/target * 100)`, and a percent label.

**Why**
- The progress bar is the first place you compute a derived numeric style inline — a small but realistic taste of dynamic CSS driven by data.

**Observe**
- Navigate to `/goals` → each goal shows a bar whose width matches its percent; a goal with `currentAmount === targetAmount` shows a full green bar; an over-funded goal still caps its bar at 100% width.

**Instructions:**
1. Use the mock array provided in the skeleton (real fetch in F091)
2. For each goal, render:
   - Goal name
   - Current / target amounts
   - Progress bar (width = `(current / target) * 100` clamped to 100)
   - Percentage text
3. Use CSS to color the bar by progress (e.g., < 33% red, < 66% yellow, >= 66% green)

**Acceptance Criteria:**
- [ ] Each goal card renders with a visible progress bar
- [ ] Width matches the percentage
- [ ] Overflows (goals over 100%) cap at 100% width

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Map mock goals to cards. Percent = `Math.min(100, (current / target) * 100)`. Two divs implement the bar: outer (grey background) + inner (coloured, width set inline). Pick a colour by percent buckets.

</details>

<details>
<summary><b>Hint 2 — Component shape</b></summary>

```jsx
const MOCK_GOALS = [
  { goalId: 1, goalName: "Holiday",    targetAmount: 1000, currentAmount: 250 },
  { goalId: 2, goalName: "New Laptop", targetAmount: 1500, currentAmount: 1500 },
  { goalId: 3, goalName: "Emergency",  targetAmount: 5000, currentAmount: 800 },
];

export default function SavingsGoals() {
  return (
    <main>
      <h2>Savings Goals</h2>
      {MOCK_GOALS.map(g => <GoalCard key={g.goalId} goal={g} />)}
    </main>
  );
}

function GoalCard({ goal }) {
  const raw = (Number(goal.currentAmount) / Number(goal.targetAmount)) * 100;
  const pct = Math.min(100, Math.max(0, raw));
  const colour = pct < 33 ? "#c62828" : pct < 66 ? "#f9a825" : "#2e7d32";

  return (
    <article className="goal-card">
      <h3>{goal.goalName}</h3>
      <p>£{goal.currentAmount} of £{goal.targetAmount}</p>
      <div className="progress-bar">
        <div className="progress-fill" style={{ width: `${pct}%`, background: colour }} />
      </div>
      <p>{pct.toFixed(0)}%</p>
    </article>
  );
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```jsx
// src/pages/SavingsGoals.jsx
const MOCK_GOALS = [
  { goalId: 1, goalName: "Holiday Fund",     targetAmount: 2000, currentAmount:  450 },
  { goalId: 2, goalName: "New Laptop",       targetAmount: 1500, currentAmount: 1500 },
  { goalId: 3, goalName: "Emergency Buffer", targetAmount: 5000, currentAmount:  800 },
  { goalId: 4, goalName: "Wedding",          targetAmount:10000, currentAmount: 2500 },
];

export default function SavingsGoals() {
  return (
    <main style={{ maxWidth: 600, margin: "1.5rem auto", padding: "0 1rem" }}>
      <h2>Savings Goals</h2>
      {MOCK_GOALS.map(g => <GoalCard key={g.goalId} goal={g} />)}
    </main>
  );
}

function GoalCard({ goal }) {
  const target  = Number(goal.targetAmount);
  const current = Number(goal.currentAmount);
  const raw     = target === 0 ? 0 : (current / target) * 100;
  const pct     = Math.min(100, Math.max(0, raw));
  const colour  = pct < 33 ? "#c62828"
                : pct < 66 ? "#f9a825"
                :            "#2e7d32";

  return (
    <article style={card}>
      <h3 style={{ margin: 0 }}>{goal.goalName}</h3>
      <p style={{ margin: ".5rem 0", color: "#555" }}>
        £{current.toFixed(2)} of £{target.toFixed(2)}
      </p>
      <div style={track}>
        <div style={{ ...fill, width: `${pct}%`, background: colour }} />
      </div>
      <p style={{ margin: ".5rem 0 0", fontSize: ".9rem" }}>
        {pct.toFixed(0)}% {pct >= 100 ? "(complete)" : ""}
      </p>
    </article>
  );
}

const card  = { background: "#fff", padding: "1rem", margin: "1rem 0",
                borderRadius: 8, boxShadow: "0 1px 4px rgba(0,0,0,.08)" };
const track = { background: "#eee", height: 12, borderRadius: 6, overflow: "hidden" };
const fill  = { height: "100%", transition: "width .4s ease" };
```

`Math.min(100, ...)` is the cap — over-funded goals show 100% bar but their progress text might say `150%`. `Math.max(0, ...)` keeps negative values (corrupt data) from rendering a negative-width bar.

</details>

---

### TICKET-F091: Add `useEffect` to fetch data on mount
**File:** `frontend/src/hooks/useBudgetAPI.js`

**Description:** Replace the mock arrays in Dashboard, TransactionList, and SavingsGoals with real fetches via custom hooks.

**What**
- A `hooks/useBudgetAPI.js` file exporting `useTransactionData`, `useCategoryData`, and `useSavingsGoals`, each wrapping a shared `useFetch(url)` that owns `data`/`loading`/`error` plus a `useEffect` that runs on mount.

**Why**
- Extracting fetch + state + lifecycle into a custom hook is what turns three near-identical pages into three one-line consumers, and it isolates the cleanup logic that prevents setState-on-unmounted bugs.

**Observe**
- Reloading any page shows DB rows instead of the hardcoded mock arrays; the yellow TODO banners are gone; React DevTools shows the hook state stepping from `loading: true` to `loading: false` with `data` populated.

**Instructions:**
1. In `hooks/useBudgetAPI.js`, build three custom hooks:
   - `useTransactionData()` -- GETs `/api/transactions`
   - `useCategoryData()` -- GETs `/api/categories`
   - `useSavingsGoals()` -- GETs `/api/savings-goals`
2. Each hook uses `useState` for `data`, `loading`, `error`, and a `useEffect` that runs the fetch on mount
3. In each page, replace `const MOCK_X = [...]` with `const { data: x, loading, error } = useTransactionData()` (etc.)
4. The yellow TODO banner code can be deleted once the hook is wired up

**Acceptance Criteria:**
- [ ] Reloading the page shows real DB data, not mock data
- [ ] No yellow TODO banners remain
- [ ] React DevTools shows the hook state (loading → success)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Build a `useFetch(url)` helper that owns `data`/`loading`/`error` + `useEffect` for the fetch. Then write three named hooks (`useTransactionData`, `useCategoryData`, `useSavingsGoals`) that each call `useFetch` with the right URL. Replace `const MOCK_X = [...]` in each page with `const { data, loading, error } = useXxx()`.

</details>

<details>
<summary><b>Hint 2 — Hooks file</b></summary>

```jsx
// src/hooks/useBudgetAPI.js
import { useEffect, useState } from "react";

function useFetch(url) {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState(null);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);

    fetch(url)
      .then(r => { if (!r.ok) throw new Error("HTTP " + r.status); return r.json(); })
      .then(d  => { if (!cancelled) setData(d); })
      .catch(e => { if (!cancelled) setError(e); })
      .finally(() => { if (!cancelled) setLoading(false); });

    return () => { cancelled = true; };   // avoid setState on unmounted
  }, [url]);

  return { data, loading, error };
}

export const useTransactionData = () => useFetch("/api/transactions");
export const useCategoryData    = () => useFetch("/api/categories");
export const useSavingsGoals    = () => useFetch("/api/goals/user/1");
```

Then in each page:

```jsx
import { useTransactionData } from "../hooks/useBudgetAPI";

export default function TransactionList() {
  const { data: txns = [], loading, error } = useTransactionData();
  // ...
}
```

</details>

<details>
<summary><b>Hint 3 — Full solution</b></summary>

```jsx
// src/hooks/useBudgetAPI.js
import { useEffect, useState } from "react";

function useFetch(url) {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState(null);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);

    fetch(url)
      .then(res => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then(json => { if (!cancelled) setData(json); })
      .catch(err  => { if (!cancelled) setError(err); })
      .finally(() => { if (!cancelled) setLoading(false); });

    // Cleanup: if the component unmounts before fetch settles,
    // don't call setState (avoids the React warning).
    return () => { cancelled = true; };
  }, [url]);

  return { data, loading, error };
}

export function useTransactionData() { return useFetch("/api/transactions"); }
export function useCategoryData()    { return useFetch("/api/categories");   }
export function useSavingsGoals(userId = 1) {
  return useFetch(`/api/goals/user/${userId}`);
}
```

Wire into `Dashboard.jsx` (replace mock):

```jsx
import { useTransactionData } from "../hooks/useBudgetAPI";

export default function Dashboard() {
  const { data: txns = [], loading, error } = useTransactionData();

  if (loading) return <p>Loading...</p>;
  if (error)   return <p style={{ color: "red" }}>Error: {error.message}</p>;

  // ... same totals computation as before, but now over real txns
}
```

Similarly:
- `TransactionList.jsx` swaps `MOCK_TRANSACTIONS` for `useTransactionData().data`.
- `SavingsGoals.jsx` swaps `MOCK_GOALS` for `useSavingsGoals(1).data`.
- The yellow TODO banners can now be deleted.

**Empty-deps array `[]`** = "run this effect once on mount". Add `[url]` if you want it to re-fetch when the URL prop changes. **The cleanup function** stops a slow fetch from updating state after the component is unmounted (real bug in production: causes "Can't perform state update on an unmounted component" warning).

</details>

---

### TICKET-F092: Add error handling
**File:** `frontend/src/pages/*.jsx`

**Description:** When the API fails, show an error message instead of a blank page.

**What**
- A shared `<ErrorBanner message={...} />` component and an early-return `if (error) return <ErrorBanner .../>;` in every page that consumes a hook.

**Why**
- A blank page on backend failure is indistinguishable from "still loading" — surfacing the error tells the user (and you, in dev) exactly what broke.

**Observe**
- Stop the backend, reload the React app → every data page renders a red banner reading "Could not load: Failed to fetch"; restart the backend and refresh → data returns cleanly.

**Instructions:**
1. In each page that uses a custom hook, check the `error` value
2. If `error` is non-null, render an error UI: `<div className="error-banner">Could not load transactions: {error.message}</div>`
3. Stop trying to render the data UI when there's an error

**Acceptance Criteria:**
- [ ] Stopping the backend and reloading shows error banners on every data page
- [ ] Error UI is styled (red background, white text)
- [ ] Restarting backend + refreshing recovers cleanly

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`if (error) return <ErrorBanner ... />;` — early-return before rendering any data UI. Extract into a tiny shared `<ErrorBanner>` component so every page styles errors the same way.

</details>

<details>
<summary><b>Hint 2 — Reusable component</b></summary>

```jsx
// src/components/ErrorBanner.jsx
export default function ErrorBanner({ message }) {
  return (
    <div role="alert" style={{
      background: "#ffebee", color: "#c62828",
      padding: "1rem", borderRadius: 8, margin: "1rem 0",
      border: "1px solid #ef9a9a",
    }}>
      <strong>Something went wrong:</strong> {message}
    </div>
  );
}
```

Use in pages:

```jsx
import ErrorBanner from "../components/ErrorBanner";

if (error) return <ErrorBanner message={error.message} />;
```

</details>

<details>
<summary><b>Hint 3 — Full solution + retry</b></summary>

```jsx
// src/components/ErrorBanner.jsx
export default function ErrorBanner({ message, onRetry }) {
  return (
    <div role="alert" style={style.banner}>
      <p style={{ margin: 0 }}>
        <strong>Could not load:</strong> {message}
      </p>
      {onRetry && (
        <button onClick={onRetry} style={style.btn}>
          Retry
        </button>
      )}
    </div>
  );
}

const style = {
  banner: { background: "#ffebee", color: "#c62828",
            padding: "1rem 1.25rem", borderRadius: 8,
            margin: "1rem 0", border: "1px solid #ef9a9a",
            display: "flex", justifyContent: "space-between",
            alignItems: "center", gap: "1rem" },
  btn:    { background: "#fff", border: "1px solid #c62828",
            color: "#c62828", padding: ".4rem .8rem",
            borderRadius: 4, cursor: "pointer" },
};
```

Use in every page:

```jsx
export default function TransactionList() {
  const { data: txns, loading, error } = useTransactionData();

  if (loading) return <Spinner />;
  if (error)   return <ErrorBanner message={error.message} />;
  if (!txns?.length) return <p>No transactions yet.</p>;

  return ( /* table */ );
}
```

For a real Retry, you'd add a `refetch` function inside the hook (incrementing a counter that's added to the `useEffect` deps). For Foundation purposes, the user can just refresh the page.

**Test:**
1. Stop the backend.
2. Reload the React app.
3. All three data pages show "Could not load: Failed to fetch".
4. Restart the backend, click Retry / refresh — data reappears.

</details>

---

### TICKET-F093: Add loading state
**File:** `frontend/src/pages/*.jsx`

**Description:** Show a loading indicator while data is in flight.

**What**
- A reusable `<Spinner />` component plus an early-return `if (loading) return <Spinner />;` in every page that consumes a hook.

**Why**
- Because `loading` starts as `true`, returning the spinner first prevents the "flash of empty state" where the page briefly shows "0 transactions" before the fetch lands.

**Observe**
- DevTools → Network → Slow 3G → navigate to `/transactions` → spinner spins for ~2s, then the table replaces it; no blank or empty-state flash appears in between.

**Instructions:**
1. In each page, check the `loading` value from the hook
2. While loading, render a spinner or "Loading..." message
3. Reuse the spinner CSS from Day 7's `style.css` (copy into the React project's CSS)

**Acceptance Criteria:**
- [ ] Throttling network in DevTools shows a spinner before data appears
- [ ] Spinner disappears once data loads
- [ ] No "flash of empty state" -- loading state covers it

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Same pattern as F092 but with `loading`. Early-return a `<Spinner />` component. Reuse the spinner CSS from Day 7's `style.css` — drop it into a new `src/components/Spinner.jsx` + `Spinner.css`.

</details>

<details>
<summary><b>Hint 2 — Spinner component</b></summary>

```jsx
// src/components/Spinner.jsx
import "./Spinner.css";

export default function Spinner({ label = "Loading…" }) {
  return (
    <div className="spinner-wrap" role="status" aria-label={label}>
      <div className="spinner-circle" />
    </div>
  );
}
```

```css
/* src/components/Spinner.css */
.spinner-wrap   { display: flex; justify-content: center; padding: 2rem; }
.spinner-circle {
  width: 32px; height: 32px;
  border: 4px solid #eee;
  border-top-color: #003366;
  border-radius: 50%;
  animation: spin .9s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
```

Pages early-return:

```jsx
if (loading) return <Spinner />;
if (error)   return <ErrorBanner message={error.message} />;
return ( /* data UI */ );
```

</details>

<details>
<summary><b>Hint 3 — Full solution + skeleton variant</b></summary>

```jsx
// src/components/Spinner.jsx
import "./Spinner.css";

export default function Spinner({ label = "Loading…" }) {
  return (
    <div className="spinner-wrap" role="status" aria-live="polite">
      <div className="spinner-circle" />
      <span className="sr-only">{label}</span>
    </div>
  );
}
```

```css
/* src/components/Spinner.css */
.spinner-wrap {
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  padding: 2rem 0;
}
.spinner-circle {
  width: 36px; height: 36px;
  border: 4px solid #eee;
  border-top-color: #003366;
  border-radius: 50%;
  animation: spin .9s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
.sr-only {
  position: absolute; width: 1px; height: 1px;
  margin: -1px; padding: 0; border: 0;
  overflow: hidden; clip: rect(0,0,0,0); white-space: nowrap;
}
```

Apply in every page that uses a fetch hook:

```jsx
export default function Dashboard() {
  const { data: txns, loading, error } = useTransactionData();

  if (loading) return <Spinner label="Loading dashboard…" />;
  if (error)   return <ErrorBanner message={error.message} />;

  // ... totals + cards
}
```

**No flash-of-empty:** because `loading` starts as `true`, the spinner renders immediately on first paint — the page never briefly shows "0 transactions" before the data arrives.

**Test:** DevTools → Network tab → Throttling → Slow 3G. Navigate to /transactions. Spinner spins for ~2s, then the table appears. No empty state flashes in between.

For a fancier UI: skeleton placeholders (grey blocks shaped like the eventual content) feel snappier than a spinner. Outside the scope of foundation but trivial to add.

</details>

---

### TICKET-F094: Style all components
**File:** `frontend/src/**/*.css`

**Description:** Apply the DB-Blue theme to the React app.

**What**
- Port `frontend-static/style.css` into `src/global.css`, import it once in `main.jsx`, replace inline styles in components with class names, and add a mobile breakpoint.

**Why**
- A single global stylesheet (plus per-component CSS) keeps theme tweaks in one place instead of scattered across `style={...}` props, and matches the Day 7 visual design so the React app looks like the static one.

**Observe**
- Every page (Dashboard, Transactions, Add, Goals) matches the Day 7 look; resizing the browser below 768px collapses the cards grid to a single column; no raw-default browser buttons or yellow TODO banners remain anywhere.

**Instructions:**
1. Port your `style.css` from Day 7 into the React project (rename to `src/styles/global.css` and import in `main.jsx`)
2. Make sure all components look polished: cards, table, form, nav, goals
3. Add responsive media queries

**Acceptance Criteria:**
- [ ] All pages match the Day 7 visual design
- [ ] Mobile layout collapses to single column
- [ ] No unstyled components (no raw browser defaults visible)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Copy `frontend-static/style.css` into `frontend/src/global.css`. Import it once in `main.jsx`. Replace the inline styles you've been using in components with class names; the global CSS handles colours, cards, tables, forms, nav.

</details>

<details>
<summary><b>Hint 2 — Where to put the styles</b></summary>

```jsx
// src/main.jsx
import "./global.css";              // ← add this line
import React  from "react";
import ReactDOM from "react-dom/client";
import App from "./App.jsx";

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
```

In components, swap inline styles for class names:

```jsx
// before
<div style={{ background: "#fff", padding: "1rem", borderRadius: 8 }}>

// after
<div className="card">
```

Add component-specific CSS in `MyComponent.css` and `import "./MyComponent.css"` at the top.

</details>

<details>
<summary><b>Hint 3 — Full structure + breakpoints</b></summary>

```
src/
├── main.jsx              <- import "./global.css"
├── global.css            <- ported from frontend-static/style.css
├── App.jsx
├── components/
│   ├── Navbar.jsx + Navbar.css
│   ├── Spinner.jsx + Spinner.css
│   ├── ErrorBanner.jsx + ErrorBanner.css
│   └── TransactionRow.jsx
└── pages/
    ├── Dashboard.jsx
    ├── TransactionList.jsx
    ├── AddTransactionForm.jsx
    └── SavingsGoals.jsx
```

`src/global.css`:

```css
:root {
  --db-blue: #003366; --db-blue-light: #1a4d80; --db-gold: #C8A951;
  --income: #2e7d32; --expense: #c62828;
  --bg: #f5f5f5; --card-bg: #ffffff; --text: #222; --muted: #666;
  --radius: 8px; --shadow: 0 1px 4px rgba(0,0,0,.08);
}
* { box-sizing: border-box; }
body { margin: 0; font-family: system-ui, -apple-system, sans-serif;
       background: var(--bg); color: var(--text); }

.page { max-width: 1100px; margin: 1.5rem auto; padding: 0 1rem; }

.cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; }
.card  { background: var(--card-bg); padding: 1.25rem;
         border-radius: var(--radius); box-shadow: var(--shadow); }

table  { width: 100%; border-collapse: collapse; background: var(--card-bg);
         border-radius: var(--radius); overflow: hidden; box-shadow: var(--shadow); }
th, td { padding: .75rem 1rem; text-align: left; border-bottom: 1px solid #eee; }
thead  { background: var(--db-blue); color: #fff; }
tbody tr:nth-child(even) { background: #fafafa; }
tbody tr:hover           { background: #eef5fb; }

.amount.income  { color: var(--income); font-weight: 600; }
.amount.expense { color: var(--expense); font-weight: 600; }

.form  { max-width: 500px; background: var(--card-bg); padding: 1.5rem;
         border-radius: var(--radius); box-shadow: var(--shadow); }
.form label    { display: block; margin: 1rem 0 .25rem; font-weight: 500; }
.form input,
.form select   { width: 100%; padding: .55rem .7rem;
                 border: 1px solid #ccc; border-radius: 4px; font: inherit; }
.form button   { margin-top: 1.5rem; background: var(--db-blue); color: #fff;
                 border: none; padding: .75rem 1.5rem; border-radius: 4px;
                 cursor: pointer; font-weight: 600; }

.error-banner   { background: #ffebee; color: var(--expense);
                  padding: 1rem; border-radius: var(--radius); margin: 1rem 0; }

@media (max-width: 768px) {
  .cards { grid-template-columns: 1fr; }
  table  { display: block; overflow-x: auto; white-space: nowrap; }
}
```

Now use them throughout: `<main className="page">`, `<section className="cards">`, etc. Inline styles disappear; theme tweaks happen in one file.

**Final check:** open every page on desktop and mobile-width. Nav, cards, table, form, goals — all should look polished, no leftover yellow `TodoBanner`, no raw browser-default buttons.

</details>

---

## End-of-Day Checklist

- [ ] JS module covered: control flow, functions, arrays, objects, DOM
- [ ] React module covered: JSX, components, props, styling, lifecycle
- [ ] `frontend-static/app.js` validates the form, fetches GET/POST/DELETE, shows a spinner (F077-F081)
- [ ] All 4 React SmartBudget pages render real data from the backend
- [ ] Add Transaction form (React) submits via POST
- [ ] Delete button removes rows
- [ ] Error and loading states work on every page
- [ ] You can explain: what JSX compiles to, why React needs `key`, what `useEffect`'s deps array does

---

*Tomorrow (Day 9): Polish features -- filters, search, charts, edit, contributions, toasts, accessibility.*
