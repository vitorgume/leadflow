/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Nossa paleta personalizada (opcional, mas recomendada para o código que te passei)
        primary: { DEFAULT: '#4f46e5', hover: '#4338ca', light: '#e0e7ff' },
        secondary: { DEFAULT: '#10b981', hover: '#059669' },
        background: '#f8fafc',
      }
    },
  },
  plugins: [],
}