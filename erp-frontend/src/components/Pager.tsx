export default function Pager({ page, setPage, totalPages }:{
  page: number; setPage: (n:number)=>void; totalPages:number;
}) {
  return (
    <div className="flex items-center gap-2">
      <button onClick={()=>setPage(Math.max(1, page-1))} disabled={page===1}
        className="px-3 py-1.5 rounded-lg border bg-white disabled:opacity-50">Anterior</button>
      <span className="text-sm text-gray-600"> {page} / {totalPages} </span>
      <button onClick={()=>setPage(Math.min(totalPages, page+1))} disabled={page===totalPages}
        className="px-3 py-1.5 rounded-lg border bg-white disabled:opacity-50">Siguiente</button>
    </div>
  );
}
