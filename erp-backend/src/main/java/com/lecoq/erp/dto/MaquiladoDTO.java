package com.lecoq.erp.dto;

import java.time.LocalDateTime;
import com.lecoq.erp.entity.Maquilado;

public record MaquiladoDTO(
        Long id,
        String numeroOrden,
        String proveedorNombre,
        LocalDateTime fechaOrden,
        String estado
) {
    public static MaquiladoDTO from(Maquilado m) {
        if (m == null) return null;
        return new MaquiladoDTO(
                m.getId(),
                m.getNumeroOrden(),
                m.getProveedorNombre(),      // <-- nombre EXACTO que lee el front
                m.getFechaOrden(),           // <-- nombre EXACTO que lee el front
                m.getEstado() != null ? m.getEstado().name() : null
        );
    }
}
