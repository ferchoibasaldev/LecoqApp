/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html","./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        brand: {
          green:  "#043006ff",
          greenDark: "#154734",
          blue:   "#04500eff",
          blueMid:"#05410fff",
          gold:   "#EAC340",
          goldDark:"#CFAF2E",
          off:    "#bebe94ff",
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
