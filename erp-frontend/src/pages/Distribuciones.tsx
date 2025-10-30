import { useEffect, useMemo, useState } from "react";
import {
  listarDistribuciones,
  obtenerDistribucion,
  crearDistribucion,
  actualizarDistribucion,
  toDateInputValue,
  type DistribucionVM,
  type DistribucionUpsert,
} from "../api/distribuciones";
import Layout from "../components/Layout";
import PageHeader from "../components/PageHeader";
import Pager from "../components/Pager";

const ESTADOS = ["PROGRAMADO", "EN_RUTA", "ENTREGADO", "CANCELADO"];
const fmt = (iso?: string | null) =>
  !iso ? "-" : Number.isNaN(new Date(iso).getTime()) ? "-" : new Date(iso!).toLocaleDateString();

export default function Distribuciones() {
  const [items, setItems] = useState<DistribucionVM[]>([]);
  const [loading, setLoading] = useState(true);
  const [q, setQ] = useState("");
  const [page, setPage] = useState(1);

  // Modal state
  const [editingId, setEditingId] = useState<number | null>(null); // null = nuevo
  const [saving, setSaving] = useState(false);
  const [err, setErr] = useState<string | null>(null);
  const [modalLoading, setModalLoading] = useState(false);
  const [form, setForm] = useState<DistribucionUpsert>({
    pedidoId: null,
    direccionEntrega: "",
    fechaSalida: "",
    fechaEntrega: "",
    estado: "PROGRAMADO",
    choferNombre: "",
    choferTelefono: "",
    vehiculoPlaca: "",
    vehiculoModelo: "",
    observaciones: "",
  });

  const pageSize = 8;

  const load = () => {
    setLoading(true);
    listarDistribuciones().then(setItems).finally(() => setLoading(false));
  };

  useEffect(load, []);
  useEffect(() => setPage(1), [q]);

  const filtered = useMemo(() => {
    const t = q.toLowerCase().trim();
    if (!t) return items;
    return items.filter((x) =>
      [x.id, x.pedidoId, x.destino, x.estado, x.choferNombre, x.choferTelefono, x.vehiculoPlaca, x.vehiculoModelo, x.observaciones]
        .map((v) => String(v ?? "").toLowerCase())
        .some((v) => v.includes(t))
    );
  }, [q, items]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const view = filtered.slice((page - 1) * pageSize, page * pageSize);

  function openNew() {
    setEditingId(null);
    setErr(null);
    setForm({
      pedidoId: null,
      direccionEntrega: "",
      fechaSalida: "",
      fechaEntrega: "",
      estado: "PROGRAMADO",
      choferNombre: "",
      choferTelefono: "",
      vehiculoPlaca: "",
      vehiculoModelo: "",
      observaciones: "",
    });
    (document.getElementById("dlgDistribucion") as HTMLDialogElement)?.showModal();
  }

async function openEdit(row: DistribucionVM) {
  if (!row?.id) return;

  setEditingId(row.id);
  setErr(null);

  const pre = vmToForm(row);
  setForm(pre);


  (document.getElementById("dlgDistribucion") as HTMLDialogElement)?.showModal();

  try {
    setModalLoading(true);
    const d = await obtenerDistribucion(row.id);
    const refined = vmToForm(d);

    setForm(curr => ({
      ...curr,
      ...Object.fromEntries(
        Object.entries(refined).filter(([, v]) => v !== null && v !== undefined && v !== "")
      ),
    }));
  } catch (e) {
    console.warn("obtenerDistribucion falló:", e);
  } finally {
    setModalLoading(false);
  }
}

    function vmToForm(d: DistribucionVM): DistribucionUpsert {
      return {
        pedidoId: d.pedidoId ?? null,
        direccionEntrega: d.destino ?? "",
        fechaSalida: toDateInputValue(d.fechaSalida),
        fechaEntrega: toDateInputValue(d.fechaEntrega),
        estado: d.estado ?? "PROGRAMADO",
        choferNombre: d.choferNombre ?? "",
        choferTelefono: d.choferTelefono ?? "",
        vehiculoPlaca: d.vehiculoPlaca ?? "",
        vehiculoModelo: d.vehiculoModelo ?? "",
        observaciones: d.observaciones ?? "",
      };
    }


  function closeDialog() {
    (document.getElementById("dlgDistribucion") as HTMLDialogElement)?.close();
  }

  function onChange<K extends keyof DistribucionUpsert>(key: K, val: any) {
    setForm((prev) => ({ ...prev, [key]: val }));
  }

function validar(): string | null {
  if (editingId == null && (form.pedidoId == null || Number.isNaN(form.pedidoId)))
    return "Pedido (ID) es obligatorio.";
  if (!form.direccionEntrega || form.direccionEntrega.trim().length < 5)
    return "Destino/Dirección es obligatorio (mín. 5 caracteres).";
  if (!form.fechaSalida) return "Fecha de salida es obligatoria.";
  if (!form.estado || !ESTADOS.includes(String(form.estado))) return "Estado inválido.";
  if (form.choferTelefono && !/^[0-9+\s-]{6,}$/.test(form.choferTelefono)) return "Teléfono de chofer inválido.";
  return null;
}

const ISO_DATE_RE = /^\d{4}-\d{2}-\d{2}$/;
function toIsoDateTime(dateStr?: string | null): string | null {
  if (!dateStr) return null;                 // permite null
  return ISO_DATE_RE.test(dateStr) ? `${dateStr}T00:00:00` : dateStr; // ya viene ISO con hora?
}

async function onSave() {
  const v = validar();
  if (v) { setErr(v); return; }
  setSaving(true);

  try {
    const payload: DistribucionUpsert = {
      ...form,
      fechaSalida: toIsoDateTime(form.fechaSalida),
      fechaEntrega: toIsoDateTime(form.fechaEntrega),
    };

    console.log("[UI onSave] editingId=", editingId, "payload=", payload);

    if (editingId == null) {
      await crearDistribucion(payload);
    } else {
      await actualizarDistribucion(editingId, payload);
    }

    closeDialog();
    load();
  } catch (ex: any) {
    console.error("Error guardando distribución (UI catch):", {
      status: ex?.response?.status,
      data: ex?.response?.data,
      msg: ex?.message,
    });

    const serverMsg =
      ex?.response?.data?.message ||
      ex?.response?.data?.error ||
      ex?.message ||
      "No se pudo guardar la distribución.";

    setErr(serverMsg);
  } finally {
    setSaving(false);
  }
}



  return (
    <Layout>
      <PageHeader title="Distribuciones" subtitle="Control de envíos">
        <input
          className="border rounded-xl px-3 py-2 text-sm"
          value={q}
          placeholder="Buscar..."
          onChange={(e) => setQ(e.target.value)}
        />
        <button className="px-3 py-2 rounded-xl border bg-white hover:bg-gray-50" onClick={openNew}>
          + Agregar
        </button>
      </PageHeader>

      <div className="rounded-2xl overflow-hidden bg-white shadow-sm border w-full">
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-gray-50 text-gray-600">
              <tr>
                <th className="p-3 text-left">ID</th>
                <th className="p-3 text-left">Pedido</th>
                <th className="p-3 text-left">Destino</th>
                <th className="p-3 text-left">Chofer</th>
                <th className="p-3 text-left">Teléfono</th>
                <th className="p-3 text-left">Placa</th>
                <th className="p-3 text-left">Modelo</th>
                <th className="p-3 text-left">Fecha salida</th>
                <th className="p-3 text-left">Estado</th>
                <th className="p-3 text-left">Observaciones</th>
                <th className="p-3 text-left">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={11} className="p-4">
                    Cargando...
                  </td>
                </tr>
              ) : view.length === 0 ? (
                <tr>
                  <td colSpan={11} className="p-6 text-gray-500">
                    Sin resultados.
                  </td>
                </tr>
              ) : (
                view.map((r) => (
                  <tr key={r.id} className="border-t">
                    <td className="p-3">{r.id}</td>
                    <td className="p-3">{r.pedidoId ?? "-"}</td>
                    <td className="p-3">{r.destino ?? "-"}</td>
                    <td className="p-3">{r.choferNombre ?? "-"}</td>
                    <td className="p-3 whitespace-nowrap">{r.choferTelefono ?? "-"}</td>
                    <td className="p-3">{r.vehiculoPlaca ?? "-"}</td>
                    <td className="p-3">{r.vehiculoModelo ?? "-"}</td>
                    <td className="p-3">{fmt(r.fechaSalida)}</td>
                    <td className="p-3">{r.estado ?? "-"}</td>
                    <td className="p-3 max-w-[18rem] truncate" title={r.observaciones ?? ""}>
                      {r.observaciones ?? "-"}
                    </td>
                    <td className="p-3">
                      <button className="px-2 py-1 rounded-lg border hover:bg-gray-50" onClick={() => openEdit(r)}>
                        Editar
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <div className="flex items-center justify-between mt-4">
        <p className="text-sm text-gray-500">{filtered.length} resultados</p>
        <Pager page={page} setPage={setPage} totalPages={totalPages} />
      </div>

      <dialog id="dlgDistribucion" className="rounded-2xl p-0 w-[720px] max-w-[95vw]">
        <form method="dialog" className="p-5">
          <div className="flex items-center justify-between mb-3">
            <h3 className="font-semibold text-lg">
              {editingId == null ? "Nueva distribución" : `Editar distribución`}
            </h3>
            {editingId != null && (
              <span className="text-xs px-2 py-1 rounded-full bg-gray-100 border text-gray-700">
                ID: {editingId}
              </span>
            )}
          </div>

          {err && (
            <div className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg p-3 mb-3">{err}</div>
          )}

          <div className="grid md:grid-cols-2 gap-3">
            {/* NOTA: No hay campo 'id' editable */}
            <div>
              <label className="block text-sm mb-1">Pedido (ID)</label>
              <input
                type="number"
                 className="w-full border rounded-xl px-3 py-2
                               disabled:bg-gray-100 disabled:text-gray-500
                               disabled:border-gray-200 disabled:cursor-not-allowed"
                value={form.pedidoId ?? ""}
                onChange={(e) => onChange("pedidoId", e.target.value ? Number(e.target.value) : null)}
                placeholder="Ej. 1023"
                disabled={editingId !== null}
              />
            </div>

            <div>
              <label className="block text-sm mb-1">Estado</label>
              <select
                className="w-full border rounded-xl px-3 py-2"
                value={form.estado ?? "PROGRAMADO"}
                onChange={(e) => onChange("estado", e.target.value)}
              >
                {ESTADOS.map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
              </select>
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm mb-1">Destino / Dirección *</label>
              <input
                className="w-full border rounded-xl px-3 py-2"
                value={form.direccionEntrega ?? ""}
                onChange={(e) => onChange("direccionEntrega", e.target.value)}
                placeholder="Calle, número, distrito, referencia…"
                required
              />
            </div>

            <div>
              <label className="block text-sm mb-1">Fecha de salida *</label>
              <input
                type="date"
                className="w-full border rounded-xl px-3 py-2"
                value={form.fechaSalida ?? ""}
                onChange={(e) => onChange("fechaSalida", e.target.value)}
                required
              />
            </div>

            <div>
              <label className="block text-sm mb-1">Fecha de entrega</label>
              <input
                type="date"
                className="w-full border rounded-xl px-3 py-2"
                value={form.fechaEntrega ?? ""}
                onChange={(e) => onChange("fechaEntrega", e.target.value)}
              />
            </div>

            <div>
              <label className="block text-sm mb-1">Chofer</label>
              <input
                className="w-full border rounded-xl px-3 py-2"
                value={form.choferNombre ?? ""}
                onChange={(e) => onChange("choferNombre", e.target.value)}
                placeholder="Nombre completo"
              />
            </div>

            <div>
              <label className="block text-sm mb-1">Teléfono del chofer</label>
              <input
                className="w-full border rounded-xl px-3 py-2"
                value={form.choferTelefono ?? ""}
                onChange={(e) => onChange("choferTelefono", e.target.value)}
                placeholder="Ej. 999 123 456"
              />
            </div>

            <div>
              <label className="block text-sm mb-1">Placa</label>
              <input
                className="w-full border rounded-xl px-3 py-2"
                value={form.vehiculoPlaca ?? ""}
                onChange={(e) => onChange("vehiculoPlaca", e.target.value)}
                placeholder="ABC-123"
              />
            </div>

            <div>
              <label className="block text-sm mb-1">Modelo</label>
              <input
                className="w-full border rounded-xl px-3 py-2"
                value={form.vehiculoModelo ?? ""}
                onChange={(e) => onChange("vehiculoModelo", e.target.value)}
                placeholder="Camión, Minivan, etc."
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm mb-1">Observaciones</label>
              <textarea
                className="w-full border rounded-xl px-3 py-2"
                rows={3}
                value={form.observaciones ?? ""}
                onChange={(e) => onChange("observaciones", e.target.value)}
                placeholder="Notas para la entrega, referencias, etc."
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
