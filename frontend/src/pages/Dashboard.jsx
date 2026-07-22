import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'

// ============================================================
// Dashboard — the landing page of SmartBudget
// ============================================================
//
// DAY 1 (provided): Shows a welcome message + fetches /api/categories
//   as "proof-of-life" — confirming the backend is running and API works.
//
// TICKET-F085 (Day 8, Sprint 7): Build the full dashboard with:
//   - Summary stat cards (Total Income, Total Expenses, Balance, Count)
//   - Loading spinner while data fetches
//   - Error message if backend is down
//
// TICKET-F100 (Day 9, Sprint 8): Add a monthly summary bar chart
//
// ============================================================

export default function Dashboard() {
  const [categories, setCategories] = useState([])
  const [status, setStatus]         = useState('loading')

  useEffect(() => {
    fetch('/api/categories')
      .then(res => res.json())
      .then(data => { setCategories(data); setStatus('ok') })
      .catch(() => setStatus('error'))
  }, [])

  return (
    <div>
      <h1 style={{ marginBottom: '1.5rem', color: 'var(--primary)' }}>Dashboard</h1>
      <p style={{ marginBottom: '1.5rem', color: 'var(--text-muted)' }}>
        Welcome to <strong>SmartBudget</strong> — your personal finance tracker.
      </p>

      {/* Day 1 proof-of-life: verify API connectivity */}
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <h3 style={{ marginBottom: '0.5rem', color: 'var(--primary)' }}>API Status</h3>
        {status === 'loading' && <p>Connecting to backend...</p>}
        {status === 'error'   && <p style={{ color: 'var(--danger)' }}>Backend not running. Start with: mvn spring-boot:run</p>}
        {status === 'ok' && (
          <>
            <p style={{ color: 'var(--success)', marginBottom: '0.5rem' }}>Connected — {categories.length} categories loaded</p>
            <ul style={{ paddingLeft: '1.2rem' }}>
              {categories.map(c => (
                <li key={c.categoryId}>{c.name} <span className={`badge badge--${c.type?.toLowerCase()}`}>{c.type}</span></li>
              ))}
            </ul>
          </>
        )}
      </div>

      {/* ------------------------------------------------------- */}
      {/* TODO TICKET-F085 (Day 8): Add summary stat cards         */}
      {/* ------------------------------------------------------- */}
      {/*
        WHAT: Stat cards show key financial metrics at a glance:
              Total Income, Total Expenses, Balance, and Transaction Count.

        HOW:  1. Import useTransactionData() from hooks/useBudgetAPI.js
              2. Call it at the top of the component to get { transactions, loading, error }
              3. Use useMemo (import from 'react') to calculate:
                 - income:   sum of all transactions where type === 'INCOME'
                 - expenses: sum of all transactions where type === 'EXPENSE'
                 - balance:  income - expenses
                 - count:    transactions.length
              4. Create a StatCard helper function that renders a card with label and value
              5. Render 4 StatCards in a grid layout (CSS grid or flexbox)
              6. Show the Spinner component (from components/Feedback.jsx) while loading
              7. Show the ErrorMessage component on error

        WHY:  The dashboard is the first thing users see. Summary metrics give an
              instant overview of financial health. useMemo caches the calculations
              so they only re-run when transactions change, not on every render.

        OBSERVE: After implementing, the dashboard should show 4 colored stat cards.
                 Total Income should be green, Total Expenses red, Balance primary color.
                 The numbers should match what you see in GET /api/transactions.
      */}

      {/* ------------------------------------------------------- */}
      {/* TODO TICKET-F100 (Day 9): Add monthly summary chart      */}
      {/* ------------------------------------------------------- */}
      {/*
        WHAT: A bar chart showing income vs. expenses per month.

        HOW:  1. Import MonthlySummaryChart from components/MonthlySummaryChart.jsx
              2. Pass the transactions array as a prop:
                 <MonthlySummaryChart transactions={transactions} />
              3. Place it below the stat cards

        WHY:  Charts reveal trends that raw numbers don't show.
              "Am I spending more each month?" is hard to answer from a table.

        OBSERVE: A bar chart should appear with green (income) and red (expense) bars.
      */}

      <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
        <Link to="/add"          className="btn btn-primary">+ Add Transaction</Link>
        <Link to="/transactions" className="btn btn-secondary">View Transactions</Link>
        <Link to="/savings"      className="btn btn-secondary">Savings Goals</Link>
      </div>
    </div>
  )
}
