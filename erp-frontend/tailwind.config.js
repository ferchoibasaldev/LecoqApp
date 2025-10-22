/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html","./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        brand: {
          green:  "#2E7D32",
          greenDark: "#154734",
          blue:   "#0D47A1",
          blueMid:"#1565C0",
          gold:   "#EAC340",
          goldDark:"#CFAF2E",
          off:    "#F7F7F2",
        }
      },
      gradientColorStops: {
        // por si usas from-brand.* en gradients
      },
      boxShadow: {
        card: "0 6px 20px rgba(0,0,0,0.08)",
      }
    }
  },
  plugins: [],
}
