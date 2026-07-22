import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// PROVIDED – proxies /api/* to Spring Boot so no CORS errors in dev
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': { target: 'http://localhost:8080', changeOrigin: true }
    }
  }
})
