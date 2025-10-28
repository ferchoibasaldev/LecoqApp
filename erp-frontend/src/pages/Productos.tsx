import { useEffect, useMemo, useState } from "react";
import { listarProductos,crearProducto, eliminarProducto, actualizarProducto} from "../api/productos";
import type { ProductoVM } from "../api/productos";   // 👈 importa el tipo
import Layout from "../components/Layout";
import PageHeader from "../components/PageHeader";

export default function Productos() {
  const [items, setItems] = useState<ProductoVM[]>([]);
  const [loading, setLoading] = useState(true);
  const [q, setQ] = useState("");
  const [page, setPage] = useState(1);
  const pageSize = 8;
  const [showForm, setShowForm] = useState(false);//Variable para mostrar form crear
  const [editando, setEditando] = useState<ProductoVM | null>(null);

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

  async function handleEliminarProducto(id: number) {
    if (!window.confirm("¿Seguro que deseas eliminar este producto?")) return;

    try {
      await eliminarProducto(id);
      const nuevos = await listarProductos();
      setItems(nuevos);
      alert("🗑️ Producto eliminado correctamente");
    } catch (err) {
      console.error("❌ Error eliminando producto:", err);
      alert("Error al eliminar producto");
    }
  }
  async function handleCrearProducto(e: React.FormEvent < HTMLFormElement > ) {
      e.preventDefault();
      const form = e.currentTarget;
      const formData = new FormData(form);

      const nuevoProducto = {
          nombre: formData.get("nombre") as string,
          descripcion: formData.get("descripcion") as string,
          precio: parseFloat(formData.get("precio") as string),
          stock: parseInt(formData.get("stock") as string),
          estado: "ACTIVO",
          presentacion: formData.get("presentacion") as string,
      };

      try {
          await crearProducto(nuevoProducto);
          const nuevos = await listarProductos();
          setItems(nuevos);
          setShowForm(false);
      } catch (err) {
          console.error("❌ Error al crear producto:", err);
          alert("❌ Error al crear producto");
      }
  }
  async function handleEditarProducto(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (!editando) return;

    const form = e.currentTarget;
    const formData = new FormData(form);

    const productoActualizado: ProductoVM = {
      ...editando,
      nombre: formData.get("nombre") as string,
      descripcion: formData.get("descripcion") as string,
      precio: parseFloat(formData.get("precio") as string),
      stock: parseInt(formData.get("stock") as string),
      estado: formData.get("estado") as string || "ACTIVO",
      presentacion: formData.get("presentacion") as string
    };

    try {
      await actualizarProducto(productoActualizado);
      const nuevos = await listarProductos();
      setItems(nuevos);
      alert("✅ Producto actualizado correctamente");
      setEditando(null);
    } catch (err) {
      console.error("❌ Error al actualizar producto:", err);
      alert("Error al actualizar producto");
    }
  }
  return (
    <Layout>
      <PageHeader title="Productos" subtitle="Catálogo e inventario">
        <div className="flex items-center gap-2">
          <input value={q} onChange={(e) => setQ(e.target.value)} placeholder="Buscar por nombre, id..." className="border rounded-xl px-3 py-2 text-sm"/>
          <button onClick={()=>setShowForm(true) } className="px-3 py-2 rounded-xl border bg-white hover:bg-gray-50" >
            + Nuevo
          </button>
        </div>
      </PageHeader>
      {/*Listado*/}

      <div className="border rounded-2xl overflow-hidden bg-white shadow-sm">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50 text-gray-600">
            <tr>
              <th className="p-3 text-left w-30">ID</th>
              <th className="p-3 text-left w-30">Nombre</th>
              <th className="p-3 text-left w-30">Descripción</th>
              <th className="p-3 text-left w-32">Presentación</th>
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
                  <td className="p-3">{p.descripcion}</td>
                  <td className="p-3">{p.presentacion}</td>
                  <td className="p-3">
                    <span className={`px-2 py-1 rounded-lg border ${//Colores de stock
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
                      <button onClick={()=> setEditando(p)} className="px-2 py-1 rounded-lg border hover:bg-gray-50">Editar</button>
                      <button  onClick={() => p.id && handleEliminarProducto(p.id)}
                        className="px-2 py-1 rounded-lg border hover:bg-gray-50 text-red-600">Eliminar</button>
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
      {/**Formulario de creacion*/}
      {
        showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div className="bg-white p-6 rounded-2xl shadow-xl w-full max-w-md">
              <h2 className="text-xl font-semibold mb-4">Nuevo producto</h2>

              <form onSubmit={handleCrearProducto}
                className="space-y-4">
                <div>
                  <label className="block text-sm font-medium mb-1">Nombre</label>
                  <input name="nombre" required className="w-full border rounded-lg px-3 py-2"/>
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Descripción</label>
                  <input name="descripcion" required className="w-full border rounded-lg px-3 py-2"/>
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Precio</label>
                  <input name="precio" type="number" step="0.01" required className="w-full border rounded-lg px-3 py-2"/>
                </div>

                <div>
                  <label className="block text-sm font-medium mb-1">Stock</label>
                  <input name="stock" type="number" required className="w-full border rounded-lg px-3 py-2"/>
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Presentación</label>
                  <input name="presentacion" required className="w-full border rounded-lg px-3 py-2"/>
                </div>
                <div className="flex justify-end gap-2 mt-4">
                  <button
                    type="button" onClick={() => setShowForm(false)} className="px-4 py-2 rounded-lg border bg-gray-100 hover:bg-gray-200">
                    Cancelar
                  </button>
                  <button type="submit" className="px-4 py-2 rounded-lg border bg-blue-600 text-white hover:bg-blue-700"          >
                    Guardar
                  </button>
                </div>
              </form>
            </div>
          </div>
        )
      }
      {/**Formulario de edicion */}
      {editando && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-2xl shadow-xl w-full max-w-md">
            <h2 className="text-xl font-semibold mb-4">Editar producto</h2>

            <form onSubmit={handleEditarProducto} className="space-y-4">
              <div> 
                <label className="block text-sm font-medium mb-1">Nombre</label> 
                <input name="nombre" defaultValue={editando.nombre} required className="w-full border rounded-lg px-3 py-2"/>
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Descripción</label>
                <input
                  name="descripcion"
                  defaultValue={editando.descripcion}
                  required
                  className="w-full border rounded-lg px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Precio</label>
                <input
                  name="precio"
                  type="number"
                  step="0.01"
                  defaultValue={editando.precio}
                  required
                  className="w-full border rounded-lg px-3 py-2"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Stock</label>
                <input
                  name="stock"
                  type="number"
                  defaultValue={editando.stock}
                  required
                  className="w-full border rounded-lg px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Presentación</label>
                <input
                  name="presentacion"
                  defaultValue={editando.presentacion}
                  required
                  className="w-full border rounded-lg px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Estado</label>
                <select
                  name="estado"
                  defaultValue={editando.estado ?? "ACTIVO"}
                  className="w-full border rounded-lg px-3 py-2"
                >
                  <option value="ACTIVO">ACTIVO</option>
                  <option value="INACTIVO">INACTIVO</option>
                </select>
              </div>

              <div className="flex justify-end gap-2 mt-4">
                <button
                  type="button"
                  onClick={() => setEditando(null)}
                  className="px-4 py-2 rounded-lg border bg-gray-100 hover:bg-gray-200"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-lg border bg-blue-600 text-white hover:bg-blue-700"
                >
                  Actualizar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </Layout>

  );
}