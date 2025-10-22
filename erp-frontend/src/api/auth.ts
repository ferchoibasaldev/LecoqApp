import api, { setAuthToken } from "./axios";

type Role = "ADMIN" | "VENTAS" | "MAQUILA";
type BackendLogin = {
  message?: string;
  data?: {
    token?: string;
    type?: string;
    rol?: Role;
    username?: string;
    id?: number;
  };
};

export async function login(user: string, pass: string) {
  const { data } = await api.post<BackendLogin>("/api/auth/login", {
    username: user,
    password: pass,
  });

  const raw = data?.data?.token ?? "";
  const token = raw.startsWith("Bearer ") ? raw.slice(7) : raw;

  if (!token || token.split(".").length !== 3) {
    throw new Error("El backend no retornó un JWT válido.");
  }

  const role = (data?.data?.rol as Role) ?? "ADMIN";
  return { token, role };
}

export async function logoutRequest() {
  try {
    await api.post("/api/auth/logout"); // x si el backend no requiere
  } catch {
    // se pude agregar un log
  } finally {
    setAuthToken(null);
    localStorage.removeItem("role");
    localStorage.removeItem("user"); // x si guarda algo más
  }
}