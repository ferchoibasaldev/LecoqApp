import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthContext";

export default function RoleGuard({
  roles,
  children,
}: { roles: Array<"ADMIN" | "VENTAS" | "MAQUILA">; children: JSX.Element }) {
  const { role } = useAuth();
  if (!role || !roles.includes(role)) return <Navigate to="/" replace />;
  return children;
}
