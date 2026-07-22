import { Link } from 'react-router-dom'

// ============================================================
// TICKET-F086/F087/F098/F102 (Day 8-9) — Transaction List Page
// ============================================================
//
// WHAT: This page displays all transactions in a sortable, filterable table.
//       It's the main data view of the application.
//
// WHY:  Users need to see their transaction history, search for specific entries,
//       and perform actions (edit, delete) on individual records.
//
// ============================================================

export default function TransactionList() {

  // -------------------------------------------------------
  // TODO TICKET-F086 (Day 8): Step 1 — Fetch and display transactions
  // -------------------------------------------------------
  // WHAT: Use the custom hook to fetch transactions from the API
  //       and display them in an HTML table.
  //
  // HOW:  1. Import useTransactionData from '../hooks/useBudgetAPI'
  //       2. Call it at the top: const { transactions, loading, error, refetch } = useTransactionData()
  //       3. Import Spinner and ErrorMessage from '../components/Feedback'
  //       4. If loading is true, return <Spinner />
  //       5. If error exists, return <ErrorMessage message={error} />
  //       6. Render a <table> with columns: ID, Date, Category, Description, Amount, Type, Actions
  //       7. Use transactions.map() to render one <tr> per transaction
  //       8. Access nested fields: t.category?.name (the ?. prevents crashes if category is null)
  //       9. Color the amount: green for INCOME, red for EXPENSE
  //          Use inline style: style={{ color: t.type === 'INCOME' ? 'var(--success)' : 'var(--danger)' }}
  //      10. Add a type badge: <span className={`badge badge--${t.type.toLowerCase()}`}>{t.type}</span>
  //
  // WHY:  This is a core React pattern: fetch data → check loading state → render.
  //       The ?. (optional chaining) prevents "Cannot read property of undefined" errors.
  //       Conditional rendering ({loading && <Spinner />}) is how React handles UI states.
  //
  // OBSERVE: The table should show all transactions from the database.
  //          Amounts should be green (income) or red (expense).
  //          While the API loads, a spinner should appear briefly.

  // -------------------------------------------------------
  // TODO TICKET-F087 (Day 8): Step 2 — Add delete functionality
  // -------------------------------------------------------
  // WHAT: Each table row gets a "Delete" button that removes the transaction.
  //
  // HOW:  1. Create a handleDelete(id) async function
  //       2. Show a confirmation dialog: if (!window.confirm('Delete this transaction?')) return
  //       3. Call fetch(`/api/transactions/${id}`, { method: 'DELETE' })
  //       4. If the response is OK, call refetch() to refresh the table
  //       5. If it fails, show an error (alert or toast)
  //       6. Add a "Delete" button in the Actions column of each row
  //          onClick={() => handleDelete(t.txnId)}
  //
  // WHY:  Delete requires a confirmation to prevent accidental data loss.
  //       After deleting, refetch() re-calls the API and React re-renders
  //       the table without the deleted row. No page reload needed.
  //
  // OBSERVE: Click Delete on a transaction → confirm → the row should disappear.
  //          Check the API: GET /api/transactions — the deleted one should be gone.

  // -------------------------------------------------------
  // TODO TICKET-F098 (Day 9): Step 3 — Add filter bar
  // -------------------------------------------------------
  // WHAT: A filter section above the table with:
  //       - Category dropdown (filter by category)
  //       - Date range inputs (from date, to date)
  //       - Search input (filter by description keyword)
  //
  // HOW:  1. Add state variables for each filter: filterCategory, filterFrom, filterTo, searchTerm
  //       2. Use useMemo to create a "filteredTransactions" array that applies all filters
  //       3. Filter logic (inside useMemo):
  //          - If filterCategory is set, keep only transactions where category.name matches
  //          - If filterFrom is set, keep only transactions where txnDate >= filterFrom
  //          - If searchTerm is set, keep only transactions where description includes the term
  //       4. Render the table using filteredTransactions instead of transactions
  //       5. Render filter inputs above the table, each with onChange updating state
  //
  // WHY:  Filtering happens client-side (in the browser) because we already have all data.
  //       useMemo caches the filtered result so it only recalculates when filters or data change.
  //       This is faster than calling the API with filter parameters for every keystroke.
  //
  // OBSERVE: Type in the search box — the table should update instantly (no API calls).
  //          Select a category — only matching transactions should appear.

  // -------------------------------------------------------
  // TODO TICKET-F102 (Day 9): Step 4 — Add edit functionality
  // -------------------------------------------------------
  // WHAT: Each table row gets an "Edit" button that allows inline editing.
  //
  // HOW:  1. Add state for the currently editing transaction: editingId, editForm
  //       2. When Edit is clicked, set editingId to that row's ID
  //          and populate editForm with the current values
  //       3. In the table, if row ID === editingId, show input fields instead of text
  //       4. Add Save/Cancel buttons in the editing row
  //       5. On Save, call PUT /api/transactions/{id} with the updated data
  //       6. On success, call refetch() and clear editingId
  //
  // WHY:  Inline editing is a better UX than navigating to a separate edit page.
  //       The user sees the change immediately in context.
  //
  // OBSERVE: Click Edit → fields should become editable → change the amount →
  //          click Save → the row should update with the new value.

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <h1 style={{ color: 'var(--primary)' }}>Transactions</h1>
        <Link to="/add" className="btn btn-primary">+ Add Transaction</Link>
      </div>

      <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
        <p style={{ fontSize: '1.1rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
          Build this page in <strong>Sprint 7 (Day 8)</strong>
        </p>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          TICKET-F086: Fetch transactions using <code>useTransactionData()</code> hook and display in a table
        </p>
      </div>
    </div>
  )
}
