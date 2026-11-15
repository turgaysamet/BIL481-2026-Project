import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// During development, calls to /api are forwarded to the Spring Boot backend.
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: { '/api': 'http://localhost:8080' },
  },
});
