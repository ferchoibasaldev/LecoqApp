package com.lecoq.erp.dto;

import com.lecoq.erp.entity.DetallePedido;
import com.lecoq.erp.entity.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoDTO(
        Long id,
        String numeroPedido,
        String clienteNombre,
        LocalDateTime fechaPedido,
        String estado,
        BigDecimal total,
        Integer items
) {

    // Para listados: solo a partir de la entidad Pedido
    public static PedidoDTO from(Pedido p) {
        if (p == null) return null;
        return new PedidoDTO(
                p.getId(),
                p.getNumeroPedido(),
                p.getClienteNombre(),
                p.getFechaPedido(),
                p.getEstado() != null ? p.getEstado().name() : null,
                p.getTotal(),
                null // sin calcular items en el listado
        );
    }

    public static PedidoDTO from(Pedido p, List<DetallePedido> detalles) {
        if (p == null) return null;

        Integer items = null;
        if (detalles != null) {

            items = detalles.stream()
                    .map(DetallePedido::getCantidad)
                    .filter(q -> q != null)
                    .reduce(0, Integer::sum);
        }

        return new PedidoDTO(
                p.getId(),
                p.getNumeroPedido(),
                p.getClienteNombre(),
                p.getFechaPedido(),
                p.getEstado() != null ? p.getEstado().name() : null,
                p.getTotal(),
                items
        );
    }
}
