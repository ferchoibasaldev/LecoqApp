
// src/components/Topbar.tsx
import { useAuth } from "../auth/AuthContext";

export default function Topbar() {
  const { role, logout } = useAuth();

  return (
    <header className="h-14 border-b bg-white/60 backdrop-blur sticky top-0 z-30">
      <div className="h-full px-4 flex items-center justify-between">
        {/* IZQUIERDA: Logo y título */}
        <div className="flex items-center gap-3">
          <img 
            src="/logo/LECOQ.jpg" 
            alt="ERP Lecoq Logo" 
            className="h-8 w-8 sm:h-10 sm:w-10 md:h-12 md:w-12 rounded-xl object-cover"
          />
          <span className="hidden sm:block font-semibold text-gray-800 text-sm sm:text-base md:text-lg">
            ERP LECOQ
          </span>
        </div>

        {/* DERECHA: Rol y botón cerrar sesión */}
        <div className="flex items-center gap-2 sm:gap-3 text-xs sm:text-sm">
          <span className="px-2 py-1 rounded-full bg-gray-100 border text-[10px] sm:text-sm">
            Rol: {role}
          </span>
          <button 
            onClick={logout} 
            className="px-2 sm:px-3 py-1.5 rounded-lg bg-brand-blue text-white hover:bg-blue-700 transition-colors text-[10px] sm:text-sm"
          >
            Cerrar sesión
          </button>
        </div>
      </div>
    </header>
  );
}


/*
import { useAuth } from "../auth/AuthContext";

export default function Topbar() {
  const { role, logout } = useAuth();
  return (
    <header className="h-14 border-b bg-white/60 backdrop-blur sticky top-0 z-30">
      <div className="h-full px-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-xl bg-brand-blue/90" />
          <span className="font-semibold">ERP LECOQ</span>
        </div>
        <div className="flex items-center gap-3 text-sm">
          <span className="px-2 py-1 rounded-full bg-gray-100 border">Rol: {role}</span>
          <button onClick={logout} className="px-3 py-1.5 rounded-lg bg-brand-blue text-white">
            Cerrar sesión
          </button>
        </div>
      </div>
    </header>
  );
}
*/