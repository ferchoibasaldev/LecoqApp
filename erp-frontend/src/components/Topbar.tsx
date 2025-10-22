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
