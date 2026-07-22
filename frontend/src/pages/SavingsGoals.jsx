// ============================================================
// TICKET-F090/F103 (Day 8-9, Sprint 7-8) — Savings Goals Page
// ============================================================
//
// WHAT: This page displays savings goals as cards with progress bars.
//       Each card shows: goal name, deadline, current/target amounts,
//       a visual progress bar, and a Contribute button.
//
// WHY:  Savings goals are a motivational feature — seeing progress toward
//       a goal (like "Holiday Fund: 60% complete") encourages saving.
//       The visual progress bar makes the abstract number tangible.
//
// ============================================================

export default function SavingsGoals() {

  // -------------------------------------------------------
  // TODO TICKET-F090 (Day 8): Step 1 — Fetch and display goals
  // -------------------------------------------------------
  // WHAT: Use the custom hook to fetch savings goals from the API
  //       and display each as a card with a progress bar.
  //
  // HOW:  1. Import useSavingsGoals from '../hooks/useBudgetAPI'
  //       2. Call it with a userId: const { goals, loading, error, refetch } = useSavingsGoals(1)
  //          (hardcode userId=1 for now — later you'd get this from auth)
  //       3. Import Spinner and ErrorMessage from '../components/Feedback'
  //       4. If loading, return <Spinner />
  //       5. If error, return <ErrorMessage message={error} />
  //       6. Render goals in a CSS grid layout:
  //          Use style: display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '1.2rem'
  //       7. For each goal, render a card with:
  //          - Goal name (<h3>)
  //          - Deadline date
  //          - Current amount / Target amount (formatted as currency)
  //          - Progress bar: calculate percentage = (currentAmount / targetAmount) * 100
  //            Cap at 100% using Math.min(100, percentage)
  //          - Use two nested divs for the progress bar:
  //            Outer div: className="progress-bar-bg" (the gray background)
  //            Inner div: className="progress-bar-fill" with style={{ width: `${pct}%` }}
  //
  // WHY:  CSS Grid with auto-fill and minmax creates a responsive layout that
  //       adapts to different screen sizes — 3 cards on desktop, 1 on mobile.
  //       Math.min(100, pct) prevents the progress bar from overflowing
  //       if someone contributes more than the target amount.
  //
  // OBSERVE: The page should show goal cards from the seed data.
  //          Each card should have a partially filled progress bar.
  //          Resize the browser — cards should reflow to fit the width.

  // -------------------------------------------------------
  // TODO TICKET-F103 (Day 9): Step 2 — Wire up Contribute button
  // -------------------------------------------------------
  // WHAT: Each goal card gets a "Contribute" button that opens an input field.
  //       The user enters an amount, and it's added to the goal's currentAmount.
  //
  // HOW:  1. Add state for the active goal: contributingId (which goal is being contributed to)
  //       2. Add state for the contribution amount: contributionAmount
  //       3. When "Contribute" is clicked:
  //          - Set contributingId to that goal's ID
  //          - Show a number input field and a "Submit" button
  //       4. When "Submit" is clicked:
  //          - Validate: amount must be > 0
  //          - Call PUT /api/goals/{id}/contribute with { amount: value }
  //            Use fetch with method: 'PUT', Content-Type: 'application/json'
  //          - On success: call refetch() to refresh the goals, clear contributingId
  //          - On error: show an error message
  //       5. Add a "Cancel" button to close the input without contributing
  //
  // WHY:  This demonstrates a business operation (not just CRUD).
  //       The frontend sends a partial update (just the amount), and the backend
  //       adds it to the existing value — this is different from a full PUT that
  //       replaces the entire resource.
  //
  // OBSERVE: Click Contribute on a goal with currentAmount = 500.
  //          Enter 100, click Submit. The progress bar should advance.
  //          The currentAmount should now show 600.
  //          Try entering 0 or a negative number — what happens?

  return (
    <div>
      <h1 style={{ marginBottom: '1.5rem', color: 'var(--primary)' }}>Savings Goals</h1>

      <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
        <p style={{ fontSize: '1.1rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
          Build this page in <strong>Sprint 7 (Day 8)</strong>
        </p>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          TICKET-F090: Fetch savings goals using <code>useSavingsGoals()</code> hook and display as cards with progress bars
        </p>
      </div>
    </div>
  )
}
