import { createContext, useContext, useState, type ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import { setAuthToken } from "../api/axios";
import { logoutRequest } from "../api/auth";

type AuthState = { token: string | null; role: string | null; user?: any | null };
type AuthCtx = AuthState & { login: (t: string, r: string, u?: any)=>void; logout: ()=>Promise<void> };

const Ctx = createContext<AuthCtx | null>(null);
export const useAuth = () => useContext(Ctx)!;  // <<— ESTO exporta useAuth

export function AuthProvider({ children }: { children: ReactNode }) {
  const navigate = useNavigate();
  const [state, setState] = useState<AuthState>(() => ({
    token: localStorage.getItem("token"),
    role: localStorage.getItem("role"),
    user: null,
  }));

  const login = (token: string, role: string, user?: any) => {
    setAuthToken(token);
    localStorage.setItem("role", role);
    setState({ token, role, user });
  };

  const logout = async () => {
    await logoutRequest();
    setState({ token: null, role: null, user: null });
    navigate("/login", { replace: true });
  };

  return <Ctx.Provider value={{ ...state, login, logout }}>{children}</Ctx.Provider>;
}
