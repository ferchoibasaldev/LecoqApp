package com.lecoq.erp.controller;

import com.lecoq.erp.dto.ApiResponse;
import com.lecoq.erp.dto.MaquiladoDTO;
import com.lecoq.erp.entity.DetalleMaquilado;
import com.lecoq.erp.entity.Maquilado;
import com.lecoq.erp.entity.Usuario;
import com.lecoq.erp.service.MaquiladoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/maquilados")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class MaquiladoController {

    private final MaquiladoService maquiladoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> getAllMaquilados(Authentication authentication) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            List<Maquilado> maquilados;

            if (usuario.getRol() == Usuario.Rol.ADMIN) {
                maquilados = maquiladoService.findAll();
            } else {
                maquilados = maquiladoService.findByUsuario(usuario);
            }

            var dto = maquilados.stream().map(MaquiladoDTO::from).toList();
            return ResponseEntity.ok(ApiResponse.success("Maquilados obtenidos exitosamente", dto));
        } catch (Exception e) {
            log.error("Error obteniendo maquilados: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo maquilados: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> getMaquiladoById(@PathVariable Long id) {
        try {
            Optional<Maquilado> maquilado = maquiladoService.findById(id);
            if (maquilado.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Maquilado encontrado", MaquiladoDTO.from(maquilado.get())));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Maquilado no encontrado"));
            }
        } catch (Exception e) {
            log.error("Error obteniendo maquilado por ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo maquilado: " + e.getMessage()));
        }
    }

    @GetMapping("/numero/{numeroOrden}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> getMaquiladoByNumero(@PathVariable String numeroOrden) {
        try {
            Optional<Maquilado> maquilado = maquiladoService.findByNumeroOrden(numeroOrden);
            if (maquilado.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Maquilado encontrado", MaquiladoDTO.from(maquilado.get())));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Maquilado no encontrado"));
            }
        } catch (Exception e) {
            log.error("Error obteniendo maquilado por número: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo maquilado: " + e.getMessage()));
        }
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> getMaquiladosByEstado(@PathVariable Maquilado.EstadoMaquilado estado) {
        try {
            List<Maquilado> maquilados = maquiladoService.findByEstado(estado);
            var dto = maquilados.stream().map(MaquiladoDTO::from).toList();
            return ResponseEntity.ok(ApiResponse.success("Maquilados obtenidos por estado", dto));
        } catch (Exception e) {
            log.error("Error obteniendo maquilados por estado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo maquilados: " + e.getMessage()));
        }
    }

    @GetMapping("/proveedor")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> getMaquiladosByProveedor(@RequestParam String nombre) {
        try {
            List<Maquilado> maquilados = maquiladoService.findByProveedorNombre(nombre);
            var dto = maquilados.stream().map(MaquiladoDTO::from).toList();
            return ResponseEntity.ok(ApiResponse.success("Maquilados encontrados por proveedor", dto));
        } catch (Exception e) {
            log.error("Error buscando maquilados por proveedor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error buscando maquilados: " + e.getMessage()));
        }
    }

    @GetMapping("/fecha")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> getMaquiladosByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<Maquilado> maquilados = maquiladoService.findByFechaOrdenBetween(fechaInicio, fechaFin);
            var dto = maquilados.stream().map(MaquiladoDTO::from).toList();
            return ResponseEntity.ok(ApiResponse.success("Maquilados obtenidos por fecha", dto));
        } catch (Exception e) {
            log.error("Error obteniendo maquilados por fecha: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo maquilados: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/detalles")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> getDetallesMaquilado(@PathVariable Long id) {
        try {
            List<DetalleMaquilado> detalles = maquiladoService.findDetallesByMaquiladoId(id);
            return ResponseEntity.ok(ApiResponse.success("Detalles del maquilado obtenidos", detalles));
        } catch (Exception e) {
            log.error("Error obteniendo detalles del maquilado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo detalles: " + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> createMaquilado(@Valid @RequestBody Maquilado maquilado, Authentication authentication) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            Maquilado nuevoMaquilado = maquiladoService.create(maquilado, usuario.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Maquilado creado exitosamente", MaquiladoDTO.from(nuevoMaquilado)));
        } catch (Exception e) {
            log.error("Error creando maquilado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error creando maquilado: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> updateMaquilado(@PathVariable Long id, @Valid @RequestBody Maquilado maquilado) {
        try {
            Maquilado maquiladoActualizado = maquiladoService.update(id, maquilado);
            return ResponseEntity.ok(ApiResponse.success("Maquilado actualizado exitosamente", MaquiladoDTO.from(maquiladoActualizado)));
        } catch (Exception e) {
            log.error("Error actualizando maquilado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error actualizando maquilado: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> cambiarEstadoMaquilado(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String estadoStr = request.get("estado");
            if (estadoStr == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("El estado es requerido"));
            }

            Maquilado.EstadoMaquilado nuevoEstado = Maquilado.EstadoMaquilado.valueOf(estadoStr.toUpperCase());
            Maquilado maquiladoActualizado = maquiladoService.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(ApiResponse.success("Estado del maquilado actualizado", MaquiladoDTO.from(maquiladoActualizado)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Estado inválido: " + request.get("estado")));
        } catch (Exception e) {
            log.error("Error cambiando estado del maquilado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error cambiando estado: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/recibir")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> recibirMaquilado(@PathVariable Long id) {
        try {
            Maquilado maquiladoRecibido = maquiladoService.recibirMaquilado(id);
            return ResponseEntity.ok(ApiResponse.success("Maquilado recibido exitosamente", MaquiladoDTO.from(maquiladoRecibido)));
        } catch (Exception e) {
            log.error("Error recibiendo maquilado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error recibiendo maquilado: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/cantidades-recibidas")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> actualizarCantidadesRecibidas(
            @PathVariable Long id,
            @RequestBody List<DetalleMaquilado> detallesActualizados) {
        try {
            Maquilado maquiladoActualizado = maquiladoService.actualizarCantidadesRecibidas(id, detallesActualizados);
            return ResponseEntity.ok(ApiResponse.success("Cantidades recibidas actualizadas", MaquiladoDTO.from(maquiladoActualizado)));
        } catch (Exception e) {
            log.error("Error actualizando cantidades recibidas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error actualizando cantidades: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/en-proceso")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> marcarComoEnProceso(@PathVariable Long id) {
        try {
            maquiladoService.marcarComoEnProceso(id);
            return ResponseEntity.ok(ApiResponse.success("Maquilado marcado como en proceso"));
        } catch (Exception e) {
            log.error("Error marcando maquilado como en proceso: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error marcando como en proceso: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/finalizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> marcarComoFinalizado(@PathVariable Long id) {
        try {
            maquiladoService.marcarComoFinalizado(id);
            return ResponseEntity.ok(ApiResponse.success("Maquilado marcado como finalizado"));
        } catch (Exception e) {
            log.error("Error marcando maquilado como finalizado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error marcando como finalizado: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> marcarComoCancelado(@PathVariable Long id) {
        try {
            maquiladoService.marcarComoCancelado(id);
            return ResponseEntity.ok(ApiResponse.success("Maquilado marcado como cancelado"));
        } catch (Exception e) {
            log.error("Error marcando maquilado como cancelado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error marcando como cancelado: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteMaquilado(@PathVariable Long id) {
        try {
            maquiladoService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Maquilado eliminado exitosamente"));
        } catch (Exception e) {
            log.error("Error eliminando maquilado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error eliminando maquilado: " + e.getMessage()));
        }
    }
}
