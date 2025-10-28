import api from "./axios";

export type MaquiladoVM = {
  id?: number;
  numeroOrden: string;
  proveedorNombre: string;
  proveedorRuc?: string | null;
  proveedorContacto?: string | null;
  fechaOrden: string | null;              // ISO
  fechaEntregaEstimada?: string | null;   // ISO
  fechaEntregaReal?: string | null;       // ISO
  estado: string;                         // PENDIENTE | EN_PROCESO | FINALIZADO | RECIBIDO | CANCELADO
  costoTotal?: number | null;
  observaciones?: string | null;
};

export type MaquiladoUpsert = {
  numeroOrden?: string | null;
  proveedorNombre?: string | null;
  proveedorRuc?: string | null;
  proveedorContacto?: string | null;
  fechaOrden?: string | null;             // "YYYY-MM-DD"
  fechaEntregaEstimada?: string | null;   // "YYYY-MM-DD"
  fechaEntregaReal?: string | null;       // "YYYY-MM-DD"
  estado?: string | null;
  costoTotal?: number | null;
  observaciones?: string | null;
};

function mapOne(m: any): MaquiladoVM {
  return {
    id: m.id ?? m.maquiladoId ?? m.codigo ?? 0,
    numeroOrden: m.numeroOrden ?? m.orden ?? "-",
    proveedorNombre: m.proveedorNombre ?? m.proveedor ?? "-",
    proveedorRuc: m.proveedorRuc ?? m.ruc ?? null,
    proveedorContacto: m.proveedorContacto ?? m.contacto ?? null,
    fechaOrden: m.fechaOrden ?? m.fecha ?? null,
    fechaEntregaEstimada: m.fechaEntregaEstimada ?? m.entregaEstimada ?? null,
    fechaEntregaReal: m.fechaEntregaReal ?? m.entregaReal ?? null,
    estado: m.estado ?? "-",
    costoTotal: m.costoTotal ?? m.total ?? null,
    observaciones: m.observaciones ?? null,
  };
}

export async function listarMaquilados(): Promise<MaquiladoVM[]> {
  const { data } = await api.get("/api/maquilados");
  const raw: any[] =
    (Array.isArray(data) && data) ||
    (Array.isArray(data?.data) && data.data) ||
    (Array.isArray(data?.content) && data.content) ||
    [];
  return raw.map(mapOne);
}

export async function obtenerMaquilado(id: number): Promise<MaquiladoVM> {
  const { data } = await api.get(`/api/maquilados/${id}`);
  const m: any = (data?.data && !Array.isArray(data.data) ? data.data : data) ?? {};
  return mapOne(m);
}

export async function buscarMaquiladoPorNumero(numero: string): Promise<MaquiladoVM | null> {
  const { data } = await api.get(`/api/maquilados/numero/${encodeURIComponent(numero)}`);
  const m: any = (data?.data && !Array.isArray(data.data) ? data.data : data) ?? null;
  return m ? mapOne(m) : null;
}

export async function listarMaquiladosPorEstado(estado: string): Promise<MaquiladoVM[]> {
  const { data } = await api.get(`/api/maquilados/estado/${encodeURIComponent(estado)}`);
  const raw: any[] =
    (Array.isArray(data) && data) ||
    (Array.isArray(data?.data) && data.data) ||
    [];
  return raw.map(mapOne);
}

export async function crearMaquilado(payload: MaquiladoUpsert) {
  const { data } = await api.post("/api/maquilados", payload);
  return data;
}
export async function actualizarMaquilado(id: number, payload: MaquiladoUpsert) {
  const { data } = await api.put(`/api/maquilados/${id}`, payload);
  return data;
}
export async function eliminarMaquilado(id: number) {
  const { data } = await api.delete(`/api/maquilados/${id}`);
  return data;
}

export function toDateInput(iso?: string | null) {
  if (!iso) return "";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return (iso ?? "").slice(0, 10);
  const p = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`;
}
