package com.lecoq.erp.controller;

import com.lecoq.erp.dto.ApiResponse;
import com.lecoq.erp.entity.DetallePedido;
import com.lecoq.erp.entity.Pedido;
import com.lecoq.erp.entity.Usuario;
import com.lecoq.erp.service.PedidoService;
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
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getAllPedidos(Authentication authentication) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            List<Pedido> pedidos;
            
            if (usuario.getRol() == Usuario.Rol.ADMIN) {
                pedidos = pedidoService.findAll();
            } else {
                pedidos = pedidoService.findByUsuario(usuario);
            }
            
            return ResponseEntity.ok(ApiResponse.success("Pedidos obtenidos exitosamente", pedidos));
        } catch (Exception e) {
            log.error("Error obteniendo pedidos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getPedidoById(@PathVariable Long id) {
        try {
            Optional<Pedido> pedido = pedidoService.findById(id);
            if (pedido.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Pedido encontrado", pedido.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Pedido no encontrado"));
            }
        } catch (Exception e) {
            log.error("Error obteniendo pedido por ID: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo pedido: " + e.getMessage()));
        }
    }

    @GetMapping("/numero/{numeroPedido}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getPedidoByNumero(@PathVariable String numeroPedido) {
        try {
            Optional<Pedido> pedido = pedidoService.findByNumeroPedido(numeroPedido);
            if (pedido.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Pedido encontrado", pedido.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Pedido no encontrado"));
            }
        } catch (Exception e) {
            log.error("Error obteniendo pedido por número: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo pedido: " + e.getMessage()));
        }
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getPedidosByEstado(@PathVariable Pedido.EstadoPedido estado) {
        try {
            List<Pedido> pedidos = pedidoService.findByEstado(estado);
            return ResponseEntity.ok(ApiResponse.success("Pedidos obtenidos por estado", pedidos));
        } catch (Exception e) {
            log.error("Error obteniendo pedidos por estado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/cliente")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getPedidosByCliente(@RequestParam String nombre) {
        try {
            List<Pedido> pedidos = pedidoService.findByClienteNombre(nombre);
            return ResponseEntity.ok(ApiResponse.success("Pedidos encontrados por cliente", pedidos));
        } catch (Exception e) {
            log.error("Error buscando pedidos por cliente: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error buscando pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/fecha")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getPedidosByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<Pedido> pedidos = pedidoService.findByFechaPedidoBetween(fechaInicio, fechaFin);
            return ResponseEntity.ok(ApiResponse.success("Pedidos obtenidos por fecha", pedidos));
        } catch (Exception e) {
            log.error("Error obteniendo pedidos por fecha: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/detalles")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getDetallesPedido(@PathVariable Long id) {
        try {
            List<DetallePedido> detalles = pedidoService.findDetallesByPedidoId(id);
            return ResponseEntity.ok(ApiResponse.success("Detalles del pedido obtenidos", detalles));
        } catch (Exception e) {
            log.error("Error obteniendo detalles del pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo detalles: " + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> createPedido(@Valid @RequestBody Pedido pedido, Authentication authentication) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            Pedido nuevoPedido = pedidoService.create(pedido, usuario.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Pedido creado exitosamente", nuevoPedido));
        } catch (Exception e) {
            log.error("Error creando pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error creando pedido: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> updatePedido(@PathVariable Long id, @Valid @RequestBody Pedido pedido) {
        try {
            Pedido pedidoActualizado = pedidoService.update(id, pedido);
            return ResponseEntity.ok(ApiResponse.success("Pedido actualizado exitosamente", pedidoActualizado));
        } catch (Exception e) {
            log.error("Error actualizando pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error actualizando pedido: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> cambiarEstadoPedido(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String estadoStr = request.get("estado");
            if (estadoStr == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("El estado es requerido"));
            }
            
            Pedido.EstadoPedido nuevoEstado = Pedido.EstadoPedido.valueOf(estadoStr.toUpperCase());
            Pedido pedidoActualizado = pedidoService.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(ApiResponse.success("Estado del pedido actualizado", pedidoActualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Estado inválido: " + request.get("estado")));
        } catch (Exception e) {
            log.error("Error cambiando estado del pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error cambiando estado: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deletePedido(@PathVariable Long id) {
        try {
            pedidoService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Pedido eliminado exitosamente"));
        } catch (Exception e) {
            log.error("Error eliminando pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error eliminando pedido: " + e.getMessage()));
        }
    }
}
