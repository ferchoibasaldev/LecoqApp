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
  fechaEntrega?: string | null;
  estado?: string | null;
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

const ISO_DATE_RE = /^\d{4}-\d{2}-\d{2}$/;
const toIso = (v?: string | null) =>
  !v ? null : ISO_DATE_RE.test(v) ? `${v}T00:00:00` : v;

export async function crearDistribucion(f: DistribucionUpsert) {
  if (f.pedidoId == null) {
    console.error("[CREAR] pedidoId es null/undefined");
    throw new Error("Pedido (ID) es obligatorio.");
  }

  const params = { pedidoId: f.pedidoId };

  // ⚠️ Mapeo a los nombres del backend
  const body = {
    direccionEntrega: (f.direccionEntrega ?? "").trim(),
    fechaSalida: toIso(f.fechaSalida),
    fechaEntrega: toIso(f.fechaEntrega),
    estado: f.estado,
    choferNombre: f.choferNombre || null,
    choferTelefono: f.choferTelefono || null,
    vehiculoPlaca: f.vehiculoPlaca || null,
    vehiculoModelo: f.vehiculoModelo || null,
    observaciones: f.observaciones || null,
  };

  console.log("[HTTP] POST /api/distribuciones", { params, body });

  try {
    const res = await api.post("/api/distribuciones", body, { params });
    console.log("[HTTP OK] /api/distribuciones", res.status, res.data);
    return res.data;
  } catch (error: any) {
    // logs súper explícitos
    console.error("[HTTP ERROR] POST /api/distribuciones", {
      url: error?.config?.url,
      params: error?.config?.params,
      sentBody: safeParse(error?.config?.data),
      status: error?.response?.status,
      response: error?.response?.data,
      message: error?.message,
    });
    throw error;
  }
}

function safeParse(x: any) {
  try { return typeof x === "string" ? JSON.parse(x) : x; } catch { return x; }
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

function toIsoDateTime(dateStr?: string | null) {
  if (!dateStr) return null;
  return `${dateStr}T00:00:00`;
}

