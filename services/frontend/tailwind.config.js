/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Nossa paleta personalizada (azul do logo)
        primary: { DEFAULT: '#2563EB', hover: '#1D4ED8', light: '#EFF6FF' },
        secondary: { DEFAULT: '#10b981', hover: '#059669' },
        background: '#f8fafc',
      }
    },
  },
  plugins: [],
}