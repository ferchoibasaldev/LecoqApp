import { useEffect, useMemo, useState } from "react";
import { listarProductos } from "../api/productos";
import type { ProductoVM } from "../api/productos";   // 👈 importa el tipo
import Layout from "../components/Layout";
import PageHeader from "../components/PageHeader";


export default function Productos() {
  const [items, setItems] = useState<ProductoVM[]>([]);
  const [loading, setLoading] = useState(true);
  const [q, setQ] = useState("");
  const [page, setPage] = useState(1);
  const pageSize = 8;

  useEffect(() => {
    setLoading(true);
    listarProductos()
      .then((arr) => setItems(Array.isArray(arr) ? arr : []))
      .catch((e) => {
        console.error("Error listando productos:", e);
        setItems([]);
      })
      .finally(() => setLoading(false));
  }, []);

  const filtered = useMemo(() => {
    const base = Array.isArray(items) ? items : [];
    const text = q.trim().toLowerCase();
    if (!text) return base;
    return base.filter((p) =>
      [p.nombre, String(p.id), String(p.precio)]
        .some(x => String(x ?? "").toLowerCase().includes(text))
    );
  }, [q, items]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const view = filtered.slice((page - 1) * pageSize, page * pageSize);

  useEffect(() => {
    if (page > totalPages) setPage(1);
  }, [totalPages, page]);

  return (
    <Layout>
      <PageHeader title="Productos" subtitle="Catálogo e inventario">
        <div className="flex items-center gap-2">
          <input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="Buscar por nombre, id..."
            className="border rounded-xl px-3 py-2 text-sm"
          />
          <button className="px-3 py-2 rounded-xl border bg-white hover:bg-gray-50">
            + Nuevo
          </button>
        </div>
      </PageHeader>

      <div className="border rounded-2xl overflow-hidden bg-white shadow-sm">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50 text-gray-600">
            <tr>
              <th className="p-3 text-left w-20">ID</th>
              <th className="p-3 text-left">Nombre</th>
              <th className="p-3 text-left w-28">Stock</th>
              <th className="p-3 text-left w-32">Precio</th>
              <th className="p-3 text-left w-32">Estado</th>
              <th className="p-3 text-left w-36">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td className="p-4" colSpan={6}>Cargando...</td></tr>
            ) : view.length === 0 ? (
              <tr><td className="p-6 text-gray-500" colSpan={6}>Sin resultados.</td></tr>
            ) : (
              view.map((p) => (
                <tr key={p.id} className="border-t">
                  <td className="p-3">{p.id}</td>
                  <td className="p-3">{p.nombre}</td>
                  <td className="p-3">
                    <span className={`px-2 py-1 rounded-lg border ${
                      p.stock <= 5 ? "bg-red-50 border-red-200 text-red-700" :
                      p.stock <= 20 ? "bg-amber-50 border-amber-200 text-amber-700" :
                      "bg-emerald-50 border-emerald-200 text-emerald-700"
                    }`}>
                      {p.stock}
                    </span>
                  </td>
                  <td className="p-3">S/ {Number(p.precio).toFixed(2)}</td>
                  <td className="p-3">
                    <span className={`px-2 py-1 rounded-lg text-xs border ${
                      p.estado === "INACTIVO"
                        ? "bg-gray-50 border-gray-200 text-gray-600"
                        : "bg-blue-50 border-blue-200 text-blue-700"
                    }`}>
                      {p.estado ?? "ACTIVO"}
                    </span>
                  </td>
                  <td className="p-3">
                    <div className="flex gap-2">
                      <button className="px-2 py-1 rounded-lg border hover:bg-gray-50">Editar</button>
                      <button className="px-2 py-1 rounded-lg border hover:bg-gray-50">Eliminar</button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div className="flex items-center justify-between mt-4">
        <p className="text-sm text-gray-500">
          {filtered.length} resultados · Página {page} de {totalPages}
        </p>
        <div className="flex gap-2">
          <button
            onClick={() => setPage((p) => Math.max(1, p - 1))}
            disabled={page === 1}
            className="px-3 py-1.5 rounded-lg border bg-white disabled:opacity-50"
          >
            Anterior
          </button>
          <button
            onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
            disabled={page === totalPages}
            className="px-3 py-1.5 rounded-lg border bg-white disabled:opacity-50"
          >
            Siguiente
          </button>
        </div>
      </div>
    </Layout>
  );
}
