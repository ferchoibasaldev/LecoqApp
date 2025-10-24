package com.lecoq.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maquilados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maquilado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_orden", unique = true, nullable = false)
    private String numeroOrden;
    
    @Column(name = "proveedor_nombre", nullable = false)
    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 100, message = "El nombre del proveedor no puede exceder 100 caracteres")
    private String proveedorNombre;
    
    @Column(name = "proveedor_ruc")
    @Size(max = 20, message = "El RUC del proveedor no puede exceder 20 caracteres")
    private String proveedorRuc;
    
    @Column(name = "proveedor_contacto")
    @Size(max = 100, message = "El contacto del proveedor no puede exceder 100 caracteres")
    private String proveedorContacto;
    
    @Column(name = "proveedor_telefono")
    @Size(max = 20, message = "El teléfono del proveedor no puede exceder 20 caracteres")
    private String proveedorTelefono;
    
    @Column(name = "costo_total", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El costo total es obligatorio")
    @DecimalMin(value = "0.01", message = "El costo total debe ser mayor a 0")
    private BigDecimal costoTotal;
    
    @Column(name = "fecha_orden", nullable = false)
    @NotNull(message = "La fecha de orden es obligatoria")
    private LocalDateTime fechaOrden;
    
    @Column(name = "fecha_entrega_estimada")
    private LocalDateTime fechaEntregaEstimada;
    
    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMaquilado estado = EstadoMaquilado.PENDIENTE;
    
    @Column(length = 500)
    private String observaciones;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @OneToMany(mappedBy = "maquilado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleMaquilado> detalles = new ArrayList<>();
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (fechaOrden == null) {
            fechaOrden = LocalDateTime.now();
        }
        if (numeroOrden == null) {
            numeroOrden = "MAQ-" + System.currentTimeMillis();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    public enum EstadoMaquilado {
        PENDIENTE, EN_PROCESO, FINALIZADO, RECIBIDO, CANCELADO
    }
}
