import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Navbar            from './components/Navbar'
import Dashboard         from './pages/Dashboard'
import TransactionList   from './pages/TransactionList'
import AddTransactionForm from './pages/AddTransactionForm'
import SavingsGoals      from './pages/SavingsGoals'

/**
 * PROVIDED – fully working router with 4 routes.
 *
 * TICKET-F083 (Day 8): Your task is NOT to rewrite this.
 * Your task is to make each page fetch REAL data from the API
 * by implementing the custom hooks in hooks/useBudgetAPI.js
 */
export default function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <main className="main-content">
        <Routes>
          <Route path="/"             element={<Dashboard />} />
          <Route path="/transactions" element={<TransactionList />} />
          <Route path="/add"          element={<AddTransactionForm />} />
          <Route path="/savings"      element={<SavingsGoals />} />
        </Routes>
      </main>
    </BrowserRouter>
  )
}
