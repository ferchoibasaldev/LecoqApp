import { useState } from "react";
import { useAuth } from "../auth/AuthContext";

export default function LogoutButton() {
  const { logout } = useAuth();
  const [loading, setLoading] = useState(false);

  return (
    <button
      disabled={loading}
      onClick={async () => {
        setLoading(true);
        try {
          await logout();
        } finally {
          setLoading(false);
        }
      }}
      className="text-sm px-3 py-1.5 rounded-lg bg-brand-blue text-white disabled:opacity-60"
      title="Cerrar sesión"
    >
      {loading ? "Saliendo..." : "Cerrar sesión"}
    </button>
  );
}
