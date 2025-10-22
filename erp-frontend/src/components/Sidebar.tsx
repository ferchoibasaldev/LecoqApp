import { NavLink } from "react-router-dom";

const links = [
  { to: "/", label: "Dashboard" },
  { to: "/productos", label: "Productos" },
  { to: "/pedidos", label: "Pedidos" },
  { to: "/distribuciones", label: "Distribuciones" },
  { to: "/maquilados", label: "Maquilados" },
  { to: "/usuarios", label: "Usuarios" },
];

export default function Sidebar() {
  return (
    <aside className="w-60 border-r bg-white/50 backdrop-blur hidden md:block">
      <nav className="p-3 space-y-1">
        {links.map(l => (
          <NavLink
            key={l.to}
            to={l.to}
            className={({ isActive }) =>
              `block rounded-lg px-3 py-2 text-sm ${
                isActive ? "bg-brand-blue text-white" : "hover:bg-brand-blue-100"
              }`
            }
          >
            {l.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
