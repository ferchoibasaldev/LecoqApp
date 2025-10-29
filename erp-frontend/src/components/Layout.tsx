import type { ReactNode } from "react";
import { NavLink } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { MENU_ITEMS } from "../config/menuItems/menuItems";
import LogoutButton from "./LogoutButton";

type Props = { children: ReactNode };

export default function Layout({ children }: Props) {
  const { role } = useAuth();

  //  Si el rol no está definido aún (por ejemplo, antes de login)
  const visibleItems = MENU_ITEMS[role as keyof typeof MENU_ITEMS] || [];

  return (
    <div className="min-h-screen bg-brand-green-50 text-brand-green-900">
      {/* 🔝 Topbar */}
      <header className="h-14 border-b bg-white flex items-center justify-between px-4 md:px-8">
        <div className="flex items-center gap-3">
          <img
            src="/logo/LECOQ.jpg"
            alt="ERP Lecoq Logo"
            className="h-8 w-8 sm:h-10 sm:w-10 md:h-12 md:w-12 rounded-xl object-cover"
          />
          <span className="font-semibold text-gray-800 text-sm sm:text-base md:text-lg">
            ERP LECOQ
          </span>
        </div>

        <div className="flex items-center gap-3 text-xs sm:text-sm">
          <span className="px-2 py-1 rounded-full bg-gray-100 border">
            Rol: {role || "Desconocido"}
          </span>
          <LogoutButton />
        </div>
      </header>

      {/* 🧩 Sidebar + contenido */}
      <div className="grid grid-cols-[240px_minmax(0,1fr)]">
        {/* Sidebar */}
        <aside className="border-r bg-white min-h-[calc(100vh-3.5rem)] p-4">
          <nav className="space-y-2">
            {visibleItems.length > 0 ? (
              visibleItems.map((item) => (
                <Item key={item.to} to={item.to}>
                  {item.label}
                </Item>
              ))
            ) : (
              <p className="text-sm text-gray-500">Sin opciones disponibles</p>
            )}
          </nav>
        </aside>

        {/* Contenido */}
        <main className="min-h-[calc(100vh-3.5rem)] w-[80vw] mx-auto">
          <div className="px-4 md:px-8 py-6 max-w-none w-full">{children}</div>
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
          isActive
            ? "bg-brand-blue text-white"
            : "text-brand-green-700 hover:bg-brand-green-50"
        }`
      }
    >
      {children}
    </NavLink>
  );
}
