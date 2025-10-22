import { useEffect, useMemo, useState } from "react";
import Layout from "../components/Layout";
import PageHeader from "../components/PageHeader";
import Pager from "../components/Pager";
import { listarMaquilados, type MaquiladoVM } from "../api/maquilados";

const f = (iso?: string | null) => !iso ? "-" : new Date(iso).toLocaleDateString();

export default function Maquilados() {
  const [items, setItems] = useState<MaquiladoVM[]>([]);
  const [loading, setLoading] = useState(true);
  const [q, setQ] = useState("");
  const [page, setPage] = useState(1);
  const pageSize = 8;

  useEffect(() => { setLoading(true); listarMaquilados().then(setItems).finally(()=>setLoading(false)); }, []);
  useEffect(() => setPage(1), [q]);

  const filtered = useMemo(() => {
    const t = q.toLowerCase().trim();
    if (!t) return items;
    return items.filter(x =>
      [x.proveedor, x.estado, x.orden, String(x.id)]
        .some(v => String(v ?? "").toLowerCase().includes(t))
    );
  }, [q, items]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const view = filtered.slice((page - 1) * pageSize, page * pageSize);

  return (
    <Layout>
      <PageHeader title="Maquilados" subtitle="Órdenes de producción tercerizada">
        <input className="border rounded-xl px-3 py-2 text-sm" value={q} placeholder="Buscar..."
               onChange={(e) => setQ(e.target.value)} />
      </PageHeader>

      <div className="border rounded-2xl overflow-hidden bg-white shadow-sm">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50 text-gray-600">
            <tr>
              <th className="p-3">ID</th>
              <th className="p-3">Orden</th>
              <th className="p-3">Proveedor</th>
              <th className="p-3">Fecha</th>
              <th className="p-3">Estado</th>
            </tr>
          </thead>
          <tbody>
          {loading ? (
            <tr><td colSpan={5} className="p-4">Cargando...</td></tr>
          ) : view.length === 0 ? (
            <tr><td colSpan={5} className="p-6 text-gray-500">Sin resultados.</td></tr>
          ) : (
            view.map(r => (
              <tr key={r.id} className="border-t">
                <td className="p-3">{r.id}</td>
                <td className="p-3">{r.orden}</td>
                <td className="p-3">{r.proveedor || "-"}</td>
                <td className="p-3">{f(r.fecha)}</td>
                <td className="p-3">{r.estado}</td>
              </tr>
            ))
          )}
          </tbody>
        </table>
      </div>

      <div className="flex items-center justify-between mt-4">
        <p className="text-sm text-gray-500">{filtered.length} resultados</p>
        <Pager page={page} setPage={setPage} totalPages={totalPages} />
      </div>
    </Layout>
  );
}
