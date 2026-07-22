// ============================================================
// TICKET-F100 (Day 9, Sprint 8) — Monthly Summary Bar Chart
// ============================================================
//
// WHAT: A bar chart that shows income vs. expenses for each month.
//       Uses the Recharts library — a React wrapper around D3.js.
//       The component receives a "transactions" array as a prop and
//       aggregates it into monthly totals before rendering.
//
// WHY:  Visual charts help users spot trends. A table shows raw data,
//       but a chart reveals patterns: "I spend more in December" or
//       "My income increased over the last 3 months."
//
// KEY CONCEPTS:
//   Props:     Data passed FROM a parent component TO this child component
//   useMemo:   Caches expensive calculations (aggregation) to avoid re-computing on every render
//   Recharts:  React charting library with components like <BarChart>, <Bar>, <XAxis>, <YAxis>
//
// ============================================================

export default function MonthlySummaryChart({ transactions = [] }) {

  // -------------------------------------------------------
  // TODO TICKET-F100: Step 1 — Aggregate transactions by month
  // -------------------------------------------------------
  // WHAT: Transform the flat transactions array into monthly summaries.
  //       Each month becomes an object: { month: "2026-05", income: 3500, expense: 1200 }
  //
  // HOW:  Use useMemo to cache the calculation (import from 'react').
  //       Inside useMemo:
  //       1. Create an empty object to accumulate totals (e.g., monthMap = {})
  //       2. Loop through each transaction
  //       3. Extract the month from txnDate (e.g., "2026-05-15" → "2026-05")
  //          using substring(0, 7) or splitting by "-"
  //       4. If the month key doesn't exist in the map, create it with income: 0, expense: 0
  //       5. Based on the transaction type (INCOME or EXPENSE), add the amount to the right field
  //       6. Convert the map to an array using Object.values()
  //       7. Sort by month string (alphabetical sort works for "YYYY-MM" format)
  //
  // WHY:  useMemo prevents this aggregation from running on every render.
  //       It only recalculates when the transactions array changes.
  //       Without useMemo, clicking a button elsewhere would re-aggregate
  //       hundreds of transactions for no reason.
  //
  // OBSERVE: Console.log the result to verify the shape:
  //          [{ month: "2026-01", income: 3500, expense: 1200 }, ...]

  // -------------------------------------------------------
  // TODO TICKET-F100: Step 2 — Render the Recharts bar chart
  // -------------------------------------------------------
  // WHAT: Use Recharts components to render a bar chart with two bars per month.
  //       One bar for income (green), one for expenses (red).
  //
  // HOW:  Import from 'recharts':
  //         BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
  //       Return JSX with this component hierarchy:
  //         <ResponsiveContainer width="100%" height={300}>
  //           <BarChart data={monthlyData}>
  //             <CartesianGrid />              → background grid lines
  //             <XAxis dataKey="month" />       → horizontal axis (month labels)
  //             <YAxis />                       → vertical axis (amounts)
  //             <Tooltip />                     → hover tooltip showing values
  //             <Legend />                      → color legend (Income / Expense)
  //             <Bar dataKey="income"  fill="green-color" name="Income" />
  //             <Bar dataKey="expense" fill="red-color"   name="Expense" />
  //           </BarChart>
  //         </ResponsiveContainer>
  //       ResponsiveContainer makes the chart resize with its parent container.
  //
  // WHY:  Recharts uses a component-based API (React-native approach).
  //       Each chart element is a React component with props — no imperative
  //       canvas drawing. This makes it easy to customize and integrate with React state.
  //
  // OBSERVE: Import this component in Dashboard.jsx and pass the transactions array.
  //          You should see a bar chart with green (income) and red (expense) bars.
  //          Hover over a bar — the tooltip should show the exact amount.
  //          Resize the browser — the chart should resize responsively.

  return (
    <div style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '1rem' }}>
      TODO TICKET-F100: Implement the Recharts bar chart
    </div>
  )
}
