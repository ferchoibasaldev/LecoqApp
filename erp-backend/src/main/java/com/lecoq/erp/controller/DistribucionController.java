package com.lecoq.erp.controller;

import com.lecoq.erp.dto.ApiResponse;
import com.lecoq.erp.dto.DistribucionDTO;
import com.lecoq.erp.entity.Distribucion;
import com.lecoq.erp.entity.Usuario;
import com.lecoq.erp.service.DistribucionService;
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
@RequestMapping("/api/distribuciones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class DistribucionController {

    private final DistribucionService distribucionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getAllDistribuciones(Authentication authentication) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            List<Distribucion> distribuciones;

            if (usuario.getRol() == Usuario.Rol.ADMIN) {
                distribuciones = distribucionService.findAll();
            } else {
                distribuciones = distribucionService.findByUsuario(usuario);
            }

            var dto = distribuciones.stream().map(DistribucionDTO::from).toList();
            return ResponseEntity.ok(ApiResponse.success("Distribuciones obtenidas exitosamente", dto));
        } catch (Exception e) {
            log.error("Error obteniendo distribuciones: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo distribuciones: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getDistribucionById(@PathVariable Long id) {
        try {
            Optional<Distribucion> distribucion = distribucionService.findById(id);
            if (distribucion.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Distribución encontrada", DistribucionDTO.from(distribucion.get())));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Distribución no encontrada"));
            }
        } catch (Exception e) {
            log.error("Error obteniendo distribución por ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo distribución: " + e.getMessage()));
        }
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getDistribucionesByEstado(@PathVariable Distribucion.EstadoDistribucion estado) {
        try {
            List<Distribucion> distribuciones = distribucionService.findByEstado(estado);
            var dto = distribuciones.stream().map(DistribucionDTO::from).toList();
            return ResponseEntity.ok(ApiResponse.success("Distribuciones obtenidas por estado", dto));
        } catch (Exception e) {
            log.error("Error obteniendo distribuciones por estado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo distribuciones: " + e.getMessage()));
        }
    }

    @GetMapping("/chofer")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getDistribucionesByChofer(@RequestParam String nombre) {
        try {
            List<Distribucion> distribuciones = distribucionService.findByChoferNombre(nombre);
            var dto = distribuciones.stream().map(DistribucionDTO::from).toList();
            return ResponseEntity.ok(ApiResponse.success("Distribuciones encontradas por chofer", dto));
        } catch (Exception e) {
            log.error("Error buscando distribuciones por chofer: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error buscando distribuciones: " + e.getMessage()));
        }
    }

    @GetMapping("/vehiculo/{placa}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getDistribucionesByVehiculo(@PathVariable String placa) {
        try {
            List<Distribucion> distribuciones = distribucionService.findByVehiculoPlaca(placa);
            var dto = distribuciones.stream().map(DistribucionDTO::from).toList();
            return ResponseEntity.ok(ApiResponse.success("Distribuciones encontradas por vehículo", dto));
        } catch (Exception e) {
            log.error("Error buscando distribuciones por vehículo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error buscando distribuciones: " + e.getMessage()));
        }
    }

    @GetMapping("/fecha")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getDistribucionesByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<Distribucion> distribuciones = distribucionService.findByFechaSalidaBetween(fechaInicio, fechaFin);
            var dto = distribuciones.stream().map(DistribucionDTO::from).toList();
            return ResponseEntity.ok(ApiResponse.success("Distribuciones obtenidas por fecha", dto));
        } catch (Exception e) {
            log.error("Error obteniendo distribuciones por fecha: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo distribuciones: " + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> createDistribucion(@Valid @RequestBody Distribucion distribucion,
                                                          @RequestParam Long pedidoId,
                                                          Authentication authentication) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            Distribucion nuevaDistribucion = distribucionService.create(distribucion, pedidoId, usuario.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Distribución creada exitosamente", DistribucionDTO.from(nuevaDistribucion)));
        } catch (Exception e) {
            log.error("Error creando distribución: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error creando distribución: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> updateDistribucion(@PathVariable Long id, @Valid @RequestBody Distribucion distribucion) {
        try {
            Distribucion distribucionActualizada = distribucionService.update(id, distribucion);
            return ResponseEntity.ok(ApiResponse.success("Distribución actualizada exitosamente", DistribucionDTO.from(distribucionActualizada)));
        } catch (Exception e) {
            log.error("Error actualizando distribución: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error actualizando distribución: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> cambiarEstadoDistribucion(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String estadoStr = request.get("estado");
            if (estadoStr == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("El estado es requerido"));
            }

            Distribucion.EstadoDistribucion nuevoEstado = Distribucion.EstadoDistribucion.valueOf(estadoStr.toUpperCase());
            Distribucion distribucionActualizada = distribucionService.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(ApiResponse.success("Estado de la distribución actualizado", DistribucionDTO.from(distribucionActualizada)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Estado inválido: " + request.get("estado")));
        } catch (Exception e) {
            log.error("Error cambiando estado de la distribución: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error cambiando estado: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/entregar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> marcarComoEntregado(@PathVariable Long id) {
        try {
            distribucionService.marcarComoEntregado(id);
            return ResponseEntity.ok(ApiResponse.success("Distribución marcada como entregada"));
        } catch (Exception e) {
            log.error("Error marcando distribución como entregada: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error marcando como entregada: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/en-ruta")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> marcarComoEnRuta(@PathVariable Long id) {
        try {
            distribucionService.marcarComoEnRuta(id);
            return ResponseEntity.ok(ApiResponse.success("Distribución marcada como en ruta"));
        } catch (Exception e) {
            log.error("Error marcando distribución como en ruta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error marcando como en ruta: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/fallido")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> marcarComoFallido(@PathVariable Long id) {
        try {
            distribucionService.marcarComoFallido(id);
            return ResponseEntity.ok(ApiResponse.success("Distribución marcada como fallida"));
        } catch (Exception e) {
            log.error("Error marcando distribución como fallida: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error marcando como fallida: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteDistribucion(@PathVariable Long id) {
        try {
            distribucionService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Distribución eliminada exitosamente"));
        } catch (Exception e) {
            log.error("Error eliminando distribución: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error eliminando distribución: " + e.getMessage()));
        }
    }
}
