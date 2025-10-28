package com.lecoq.erp.dto;

import java.time.LocalDateTime;
import com.lecoq.erp.entity.Distribucion;

public record DistribucionDTO(
        Long id,
        Long pedidoId,
        String direccionEntrega,
        LocalDateTime fechaSalida,
        String estado,
        String choferNombre,
        String choferTelefono,
        String vehiculoPlaca,
        String vehiculoModelo,
        String observaciones,
        LocalDateTime fechaEntrega
) {
    public static DistribucionDTO from(Distribucion d) {
        if (d == null) return null;
        return new DistribucionDTO(
                d.getId(),
                d.getPedido() != null ? d.getPedido().getId() : null,
                d.getDireccionEntrega(),
                d.getFechaSalida(),
                d.getEstado() != null ? d.getEstado().name() : null,
                d.getChoferNombre(),
                d.getChoferTelefono(),
                d.getVehiculoPlaca(),
                d.getVehiculoModelo(),
                d.getObservaciones(),
                d.getFechaEntrega()
        );
    }
}

