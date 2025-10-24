package com.lecoq.erp.dto;

import java.math.BigDecimal;

import com.lecoq.erp.entity.DetallePedido;

public record DetallePedidoDTO(
        Long id,
        Long productoId,
        String productoNombre,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
    public static DetallePedidoDTO from(DetallePedido d) {
        return new DetallePedidoDTO(
                d.getId(),
                d.getProducto() != null ? d.getProducto().getId() : null,
                d.getProducto() != null ? d.getProducto().getNombre() : null,
                d.getCantidad(),
                d.getPrecioUnitario(),
                d.getSubtotal()
        );
    }
}
