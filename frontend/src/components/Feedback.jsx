import { useEffect } from 'react'

/** PROVIDED – reusable UI feedback components */

export function Spinner() {
  return <div className="spinner-wrapper"><div className="spinner" /></div>
}

export function ErrorMessage({ message }) {
  return <p style={{ color: 'var(--danger)', padding: '1rem', textAlign: 'center' }}>[Warning] {message}</p>
}

export function Toast({ message, type = 'success', onClose }) {
  useEffect(() => {
    const t = setTimeout(onClose, 2500)
    return () => clearTimeout(t)
  }, [onClose])
  return <div className={`toast toast--${type}`}>{message}</div>
}

/** Yellow banner shown on pages still using mock data */
export function TodoBanner({ ticket, task }) {
  return (
    <div className="todo-banner">
      [TODO] <strong>{ticket}:</strong> {task}
    </div>
  )
}
