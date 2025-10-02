package com.lecoq.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "distribuciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Distribucion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @Column(name = "chofer_nombre", nullable = false)
    @NotBlank(message = "El nombre del chofer es obligatorio")
    @Size(max = 100, message = "El nombre del chofer no puede exceder 100 caracteres")
    private String choferNombre;
    
    @Column(name = "chofer_telefono")
    @Size(max = 20, message = "El teléfono del chofer no puede exceder 20 caracteres")
    private String choferTelefono;
    
    @Column(name = "vehiculo_placa", nullable = false)
    @NotBlank(message = "La placa del vehículo es obligatoria")
    @Size(max = 10, message = "La placa no puede exceder 10 caracteres")
    private String vehiculoPlaca;
    
    @Column(name = "vehiculo_modelo")
    @Size(max = 50, message = "El modelo del vehículo no puede exceder 50 caracteres")
    private String vehiculoModelo;
    
    @Column(name = "fecha_salida", nullable = false)
    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDateTime fechaSalida;
    
    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDistribucion estado = EstadoDistribucion.PROGRAMADO;
    
    @Column(length = 500)
    private String observaciones;
    
    @Column(name = "direccion_entrega", nullable = false)
    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccionEntrega;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    public enum EstadoDistribucion {
        PROGRAMADO, EN_RUTA, ENTREGADO, FALLIDO
    }
}
