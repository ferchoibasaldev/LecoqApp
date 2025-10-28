import { useEffect, useMemo, useState } from "react";
import Layout from "../components/Layout";
import PageHeader from "../components/PageHeader";
import Pager from "../components/Pager";
import {
  listarMaquilados,
  obtenerMaquilado,
  crearMaquilado,
  actualizarMaquilado,
  eliminarMaquilado,
  buscarMaquiladoPorNumero,
  listarMaquiladosPorEstado,
  toDateInput,
  type MaquiladoVM,
  type MaquiladoUpsert,
} from "../api/maquilados";

const ESTADOS = ["PENDIENTE", "EN_PROCESO", "FINALIZADO", "RECIBIDO", "CANCELADO"];
const fmt = (iso?: string | null) =>
  !iso ? "-" : Number.isNaN(new Date(iso).getTime()) ? "-" : new Date(iso!).toLocaleDateString();

function EstadoBadge({ v }: { v?: string | null }) {
  const s = (v ?? "").toUpperCase();
  const base = "px-2 py-1 rounded-full text-xs border";
  const map: Record<string, string> = {
    PENDIENTE: "bg-yellow-50 text-yellow-700 border-yellow-200",
    "EN_PROCESO": "bg-blue-50 text-blue-700 border-blue-200",
    FINALIZADO: "bg-emerald-50 text-emerald-700 border-emerald-200",
    RECIBIDO: "bg-teal-50 text-teal-700 border-teal-200",
    CANCELADO: "bg-rose-50 text-rose-700 border-rose-200",
  };
  return <span className={`${base} ${map[s] ?? "bg-gray-50 text-gray-600 border-gray-200"}`}>{s || "-"}</span>;
}

export default function Maquilados() {
  const [items, setItems] = useState<MaquiladoVM[]>([]);
  const [loading, setLoading] = useState(true);

  const [q, setQ] = useState("");
  const [fEstado, setFEstado] = useState("");
  const [page, setPage] = useState(1);
  const pageSize = 8;

  // pra el modal
  const [editingId, setEditingId] = useState<number | null>(null); // null = crear
  const [saving, setSaving] = useState(false);
  const [err, setErr] = useState<string | null>(null);
  const [form, setForm] = useState<MaquiladoUpsert>({
    numeroOrden: "",
    proveedorNombre: "",
    proveedorRuc: "",
    proveedorContacto: "",
    fechaOrden: "",
    fechaEntregaEstimada: "",
    fechaEntregaReal: "",
    estado: "PENDIENTE",
    costoTotal: null,
    observaciones: "",
  });

  const load = () => {
    setLoading(true);
    listarMaquilados().then(setItems).finally(() => setLoading(false));
  };

  useEffect(load, []);

  useEffect(() => setPage(1), [q, fEstado]);

  const filtered = useMemo(() => {
    let arr = items;
    if (fEstado) arr = arr.filter(x => (x.estado ?? "").toUpperCase() === fEstado.toUpperCase());

    const t = q.toLowerCase().trim();
    if (!t) return arr;
    return arr.filter((x) =>
      [
        x.id,
        x.numeroOrden,
        x.proveedorNombre,
        x.proveedorRuc,
        x.proveedorContacto,
        x.estado,
        x.observaciones,
      ]
        .map((v) => String(v ?? "").toLowerCase())
        .some((v) => v.includes(t))
    );
  }, [q, fEstado, items]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const view = filtered.slice((page - 1) * pageSize, page * pageSize);

  function openNew() {
    setEditingId(null);
    setErr(null);
    setForm({
      numeroOrden: "",
      proveedorNombre: "",
      proveedorRuc: "",
      proveedorContacto: "",
      fechaOrden: "",
      fechaEntregaEstimada: "",
      fechaEntregaReal: "",
      estado: "PENDIENTE",
      costoTotal: null,
      observaciones: "",
    });
    (document.getElementById("dlgMaquilado") as HTMLDialogElement)?.showModal();
  }

 function toSafe(v?: string | null) { return v ?? ""; }

 async function openEdit(row: MaquiladoVM) {
   if (!row?.id) return;

   setEditingId(row.id);
   setErr(null);

   setForm({
     numeroOrden:        toSafe(row.numeroOrden),
     proveedorNombre:    toSafe(row.proveedorNombre),
     proveedorRuc:       toSafe(row.proveedorRuc),
     proveedorContacto:  toSafe(row.proveedorContacto),
     fechaOrden:         toDateInput(row.fechaOrden),
     fechaEntregaEstimada: toDateInput(row.fechaEntregaEstimada),
     fechaEntregaReal:   toDateInput(row.fechaEntregaReal),
     estado:             toSafe(row.estado || "PENDIENTE"),
     costoTotal:         row.costoTotal ?? null,
     observaciones:      toSafe(row.observaciones),
   });

   (document.getElementById("dlgMaquilado") as HTMLDialogElement)?.showModal();

   try {
     const d = await obtenerMaquilado(row.id);
     setForm(curr => ({
       ...curr,
       numeroOrden:        d.numeroOrden ?? curr.numeroOrden,
       proveedorNombre:    d.proveedorNombre ?? curr.proveedorNombre,
       proveedorRuc:       d.proveedorRuc ?? curr.proveedorRuc,
       proveedorContacto:  d.proveedorContacto ?? curr.proveedorContacto,
       fechaOrden:         toDateInput(d.fechaOrden) || curr.fechaOrden,
       fechaEntregaEstimada: toDateInput(d.fechaEntregaEstimada) || curr.fechaEntregaEstimada,
       fechaEntregaReal:   toDateInput(d.fechaEntregaReal) || curr.fechaEntregaReal,
       estado:             d.estado ?? curr.estado,
       costoTotal:         d.costoTotal ?? curr.costoTotal,
       observaciones:      d.observaciones ?? curr.observaciones,
     }));
   } catch {
     //
   }
 }

  function closeDialog() {
    (document.getElementById("dlgMaquilado") as HTMLDialogElement)?.close();
  }

  function onChange<K extends keyof MaquiladoUpsert>(key: K, val: any) {
    setForm((prev) => ({ ...prev, [key]: val }));
  }

  function validar(): string | null {
    if (!form.numeroOrden || String(form.numeroOrden).trim().length < 2)
      return "Número de orden es obligatorio.";
    if (!form.proveedorNombre || String(form.proveedorNombre).trim().length < 2)
      return "Proveedor es obligatorio.";
    if (form.costoTotal != null && Number(form.costoTotal) < 0)
      return "El costo total no puede ser negativo.";
    if (form.estado && !ESTADOS.includes(String(form.estado).toUpperCase()))
      return "Estado inválido.";
    return null;
  }

  async function onSave() {
    const v = validar();
    if (v) { setErr(v); return; }
    setSaving(true);
    try {
      const payload: MaquiladoUpsert = { ...form };
      if (editingId == null) {
        await crearMaquilado(payload);
      } else {
        await actualizarMaquilado(editingId, payload);
      }
      closeDialog();
      load();
    } catch (ex: any) {
      setErr(ex?.response?.data?.message ?? "No se pudo guardar el maquilado.");
    } finally {
      setSaving(false);
    }
  }

  async function onDelete(id: number) {
    if (!confirm("¿Eliminar este maquilado?")) return;
    try {
      await eliminarMaquilado(id);
      load();
    } catch (e) {
      alert("No se pudo eliminar.");
      console.error(e);
    }
  }

  async function onBuscarPorNumero() {
    if (!q.trim()) return;
    const one = await buscarMaquiladoPorNumero(q.trim());
    if (one) {
      setItems([one]);
      setPage(1);
    } else {
      setItems([]);
    }
  }
  async function onFiltrarPorEstado(e: React.ChangeEvent<HTMLSelectElement>) {
    const val = e.target.value;
    setFEstado(val);
    if (val) {
      setLoading(true);
      try {
        const res = await listarMaquiladosPorEstado(val);
        setItems(res);
      } finally {
        setLoading(false);
      }
    } else {
      load();
    }
  }

  return (
    <Layout>
      <PageHeader title="Maquilados" subtitle="Órdenes de producción tercerizada">
        <div className="flex items-center gap-2">
          <input
            className="border rounded-xl px-3 py-2 text-sm"
            value={q}
            placeholder="Buscar por número, proveedor, RUC…"
            onChange={(e) => setQ(e.target.value)}
          />
          <select
            className="border rounded-xl px-3 py-2 text-sm"
            value={fEstado}
            onChange={onFiltrarPorEstado}
          >
            <option value="">Todos los estados</option>
            {ESTADOS.map(s => <option key={s} value={s}>{s}</option>)}
          </select>
          <button className="px-3 py-2 rounded-xl border bg-white hover:bg-gray-50" onClick={openNew}>
            + Nuevo
          </button>
        </div>
      </PageHeader>

      {/* Tabla */}
      <div className="border rounded-2xl overflow-hidden bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-gray-50 text-gray-600">
              <tr>
                <th className="p-3 text-left">ID</th>
                <th className="p-3 text-left">Nº Orden</th>
                <th className="p-3 text-left">Proveedor</th>
                <th className="p-3 text-left">RUC</th>
                <th className="p-3 text-left">Contacto</th>
                <th className="p-3 text-left">Fecha orden</th>
                <th className="p-3 text-left">Estado</th>
                <th className="p-3 text-left">Costo</th>
                <th className="p-3 text-left">Obs.</th>
                <th className="p-3 text-left">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan={12} className="p-4">Cargando...</td></tr>
              ) : view.length === 0 ? (
                <tr><td colSpan={12} className="p-6 text-gray-500">Sin resultados.</td></tr>
              ) : (
                view.map((r) => (
                  <tr key={r.id} className="border-t">
                    <td className="p-3">{r.id}</td>
                    <td className="p-3">{r.numeroOrden}</td>
                    <td className="p-3">{r.proveedorNombre}</td>
                    <td className="p-3">{r.proveedorRuc ?? "-"}</td>
                    <td className="p-3">{r.proveedorContacto ?? "-"}</td>
                    <td className="p-3">{fmt(r.fechaOrden)}</td>
                    <td className="p-3"><EstadoBadge v={r.estado} /></td>
                    <td className="p-3">{r.costoTotal != null ? Number(r.costoTotal).toFixed(2) : "-"}</td>
                    <td className="p-3 max-w-[16rem] truncate" title={r.observaciones ?? ""}>
                      {r.observaciones ?? "-"}
                    </td>
                    <td className="p-3">
                      <div className="flex gap-2">
                        <button className="px-2 py-1 rounded-lg border hover:bg-gray-50" onClick={() => openEdit(r)}>
                          Editar
                        </button>
                        <button className="px-2 py-1 rounded-lg border hover:bg-gray-50 text-red-600"
                                onClick={() => onDelete(r.id!)}>
                          Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Pager */}
      <div className="flex items-center justify-between mt-4">
        <p className="text-sm text-gray-500">{filtered.length} resultados</p>
        <Pager page={page} setPage={setPage} totalPages={totalPages} />
      </div>

      {/* Modal Crear/Editar */}
      <dialog id="dlgMaquilado" className="rounded-2xl p-0 w-[780px] max-w-[95vw]">
        <form method="dialog" className="p-5">
          <div className="flex items-center justify-between mb-3">
            <h3 className="font-semibold text-lg">
              {editingId == null ? "Nuevo maquilado" : "Editar maquilado"}
            </h3>
            {editingId != null && (
              <span className="text-xs px-2 py-1 rounded-full bg-gray-100 border text-gray-700">
                ID: {editingId}
              </span>
            )}
          </div>

          {err && <div className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg p-3 mb-3">{err}</div>}

          <div className="grid md:grid-cols-3 gap-3">
            <div className="md:col-span-1">
              <label className="block text-sm mb-1">Nº de orden *</label>
              <input
                className="w-full border rounded-xl px-3 py-2"
                value={form.numeroOrden ?? ""}
                onChange={(e) => onChange("numeroOrden", e.target.value)}
                placeholder="MQ-2025-001"
                required
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm mb-1">Proveedor *</label>
              <input
                className="w-full border rounded-xl px-3 py-2"
                value={form.proveedorNombre ?? ""}
                onChange={(e) => onChange("proveedorNombre", e.target.value)}
                placeholder="Nombre del proveedor"
                required
              />
            </div>

            <div>
              <label className="block text-sm mb-1">RUC</label>
              <input
                className="w-full border rounded-xl px-3 py-2"
                value={form.proveedorRuc ?? ""}
                onChange={(e) => onChange("proveedorRuc", e.target.value)}
                placeholder="20xxxxxxxxxx"
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm mb-1">Contacto</label>
              <input
                className="w-full border rounded-xl px-3 py-2"
                value={form.proveedorContacto ?? ""}
                onChange={(e) => onChange("proveedorContacto", e.target.value)}
                placeholder="Nombre / teléfono / correo"
              />
            </div>

            <div>
              <label className="block text-sm mb-1">Fecha orden</label>
              <input
                type="date"
                className="w-full border rounded-xl px-3 py-2"
                value={form.fechaOrden ?? ""}
                onChange={(e) => onChange("fechaOrden", e.target.value)}
              />
            </div>

            <div>
              <label className="block text-sm mb-1">Estado</label>
              <select
                className="w-full border rounded-xl px-3 py-2"
                value={form.estado ?? "PENDIENTE"}
                onChange={(e) => onChange("estado", e.target.value)}
              >
                {ESTADOS.map((s) => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>

            <div>
              <label className="block text-sm mb-1">Costo total (S/)</label>
              <input
                type="number"
                step="0.01"
                min={0}
                className="w-full border rounded-xl px-3 py-2"
                value={form.costoTotal ?? ""}
                onChange={(e) => onChange("costoTotal", e.target.value === "" ? null : Number(e.target.value))}
                placeholder="0.00"
              />
            </div>

            <div className="md:col-span-3">
              <label className="block text-sm mb-1">Observaciones</label>
              <textarea
                className="w-full border rounded-xl px-3 py-2"
                rows={3}
                value={form.observaciones ?? ""}
                onChange={(e) => onChange("observaciones", e.target.value)}
                placeholder="Notas, especificaciones, etc."
              />
            </div>
          </div>

          <div className="mt-4 flex justify-end gap-2">
            <button type="button" className="px-3 py-1.5 rounded-lg border" onClick={closeDialog}>
              Cancelar
            </button>
            <button
              type="button"
              className="px-3 py-1.5 rounded-lg bg-black text-white disabled:opacity-60"
              disabled={saving}
              onClick={onSave}
            >
              {saving ? "Guardando..." : editingId == null ? "Crear" : "Guardar"}
            </button>
          </div>
        </form>
      </dialog>
    </Layout>
  );
}
