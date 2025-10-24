package com.lecoq.erp.dto;

import java.time.LocalDateTime;
import com.lecoq.erp.entity.Distribucion;

public record DistribucionDTO(
        Long id,
        Long pedidoId,
        String direccionEntrega,      // <--- "Destino" en la tabla
        LocalDateTime fechaSalida,
        String estado
) {
    public static DistribucionDTO from(Distribucion d) {
        if (d == null) return null;
        return new DistribucionDTO(
                d.getId(),
                d.getPedido() != null ? d.getPedido().getId() : null,
                d.getDireccionEntrega(),     // <-- nombre EXACTO que lee el front
                d.getFechaSalida(),          // <-- nombre EXACTO que lee el front
                d.getEstado() != null ? d.getEstado().name() : null
        );
    }
}
