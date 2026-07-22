import { Link } from 'react-router-dom'

// ============================================================
// TICKET-F088/F089 (Day 8, Sprint 7) — Add Transaction Form
// ============================================================
//
// WHAT: A form page that allows users to create new transactions.
//       Uses React "controlled components" — the form state lives in React,
//       not in the DOM. Every input change updates React state, and React
//       re-renders the input with the new value.
//
// WHY:  Controlled components give you full control over form data.
//       You can validate before submission, format values, and prevent
//       invalid characters — all in JavaScript, before hitting the server.
//
// KEY CONCEPTS:
//   Controlled input:  value={state} + onChange={updateState}
//   useState:          Stores form field values as React state
//   handleChange:      A single function that handles ALL input changes
//   handleSubmit:      Sends the form data to the API as JSON
//   e.preventDefault(): Stops the browser's default form submission (page reload)
//   useNavigate:       React Router's hook for programmatic navigation
//
// ============================================================

export default function AddTransactionForm() {

  // -------------------------------------------------------
  // TODO TICKET-F088 (Day 8): Step 1 — Create form state
  // -------------------------------------------------------
  // WHAT: Use useState to create a form state object that holds all input values.
  //       All inputs are "controlled" — their value comes from React state.
  //
  // HOW:  1. Import useState from 'react'
  //       2. Create state: const [form, setForm] = useState({...})
  //          with fields: categoryId, amount, txnDate, description
  //          Set txnDate default to today: new Date().toISOString().substring(0, 10)
  //       3. Create a handleChange function that updates form state:
  //          Accept the event (e), extract name and value from e.target,
  //          update form using setForm with the spread operator: { ...prev, [name]: value }
  //          The [name] syntax uses the input's "name" attribute to determine which field to update
  //       4. Add name, value, and onChange attributes to each input element
  //
  // WHY:  One handleChange function handles ALL inputs because each input has a "name"
  //       attribute that matches a key in the form state object.
  //       This is the standard React pattern — you don't write separate handlers
  //       for each input field.
  //
  // OBSERVE: Type in any input field and console.log(form) — you should see the values update.

  // -------------------------------------------------------
  // TODO TICKET-F091 (Day 8): Step 2 — Populate categories dropdown
  // -------------------------------------------------------
  // WHAT: The category dropdown should show real categories from the API,
  //       not hardcoded options.
  //
  // HOW:  1. Import useCategories from '../hooks/useBudgetAPI'
  //       2. Call it: const categories = useCategories()
  //       3. In the <select> element, map over categories to create <option> elements:
  //          Each option has value={c.categoryId} and displays c.name + c.type
  //       4. Add a default "-- Select category --" option with an empty value
  //
  // WHY:  Hardcoding categories creates a mismatch between frontend and backend.
  //       If someone adds a new category via the API, the dropdown should show it
  //       automatically — without changing frontend code.
  //
  // OBSERVE: Open the form — the dropdown should show: Salary (INCOME), Groceries (EXPENSE), etc.
  //          These come from the database via GET /api/categories.

  // -------------------------------------------------------
  // TODO TICKET-F089 (Day 8): Step 3 — Wire up form submission
  // -------------------------------------------------------
  // WHAT: When the user clicks "Add Transaction," send the form data to the API.
  //       Validate the input first, then POST to /api/transactions.
  //
  // HOW:  1. Import useNavigate from 'react-router-dom'
  //       2. Call it: const navigate = useNavigate()
  //       3. Create an async handleSubmit function:
  //          a. Call e.preventDefault() to stop the browser from reloading the page
  //          b. VALIDATE:
  //             - Category must be selected (form.categoryId is not empty)
  //             - Amount must be > 0 (parseFloat(form.amount) > 0)
  //             - Date must not be in the future (form.txnDate <= today)
  //             If any validation fails, show an alert or set an error state
  //          c. Build the request body as a JSON object:
  //             Include user (with userId), category (with categoryId),
  //             amount (as a number), txnDate, description, and type
  //             NOTE: Determine the type from the selected category's type field
  //          d. Call fetch('/api/transactions', { method: 'POST', headers: {...}, body: JSON.stringify(...) })
  //          e. If the response is OK, navigate to '/transactions' (redirect to the list)
  //          f. If not OK, show an error message
  //       4. Add onSubmit={handleSubmit} to the <form> element
  //       5. Add noValidate to the <form> to disable browser validation (you handle it in JS)
  //
  // WHY:  Client-side validation gives instant feedback (no waiting for the server).
  //       The server ALSO validates (TransactionService) — this is "defense in depth."
  //       useNavigate('/transactions') redirects without a full page reload.
  //
  // OBSERVE: Fill in the form with valid data and submit.
  //          You should be redirected to /transactions and see the new entry in the list.
  //          Try submitting with empty category or negative amount — you should see validation errors.

  return (
    <div>
      <h1 style={{ marginBottom: '1.5rem', color: 'var(--primary)' }}>Add Transaction</h1>

      <div className="card" style={{ maxWidth: 540, textAlign: 'center', padding: '3rem' }}>
        <p style={{ fontSize: '1.1rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
          Build this form in <strong>Sprint 7 (Day 8)</strong>
        </p>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '1.5rem' }}>
          TICKET-F088: Create a controlled form with category, amount, date, and description fields
        </p>

        {/* ------------------------------------------------------- */}
        {/* TODO TICKET-F088: Replace this placeholder with a form   */}
        {/* ------------------------------------------------------- */}
        {/*
          WHAT: An HTML form with 4 fields:
                - Category <select> dropdown
                - Amount <input type="number">
                - Date <input type="date">
                - Description <input type="text">
                Plus a Submit button.

          HOW:  Wrap inputs in a <form> with onSubmit={handleSubmit} and noValidate.
                Each input needs: name, value={form.fieldName}, onChange={handleChange}
                Group each input in a <div className="form-group"> with a <label>.
                The submit button should be <button type="submit" className="btn btn-primary">.

          WHY:  The "name" attribute on each input must match the key in your form state.
                This is what makes handleChange work with one function for all inputs.

          OBSERVE: After building, the form should render with proper labels and styling.
                   All inputs should be interactive (typing updates the value).
        */}

        <Link to="/transactions" className="btn btn-secondary">Back to Transactions</Link>
      </div>
    </div>
  )
}
