// src/api/pedidos.ts
import { api } from "./axios";

export type PedidoVM = {
  id: number;
  numero: string;
  cliente: string;
  fecha: string | null;  // ISO o null
  estado: string;
};

// GET /api/pedidos → ApiResponse { data: Pedido[] }
export async function listarPedidos(): Promise<PedidoVM[]> {
  const res = await api.get("/api/pedidos");
  const items = (res.data?.data ?? []) as any[];

  return items.map(p => ({
    id: p.id,
    numero: p.numeroPedido,
    cliente: p.clienteNombre,    // 👈 mapeo correcto
    fecha: p.fechaPedido ?? null, // 👈 mapeo correcto
    estado: p.estado
  }));
}

// Crear rápido (si tu backend aún no autonumera):
// Ojo: en tu BD numero_pedido es NOT NULL + UNIQUE.
// Si tu backend NO genera el número, manda uno temporal.
export async function crearPedido(input: { cliente: string }) {
  const payload = {
    numeroPedido: `WEB-${Date.now()}`,   // quítalo si tu backend lo genera
    clienteNombre: input.cliente,
    clienteRuc: null,
    clienteTelefono: null,
    clienteDireccion: null,
    fechaPedido: new Date().toISOString(),
    estado: "PENDIENTE",
    observaciones: null,
    total: 1, // evita validación si tu service recalcula solo con detalles
  };
  await api.post("/api/pedidos", payload);
}
