package com.lecoq.erp.controller;

import com.lecoq.erp.dto.ApiResponse;
import com.lecoq.erp.dto.DetallePedidoDTO;
import com.lecoq.erp.dto.PedidoDTO;
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
            List<Pedido> pedidos = (usuario.getRol() == Usuario.Rol.ADMIN)
                    ? pedidoService.findAll()
                    : pedidoService.findByUsuario(usuario);

            var dto = pedidos.stream().map(PedidoDTO::from).toList();
            return ResponseEntity.ok(ApiResponse.success("Pedidos obtenidos exitosamente", dto));
        } catch (Exception e) {
            log.error("Error obteniendo pedidos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getPedidoById(@PathVariable Long id) {
        try {
            Optional<Pedido> pedidoOpt = pedidoService.findById(id);
            if (pedidoOpt.isPresent()) {
                var p = pedidoOpt.get();
                var detalles = pedidoService.findDetallesByPedidoId(id);
                return ResponseEntity.ok(ApiResponse.success("Pedido encontrado", PedidoDTO.from(p, detalles)));

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Pedido no encontrado"));
            }
        } catch (Exception e) {
            log.error("Error obteniendo pedido por ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo pedido: " + e.getMessage()));
        }
    }

    @GetMapping("/numero/{numeroPedido}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getPedidoByNumero(@PathVariable String numeroPedido) {
        try {
            Optional<Pedido> pedidoOpt = pedidoService.findByNumeroPedido(numeroPedido);
            if (pedidoOpt.isPresent()) {
                var p = pedidoOpt.get();
                var detalles = pedidoService.findDetallesByPedidoId(p.getId());
                return ResponseEntity.ok(ApiResponse.success("Pedido encontrado", PedidoDTO.from(p, detalles)));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Pedido no encontrado"));
            }
        } catch (Exception e) {
            log.error("Error obteniendo pedido por número: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo pedido: " + e.getMessage()));
        }
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getPedidosByEstado(@PathVariable Pedido.EstadoPedido estado) {
        try {
            var dtoList = pedidoService.findByEstado(estado).stream()
                    .map(p -> PedidoDTO.from(p, pedidoService.findDetallesByPedidoId(p.getId())))
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Pedidos obtenidos por estado", dtoList));
        } catch (Exception e) {
            log.error("Error obteniendo pedidos por estado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/cliente")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getPedidosByCliente(@RequestParam String nombre) {
        try {
            var dtoList = pedidoService.findByClienteNombre(nombre).stream()
                    .map(p -> PedidoDTO.from(p, pedidoService.findDetallesByPedidoId(p.getId())))
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Pedidos encontrados por cliente", dtoList));
        } catch (Exception e) {
            log.error("Error buscando pedidos por cliente: {}", e.getMessage(), e);
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
            var dtoList = pedidoService.findByFechaPedidoBetween(fechaInicio, fechaFin).stream()
                    .map(p -> PedidoDTO.from(p, pedidoService.findDetallesByPedidoId(p.getId())))
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Pedidos obtenidos por fecha", dtoList));
        } catch (Exception e) {
            log.error("Error obteniendo pedidos por fecha: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/detalles")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getDetallesPedido(@PathVariable Long id) {
        try {
            var dto = pedidoService.findDetallesByPedidoId(id).stream()
                    .map(DetallePedidoDTO::from)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Detalles del pedido obtenidos", dto));
        } catch (Exception e) {
            log.error("Error obteniendo detalles del pedido: {}", e.getMessage(), e);
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
            var detalles = pedidoService.findDetallesByPedidoId(nuevoPedido.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Pedido creado exitosamente", PedidoDTO.from(nuevoPedido, detalles)));
        } catch (Exception e) {
            log.error("Error creando pedido: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error creando pedido: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> updatePedido(@PathVariable Long id, @Valid @RequestBody Pedido pedido) {
        try {
            Pedido pedidoActualizado = pedidoService.update(id, pedido);
            var detalles = pedidoService.findDetallesByPedidoId(pedidoActualizado.getId());
            return ResponseEntity.ok(ApiResponse.success("Pedido actualizado exitosamente", PedidoDTO.from(pedidoActualizado, detalles)));
        } catch (Exception e) {
            log.error("Error actualizando pedido: {}", e.getMessage(), e);
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
            var detalles = pedidoService.findDetallesByPedidoId(pedidoActualizado.getId());
            return ResponseEntity.ok(ApiResponse.success("Estado del pedido actualizado", PedidoDTO.from(pedidoActualizado, detalles)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Estado inválido: " + request.get("estado")));
        } catch (Exception e) {
            log.error("Error cambiando estado del pedido: {}", e.getMessage(), e);
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
            log.error("Error eliminando pedido: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error eliminando pedido: " + e.getMessage()));
        }
    }
}
