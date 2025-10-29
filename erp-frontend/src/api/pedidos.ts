import { api } from "./axios";

export type PedidoVM = {
  id: number;
  numero: string;
  cliente: string;
  fecha: string | null;  
  estado: string;
};

export type PedidoDetalle={

        id: number,
        numeroPedido: string,
        clienteNombre: string;
        clienteRuc: string
        clienteDireccion: string;
        clienteTelefono: string;
        total: number;
        estado: string;
        fechaPedido: string;
        fechaEntregaEstimada: string;
        observaciones: string;
        usuario: string,
        detalles: string
        fechaCreacion: string;
        fechaActualizacion: string;
}


export type PedidoCreateInput = {
  clienteNombre: string;
  clienteRuc?: string | null;
  clienteTelefono?: string | null;
  clienteDireccion?: string | null;
  observaciones?: string | null;
  fechaEntregaEstimada?: string | null; 
  detalles?: {
    producto: { id: number };   
    cantidad: number;
  }[];
};


export async function listarPedidos(): Promise<PedidoVM[]> {
  const res = await api.get("/api/pedidos");
  const items = (res.data?.data ?? []) as any[];

  return items.map(p => ({
    id: p.id,
    numero: p.numeroPedido,
    cliente: p.clienteNombre,
    fecha: p.fechaPedido ?? null,
    estado: p.estado,
  }));
}

export async function crearPedido(input: PedidoCreateInput) {

  const payload = {
    clienteNombre: input.clienteNombre,
    clienteRuc: input.clienteRuc ?? null,
    clienteTelefono: input.clienteTelefono ?? null,
    clienteDireccion: input.clienteDireccion ?? null,
    observaciones: input.observaciones ?? null,
    fechaEntregaEstimada: input.fechaEntregaEstimada ?? null,
    estado: "PENDIENTE",
    total: 1, 
    detalles: input.detalles?.map(d => ({
      producto: { id: d.producto.id },
      cantidad: d.cantidad,
    })) ?? [],
  };

  await api.post("/api/pedidos", payload);
}

export async function obtenerPedidoPorId(id: number): Promise<PedidoDetalle> {
  const res = await api.get(`/api/pedidos/${id}`);


  const pedido = res.data?.data ?? res.data;

  return {
    id: pedido.id,
    numeroPedido: pedido.numeroPedido,
    clienteNombre: pedido.clienteNombre,
    clienteRuc: pedido.clienteRuc,
    clienteDireccion: pedido.clienteDireccion,
    clienteTelefono: pedido.clienteTelefono,
    total: pedido.total,
    estado: pedido.estado,
    fechaPedido: pedido.fechaPedido,
    fechaEntregaEstimada: pedido.fechaEntregaEstimada,
    observaciones: pedido.observaciones,
    usuario: pedido.usuario,
    detalles: pedido.detalles ?? [], 
    fechaCreacion: pedido.fechaCreacion,
    fechaActualizacion: pedido.fechaActualizacion,
  };
}
