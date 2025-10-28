package com.lecoq.erp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.lecoq.erp.entity.Maquilado;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MaquiladoDTO(
        Long id,
        String numeroOrden,
        String proveedorNombre,
        String proveedorRuc,
        String proveedorContacto,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDateTime fechaOrden,

        String estado,          // Enum como texto (name)
        BigDecimal costoTotal,
        String observaciones
) {

    public static MaquiladoDTO from(Maquilado m) {
        if (m == null) return null;
        return new MaquiladoDTO(
                m.getId(),
                m.getNumeroOrden(),
                m.getProveedorNombre(),
                m.getProveedorRuc(),
                m.getProveedorContacto(),
                m.getFechaOrden(),            // LocalDate
                m.getEstado() != null ? m.getEstado().name() : null,
                m.getCostoTotal(),
                m.getObservaciones()
        );
    }

    public static List<MaquiladoDTO> from(List<Maquilado> list) {
        return list.stream().map(MaquiladoDTO::from).toList();
    }

    public Maquilado toNewEntity() {
        Maquilado x = new Maquilado();
        // id autogenerado
        x.setNumeroOrden(nullIfBlank(numeroOrden));
        x.setProveedorNombre(nullIfBlank(proveedorNombre));
        x.setProveedorRuc(nullIfBlank(proveedorRuc));
        x.setProveedorContacto(nullIfBlank(proveedorContacto));
        x.setFechaOrden(fechaOrden);
        x.setEstado(parseEstado(estado));
        x.setCostoTotal(costoTotal);
        x.setObservaciones(nullIfBlank(observaciones));
        return x;
    }

    public void applyTo(Maquilado target) {
        if (target == null) return;
        if (numeroOrden != null)        target.setNumeroOrden(nullIfBlank(numeroOrden));
        if (proveedorNombre != null)    target.setProveedorNombre(nullIfBlank(proveedorNombre));
        if (proveedorRuc != null)       target.setProveedorRuc(nullIfBlank(proveedorRuc));
        if (proveedorContacto != null)  target.setProveedorContacto(nullIfBlank(proveedorContacto));
        if (fechaOrden != null)         target.setFechaOrden(fechaOrden);
        if (estado != null)             target.setEstado(parseEstado(estado));
        if (costoTotal != null)         target.setCostoTotal(costoTotal);
        if (observaciones != null)      target.setObservaciones(nullIfBlank(observaciones));
    }

    private static String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
    private static Maquilado.EstadoMaquilado parseEstado(String raw) {
        if (raw == null || raw.isBlank()) return null;
        return Maquilado.EstadoMaquilado.valueOf(raw.trim().toUpperCase());
    }
}
