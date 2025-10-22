import { ReactNode } from "react";
import { NavLink } from "react-router-dom";
import LogoutButton  from "./LogoutButton";

type Props = { children: ReactNode };

export default function Layout({ children }: Props) {
  return (
    <div className="min-h-screen bg-brand-green-50 text-brand-green-900">
      {/* Topbar */}
      <header className="h-14 border-b bg-white flex items-center justify-between px-4 md:px-8">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-full bg-green-900" />
          <span className="font-semibold">ERP LECOQ</span>
        </div>
        <div className="flex items-center gap-3">
          <span className="text-xs px-2 py-1 rounded-full bg-gray-100">Rol: ADMIN</span>
          <LogoutButton />
        </div>
      </header>

      {/* 2 columnas: sidebar + contenido */}
      <div className="grid grid-cols-[240px_minmax(0,1fr)]">
        {/* Sidebar */}
        <aside className="border-r bg-white min-h-[calc(100vh-3.5rem)] p-4">
          <nav className="space-y-2">
            <Item to="/">Dashboard</Item>
            <Item to="/productos">Productos</Item>
            <Item to="/pedidos">Pedidos</Item>
            <Item to="/distribuciones">Distribuciones</Item>
            <Item to="/maquilados">Maquilados</Item>
            <Item to="/usuarios">Usuarios</Item>
          </nav>
        </aside>

        {/* Contenido: ANCHO COMPLETO */}
        <main className="min-h-[calc(100vh-3.5rem)] w-[80vw] mx-auto">
          {/* CLAVE: sin max-w. Si lo quieres limitado, cambia max-w-none por max-w-screen-2xl mx-auto */}
          <div className="px-4 md:px-8 py-6 max-w-none w-full">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
}

function Item({ to, children }: { to: string; children: ReactNode }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `block px-3 py-2 rounded-xl text-[0.95rem] ${
          isActive ? "bg-brand-blue text-white" : "text-brand-green-700 hover:bg-brand-green-50"
        }`
      }
    >
      {children}
    </NavLink>
  );
}
