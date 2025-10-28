// src/api/distribuciones.ts
import api from "./axios";

export type DistribucionVM = {
  id?: number;
  pedidoId: number | null;
  destino: string | null;          // direccionEntrega
  fechaSalida: string | null;
  fechaEntrega?: string | null;
  estado: string | null;
  choferNombre?: string | null;
  choferTelefono?: string | null;
  vehiculoPlaca?: string | null;
  vehiculoModelo?: string | null;
  observaciones?: string | null;
};

export type DistribucionUpsert = {
  pedidoId?: number | null;
  direccionEntrega?: string | null;
  fechaSalida?: string | null;     // ISO: "YYYY-MM-DD"
  fechaEntrega?: string | null;    // ISO
  estado?: string | null;          // PROGRAMADO | EN_RUTA | ENTREGADO | CANCELADO
  choferNombre?: string | null;
  choferTelefono?: string | null;
  vehiculoPlaca?: string | null;
  vehiculoModelo?: string | null;
  observaciones?: string | null;
};


export async function listarDistribuciones(): Promise<DistribucionVM[]> {
  const { data } = await api.get("/api/distribuciones");

  const raw: any[] =
    (Array.isArray(data) && data) ||
    (Array.isArray(data?.data) && data.data) ||
    (Array.isArray(data?.content) && data.content) ||
    [];

  const mapped: DistribucionVM[] = raw.map((d: any) => ({
    id: d.id ?? d.distribucionId ?? d.codigo ?? 0,
    pedidoId: d.pedidoId ?? d.pedido?.id ?? d.pedido_id ?? null,
    destino: d.direccionEntrega ?? d.destino ?? null,
    fechaSalida: d.fechaSalida ?? d.fecha_salida ?? null,
    fechaEntrega: d.fechaEntrega ?? d.fecha_entrega ?? null,
    estado: d.estado ?? null,
    choferNombre: d.choferNombre ?? d.chofer_nombre ?? null,
    choferTelefono: d.choferTelefono ?? d.chofer_telefono ?? null,
    vehiculoPlaca: d.vehiculoPlaca ?? d.vehiculo_placa ?? null,
    vehiculoModelo: d.vehiculoModelo ?? d.vehiculo_modelo ?? null,
    observaciones: d.observaciones ?? null,
  }));

  return mapped;
}

export async function obtenerDistribucion(id: number): Promise<DistribucionVM> {
  const { data } = await api.get(`/api/distribuciones/${id}`);
  const d: any =
    (data && !Array.isArray(data) && data) ||
    (data?.data && !Array.isArray(data.data) && data.data) ||
    data;

  return {
    id: d.id ?? d.distribucionId ?? d.codigo ?? 0,
    pedidoId: d.pedidoId ?? d.pedido?.id ?? d.pedido_id ?? null,
    destino: d.direccionEntrega ?? d.destino ?? null,
    fechaSalida: d.fechaSalida ?? d.fecha_salida ?? null,
    fechaEntrega: d.fechaEntrega ?? d.fecha_entrega ?? null,
    estado: d.estado ?? null,
    choferNombre: d.choferNombre ?? d.chofer_nombre ?? null,
    choferTelefono: d.choferTelefono ?? d.chofer_telefono ?? null,
    vehiculoPlaca: d.vehiculoPlaca ?? d.vehiculo_placa ?? null,
    vehiculoModelo: d.vehiculoModelo ?? d.vehiculo_modelo ?? null,
    observaciones: d.observaciones ?? null,
  };
}

export async function crearDistribucion(payload: DistribucionUpsert) {
  const { data } = await api.post("/api/distribuciones", payload);
  return data;
}

export async function actualizarDistribucion(id: number, payload: DistribucionUpsert) {
  const { data } = await api.put(`/api/distribuciones/${id}`, payload);
  return data;
}

export async function eliminarDistribucion(id: number) {
  const { data } = await api.delete(`/api/distribuciones/${id}`);
  return data;
}

export function toDateInputValue(iso?: string | null) {
  if (!iso) return "";
  const d = new Date(iso);
  if (isNaN(d.getTime())) return (iso ?? "").slice(0, 10);
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}
