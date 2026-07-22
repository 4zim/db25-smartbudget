import { useState, useEffect, useCallback } from 'react'

// ============================================================
// TICKET-F091 (Day 8, Sprint 7) — Custom React Hooks for API Calls
// ============================================================
//
// WHAT: Custom hooks are reusable functions that encapsulate React logic.
//       Each hook below handles a specific API call and manages three states:
//         - data (the fetched result — transactions, goals, or categories)
//         - loading (true while the fetch is in progress)
//         - error (an error message if the fetch failed)
//       Custom hooks MUST start with "use" — this is a React naming convention.
//       React treats functions starting with "use" specially.
//
// WHY:  Without custom hooks, every page component would repeat the same
//       fetch + loading + error logic. That violates DRY (Don't Repeat Yourself).
//       With hooks, each page simply calls: const { data, loading, error } = useMyHook()
//       and the hook handles everything internally.
//
// KEY CONCEPTS:
//   useState()    → Creates a state variable that triggers re-render when changed
//   useEffect()   → Runs code AFTER the component renders (side effects like API calls)
//   useCallback() → Memoizes a function so it doesn't get recreated on every render
//   fetch()       → The browser's built-in API for making HTTP requests
//   async/await   → Modern JavaScript syntax for handling asynchronous operations
//
// HOW HOOKS WORK TOGETHER:
//   1. Component mounts → useEffect runs → calls fetchData()
//   2. fetchData() sets loading=true, calls the API, sets data, sets loading=false
//   3. Component re-renders with the new data
//   4. If the API fails, error is set instead of data
//   5. refetch() allows the component to manually trigger a re-fetch (e.g., after a delete)
//
// ============================================================

// -------------------------------------------------------
// TODO TICKET-F091: Step 1 — Implement useTransactionData()
// -------------------------------------------------------
// WHAT: Fetches ALL transactions from GET /api/transactions.
//       Returns { transactions, loading, error, refetch }.
//
// HOW:  Inside the function:
//       1. Create three state variables using useState:
//          - transactions (starts as empty array [])
//          - loading (starts as true)
//          - error (starts as null)
//       2. Create a fetchData function wrapped in useCallback:
//          - Set loading to true and error to null
//          - Use fetch('/api/transactions') to call the API
//          - If the response is OK, parse the JSON and set transactions
//          - If the response is NOT OK, set error to a message
//          - Use try/catch to handle network errors (fetch fails if server is down)
//          - Set loading to false in a finally block
//       3. Call fetchData inside useEffect with [fetchData] as the dependency array
//          (this means: "run fetchData when the component mounts")
//       4. Return an object: { transactions, loading, error, refetch: fetchData }
//
// WHY:  useCallback prevents fetchData from being recreated on every render.
//       Without it, useEffect would see a "new" function each time and re-fetch
//       in an infinite loop. The dependency array [fetchData] ensures useEffect
//       only runs when fetchData actually changes (which is never, thanks to useCallback).
//
// OBSERVE: Import this hook in TransactionList.jsx. While loading, you should see
//          a spinner. After loading, you should see transaction data.
//          If the backend is down, you should see an error message.
export function useTransactionData() {
  // TODO TICKET-F091: Implement as described above
}

// -------------------------------------------------------
// TODO TICKET-F091: Step 2 — Implement useSavingsGoals(userId)
// -------------------------------------------------------
// WHAT: Fetches savings goals for a specific user from GET /api/goals/user/{userId}.
//       Returns { goals, loading, error, refetch }.
//
// HOW:  Same pattern as useTransactionData, but:
//       1. Accept a userId parameter
//       2. Fetch from `/api/goals/user/${userId}` (template literal with backticks)
//       3. Use [userId] in the useCallback dependency array — so if userId changes,
//          the hook re-fetches goals for the new user
//       4. State variable should be called "goals" instead of "transactions"
//
// WHY:  The SavingsGoals page needs to show goals for the currently logged-in user.
//       Passing userId as a parameter makes the hook flexible — it works for any user.
//
// OBSERVE: Import in SavingsGoals.jsx with userId=1.
//          Should show the seed goals for user 1 from the database.
export function useSavingsGoals(userId) {
  // TODO TICKET-F091: Implement as described above
}

// -------------------------------------------------------
// TODO TICKET-F091: Step 3 — Implement useCategories()
// -------------------------------------------------------
// WHAT: Fetches all categories from GET /api/categories.
//       Returns just the categories array (simpler than the other hooks).
//
// HOW:  1. Create a state variable: categories (starts as empty array)
//       2. Use useEffect to fetch '/api/categories' when the component mounts
//       3. Parse the JSON and set categories
//       4. Return the categories array directly
//       This hook is simpler because categories rarely change — no loading/error
//       states needed (though you can add them for polish).
//
// WHY:  The AddTransactionForm needs a list of categories for its dropdown.
//       Without this hook, you'd hardcode categories or repeat fetch logic in the form.
//
// OBSERVE: Import in AddTransactionForm.jsx.
//          The category dropdown should populate with Salary, Groceries, Rent, etc.
export function useCategories() {
  // TODO TICKET-F091: Implement as described above
}
