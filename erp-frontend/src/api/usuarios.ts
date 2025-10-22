import api from "./axios";
export type Role = "ADMIN" | "VENTAS" | "MAQUILA";

export type Usuario = {
  id: number;
  username: string;
  email?: string;
  rol: Role;
  estado?: string;
};

export async function listarUsuarios(): Promise<Usuario[]> {
  const { data } = await api.get("/api/usuarios");
  const raw: any[] =
    (Array.isArray(data) && data) || (Array.isArray(data?.data) && data.data) ||
    (Array.isArray(data?.content) && data.content) || [];
  return raw.map((x: any) => ({
    id: x.id ?? x.usuarioId ?? 0,
    username: x.username ?? x.usuario ?? "-",
    email: x.email ?? x.correo,
    rol: (x.rol ?? x.role ?? "VENTAS") as Role,
    estado: x.estado ?? (x.activo === false ? "INACTIVO" : "ACTIVO"),
  }));
}

export async function crearUsuario(payload: { username: string; password: string; rol: Role; email?: string }) {
  const { data } = await api.post("/api/usuarios", payload);
  return data;
}
