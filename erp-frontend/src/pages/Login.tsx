import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login as doLogin } from "../api/auth";
import { useAuth } from "../auth/AuthContext";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [u, setU] = useState("");
  const [p, setP] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const { token, role } = await doLogin(u, p);
      // Guarda SOLO el JWT (sin "Bearer ") y el rol correcto
      login(token, role);
      navigate("/", { replace: true });
    } catch (e: any) {
      setError(e?.message || "Credenciales inválidas");
    } finally {
      setLoading(false);
    }
  };


  return (
    <div className="min-h-screen grid md:grid-cols-2">
      <div className="md:block min-h-screen bg-gradient-to-b from-brand-blue via-brand-blueMid to-brand-green text-white p-12">
        <div className="max-w-md">
          <h1 className="text-3xl font-bold">ERP LECOQ</h1>
          <p className="mt-3 text-white/80">Administra productos, pedidos, distribuciones y maquilados.</p>
        </div>
      </div>

      <div className="flex items-center justify-center p-6">
        <form onSubmit={submit} className="w-full max-w-sm space-y-4 border p-6 rounded-2xl bg-white shadow-sm">
          <h2 className="text-xl font-semibold">ERP LECOQ – Ingreso</h2>
          <input className="w-full border p-2 rounded" placeholder="Usuario" value={u} onChange={(e)=>setU(e.target.value)} />
          <input className="w-full border p-2 rounded" type="password" placeholder="Contraseña" value={p} onChange={(e)=>setP(e.target.value)} />
          {error && <p className="text-red-600 text-sm">{error}</p>}
          <button disabled={loading} className="w-full p-2 rounded bg-brand-blue text-white">
            {loading ? "Validando..." : "Entrar"}
          </button>
          <p className="text-sm text-gray-500">admin/admin123 • ventas/ventas123 • maquila/maquila123</p>
        </form>
      </div>
    </div>
  );
}
