package com.lecoq.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "detalles_maquilado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleMaquilado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maquilado_id", nullable = false)
    private Maquilado maquilado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(name = "cantidad_solicitada", nullable = false)
    @NotNull(message = "La cantidad solicitada es obligatoria")
    @Min(value = 1, message = "La cantidad solicitada debe ser mayor a 0")
    private Integer cantidadSolicitada;
    
    @Column(name = "cantidad_recibida", nullable = false)
    @Min(value = 0, message = "La cantidad recibida no puede ser negativa")
    private Integer cantidadRecibida = 0;
    
    @Column(name = "costo_unitario", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El costo unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El costo unitario debe ser mayor a 0")
    private BigDecimal costoUnitario;
    
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.01", message = "El subtotal debe ser mayor a 0")
    private BigDecimal subtotal;
    
    @PrePersist
    @PreUpdate
    protected void calcularSubtotal() {
        if (cantidadSolicitada != null && costoUnitario != null) {
            subtotal = costoUnitario.multiply(BigDecimal.valueOf(cantidadSolicitada));
        }
    }
}
