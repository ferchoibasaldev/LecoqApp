import { api } from "./axios";

export type MaquiladoVM = {
  id: number;
  orden: string;
  proveedor: string;
  fecha: string | null;   // fechaOrden
  estado: string;
};

export async function listarMaquilados(): Promise<MaquiladoVM[]> {
  const res = await api.get("/api/maquilados");
  const items = (res.data?.data ?? []) as any[];

  return items.map(m => ({
    id: m.id,
    orden: m.numeroOrden,
    proveedor: m.proveedorNombre,
    fecha: m.fechaOrden ?? null,
    estado: m.estado,
  }));
}
