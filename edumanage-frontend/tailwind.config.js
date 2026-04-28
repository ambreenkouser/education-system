/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: { DEFAULT: '#3b82f6', dark: '#1d4ed8' },
        sidebar: '#1e293b',
      },
    },
  },
  plugins: [],
}

