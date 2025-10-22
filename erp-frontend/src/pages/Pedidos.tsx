import { useEffect, useMemo, useState } from "react";
import Layout from "../components/Layout";
import PageHeader from "../components/PageHeader";
import Pager from "../components/Pager";
import { listarPedidos, crearPedido, type PedidoVM } from "../api/pedidos";

function formatDate(iso?: string | null) {
  if (!iso) return "-";
  const d = new Date(iso);
  return Number.isNaN(d.getTime()) ? "-" : d.toLocaleDateString();
}

export default function Pedidos() {
  const [items, setItems] = useState<PedidoVM[]>([]);
  const [loading, setLoading] = useState(true);
  const [q, setQ] = useState("");
  const [page, setPage] = useState(1);
  const [nuevo, setNuevo] = useState({ cliente: "" });
  const pageSize = 8;

  const load = () => {
    setLoading(true);
    listarPedidos().then(setItems).finally(() => setLoading(false));
  };

  useEffect(load, []);

  // resetea a la página 1 cuando cambie el filtro
  useEffect(() => { setPage(1); }, [q]);

  const filtered = useMemo(() => {
    const text = q.toLowerCase().trim();
    if (!text) return items;
    return items.filter(x =>
      [x.cliente, x.estado, x.numero, String(x.id)]
        .some(v => String(v ?? "").toLowerCase().includes(text))
    );
  }, [q, items]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const view = filtered.slice((page - 1) * pageSize, page * pageSize);

  return (
    <Layout>
      <PageHeader title="Pedidos" subtitle="Gestión de pedidos">
        <input
          className="border rounded-xl px-3 py-2 text-sm"
          value={q}
          placeholder="Buscar..."
          onChange={(e) => setQ(e.target.value)}
        />
        <button
          className="px-3 py-2 rounded-xl border bg-white hover:bg-gray-50"
          onClick={() => (document.getElementById("dlgNuevoPedido") as HTMLDialogElement)?.showModal()}
        >
          + Nuevo
        </button>
      </PageHeader>

    <div className="rounded-2xl overflow-hidden bg-white shadow-sm border w-full">
      <div className="overflow-x-auto">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50 text-gray-600">
            <tr>
              <th className="p-3">ID</th>
              <th className="p-3">Cliente</th>
              <th className="p-3">Fecha</th>
              <th className="p-3">Estado</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={4} className="p-4">Cargando...</td></tr>
            ) : view.length === 0 ? (
              <tr><td colSpan={4} className="p-6 text-gray-500">Sin resultados.</td></tr>
            ) : (
              view.map(r => (
                <tr key={r.id} className="border-t">
                  <td className="p-3">{r.id}</td>
                  <td className="p-3">{r.cliente || "-"}</td>
                  <td className="p-3">{formatDate(r.fecha)}</td>
                  <td className="p-3">{r.estado}</td>
                </tr>
              ))
            )}
          </tbody>
         </table>
        </div>
      </div>

      <div className="flex items-center justify-between mt-4">
        <p className="text-sm text-gray-500">{filtered.length} resultados</p>
        <Pager page={page} setPage={setPage} totalPages={totalPages}/>
      </div>

      <dialog id="dlgNuevoPedido" className="rounded-2xl p-0">
        <form method="dialog" className="p-5 w-96">
          <h3 className="font-semibold text-lg mb-3">Nuevo Pedido</h3>
          <input
            className="w-full border rounded-xl px-3 py-2 text-sm"
            placeholder="Cliente"
            value={nuevo.cliente}
            onChange={e => setNuevo({ cliente: e.target.value })}
          />
          <div className="mt-4 flex justify-end gap-2">
            <button type="button" className="px-3 py-1.5 rounded-lg border"
                    onClick={() => (document.getElementById("dlgNuevoPedido") as HTMLDialogElement)?.close()}>
              Cancelar
            </button>
            <button
              type="button"
              className="px-3 py-1.5 rounded-lg bg-black text-white"
              onClick={async () => {
                await crearPedido({ cliente: nuevo.cliente });
                (document.getElementById("dlgNuevoPedido") as HTMLDialogElement)?.close();
                setNuevo({ cliente: "" });
                load();
              }}
            >
              Guardar
            </button>
          </div>
        </form>
      </dialog>
    </Layout>
  );
}
