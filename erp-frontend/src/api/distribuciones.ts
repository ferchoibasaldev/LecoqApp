import { api } from "./axios";

export type DistribucionVM = {
  id: number;
  pedidoId: number;
  destino: string;
  fecha: string | null;   // usamos fechaSalida como “fecha”
  estado: string;
};

export async function listarDistribuciones(): Promise<DistribucionVM[]> {
  const res = await api.get("/api/distribuciones");
  const items = (res.data?.data ?? []) as any[];

  return items.map(d => ({
    id: d.id,
    pedidoId: d.pedido?.id ?? d.pedido_id ?? 0,
    destino: d.direccionEntrega,
    fecha: d.fechaSalida ?? d.fechaEntrega ?? null,
    estado: d.estado,
  }));
}
