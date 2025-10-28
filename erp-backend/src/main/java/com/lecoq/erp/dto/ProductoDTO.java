package com.lecoq.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.lecoq.erp.entity.Producto;

/**
 * DTO para exponer productos sin entidades JPA.
 * Ajusta los campos si tu entidad tiene nombres diferentes.
 */
public record ProductoDTO(
        Long id,
        String nombre,
        String descripcion,
        String presentacion,
        Integer stock,
        BigDecimal precio,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {
    public static ProductoDTO from(Producto p) {
        if (p == null) return null;
        return new ProductoDTO(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPresentacion(),
                p.getStock(),
                p.getPrecio(),
                // Si tu entidad usa "isActivo()" o "getEstado()", ajusta esta línea:
                p.getActivo(),
                // Si no tienes timestamps, deja estos como null o elimínalos del DTO:
                p.getFechaCreacion(),
                p.getFechaActualizacion()
        );
    }
}
