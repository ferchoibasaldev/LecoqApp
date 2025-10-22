// src/api/productos.ts
import api from "./axios";

export type ProductoVM = {
  id: number;
  nombre: string;
  stock: number;
  precio: number;
  estado?: string;
};

export async function listarProductos(): Promise<ProductoVM[]> {
  const { data } = await api.get("/api/productos");

  const raw: any[] =
    (Array.isArray(data) && data) ||
    (Array.isArray(data?.data) && data.data) ||
    (Array.isArray(data?.content) && data.content) ||
    [];

  const mapped: ProductoVM[] = raw.map((p: any) => ({
    id: p.id ?? p.productoId ?? p.codigo ?? 0,
    nombre: p.nombre ?? p.nombreProducto ?? p.descripcion ?? "-",
    stock: p.stock ?? p.stockDisponible ?? p.cantidad ?? 0,
    precio: p.precio ?? p.precioUnitario ?? p.precioVenta ?? 0,
    estado: p.estado ?? (p.activo === false ? "INACTIVO" : "ACTIVO"),
  }));

  return mapped;
}
