import { useEffect, useMemo, useState } from "react";
import Layout from "../components/Layout";
import PageHeader from "../components/PageHeader";
import Pager from "../components/Pager";
import { listarUsuarios, crearUsuario, type Usuario } from "../api/usuarios";

export default function Usuarios() {
  const [items, setItems] = useState<Usuario[]>([]);
  const [loading, setLoading] = useState(true);
  const [q, setQ] = useState("");
  const [page, setPage] = useState(1);
  const [nuevo, setNuevo] = useState<{username:string; password:string; rol:"ADMIN"|"VENTAS"|"MAQUILA"; email?:string}>({
    username:"", password:"", rol:"VENTAS", email:""
  });
  const pageSize = 8;

  const load = () => { setLoading(true); listarUsuarios().then(setItems).finally(()=>setLoading(false)); };
  useEffect(load, []);

  const filtered = useMemo(()=>{
    const t = q.toLowerCase().trim();
    if(!t) return items;
    return items.filter(x => [x.username, x.email, x.rol, x.estado, String(x.id)]
      .some(v => String(v??"").toLowerCase().includes(t)));
  },[q, items]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const view = filtered.slice((page-1)*pageSize, page*pageSize);

  return (
    <Layout>
      <PageHeader title="Usuarios" subtitle="Gestión de usuarios y roles">
        <input className="border rounded-xl px-3 py-2 text-sm" value={q} placeholder="Buscar..."
               onChange={(e)=>setQ(e.target.value)} />
        <button className="px-3 py-2 rounded-xl border bg-white hover:bg-gray-50"
                onClick={()=>document.getElementById("dlgNuevoUser")?.showModal()}>+ Nuevo</button>
      </PageHeader>

      <div className="border rounded-2xl overflow-hidden bg-white shadow-sm">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50 text-gray-600">
            <tr>
              <th className="p-3">ID</th><th className="p-3">Usuario</th>
              <th className="p-3">Email</th><th className="p-3">Rol</th><th className="p-3">Estado</th>
            </tr>
          </thead>
          <tbody>
            {loading ? <tr><td colSpan={5} className="p-4">Cargando...</td></tr> :
            view.length===0 ? <tr><td colSpan={5} className="p-6 text-gray-500">Sin resultados.</td></tr> :
            view.map(u=>(
              <tr key={u.id} className="border-t">
                <td className="p-3">{u.id}</td>
                <td className="p-3">{u.username}</td>
                <td className="p-3">{u.email ?? "-"}</td>
                <td className="p-3">{u.rol}</td>
                <td className="p-3">{u.estado ?? "ACTIVO"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="flex items-center justify-between mt-4">
        <p className="text-sm text-gray-500">{filtered.length} resultados</p>
        <Pager page={page} setPage={setPage} totalPages={totalPages}/>
      </div>

      <dialog id="dlgNuevoUser" className="rounded-2xl p-0">
        <form method="dialog" className="p-5 w-[28rem]">
          <h3 className="font-semibold text-lg mb-3">Nuevo Usuario</h3>
          <div className="grid grid-cols-2 gap-3">
            <input className="border rounded-xl px-3 py-2 text-sm" placeholder="Usuario"
                   value={nuevo.username} onChange={e=>setNuevo(v=>({...v, username:e.target.value}))}/>
            <input className="border rounded-xl px-3 py-2 text-sm" placeholder="Email"
                   value={nuevo.email ?? ""} onChange={e=>setNuevo(v=>({...v, email:e.target.value}))}/>
            <input className="border rounded-xl px-3 py-2 text-sm" type="password" placeholder="Contraseña"
                   value={nuevo.password} onChange={e=>setNuevo(v=>({...v, password:e.target.value}))}/>
            <select className="border rounded-xl px-3 py-2 text-sm" value={nuevo.rol}
                    onChange={e=>setNuevo(v=>({...v, rol: e.target.value as any}))}>
              <option value="ADMIN">ADMIN</option>
              <option value="VENTAS">VENTAS</option>
              <option value="MAQUILA">MAQUILA</option>
            </select>
          </div>
          <div className="mt-4 flex justify-end gap-2">
            <button className="px-3 py-1.5 rounded-lg border">Cancelar</button>
            <button className="px-3 py-1.5 rounded-lg bg-brand-blue text-white" onClick={async()=>{
              await crearUsuario(nuevo);
              (document.getElementById("dlgNuevoUser") as HTMLDialogElement)?.close();
              setNuevo({ username:"", password:"", rol:"VENTAS", email:"" });
              load();
            }}>Guardar</button>
          </div>
        </form>
      </dialog>
    </Layout>
  );
}
