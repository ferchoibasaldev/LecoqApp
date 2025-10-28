// src/api/productos.ts
import api from "./axios";

export type ProductoVM = {
  id?: number;
  nombre: string;
  descripcion?: string;
  stock: number;
  precio: number;
  estado?: string;
  presentacion?: string;
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
    descripcion: p.descripcion,
    stock: p.stock ?? p.stockDisponible ?? p.cantidad ?? 0,
    precio: p.precio ?? p.precioUnitario ?? p.precioVenta ?? 0,
    presentacion: p.presentacion,
    estado: p.estado ?? (p.activo === false ? "INACTIVO" : "ACTIVO"),
  }));

  return mapped;
}
export async function crearProducto(producto: ProductoVM) {
  const { data } = await api.post("/api/productos", producto);
  return data;
}
export async function eliminarProducto(id: number) {
  const { data } = await api.delete(`/api/productos/${id}`);
  return data;
}
export async function actualizarProducto(producto: ProductoVM) {
  const { data } = await api.put(`/api/productos/${producto.id}`, producto);
  return data;
}