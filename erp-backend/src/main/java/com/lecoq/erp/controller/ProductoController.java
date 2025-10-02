package com.lecoq.erp.controller;

import com.lecoq.erp.dto.ApiResponse;
import com.lecoq.erp.entity.Producto;
import com.lecoq.erp.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS', 'MAQUILA')")
    public ResponseEntity<ApiResponse> getAllProductos() {
        try {
            List<Producto> productos = productoService.findAllActiveOrderByName();
            return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", productos));
        } catch (Exception e) {
            log.error("Error obteniendo productos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo productos: " + e.getMessage()));
        }
    }

    @GetMapping("/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllProductosIncludingInactive() {
        try {
            List<Producto> productos = productoService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Todos los productos obtenidos exitosamente", productos));
        } catch (Exception e) {
            log.error("Error obteniendo todos los productos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo productos: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS', 'MAQUILA')")
    public ResponseEntity<ApiResponse> getProductoById(@PathVariable Long id) {
        try {
            Optional<Producto> producto = productoService.findById(id);
            if (producto.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Producto encontrado", producto.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Producto no encontrado"));
            }
        } catch (Exception e) {
            log.error("Error obteniendo producto por ID: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo producto: " + e.getMessage()));
        }
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS', 'MAQUILA')")
    public ResponseEntity<ApiResponse> buscarProductos(@RequestParam String nombre) {
        try {
            List<Producto> productos = productoService.findByNombre(nombre);
            return ResponseEntity.ok(ApiResponse.success("Productos encontrados", productos));
        } catch (Exception e) {
            log.error("Error buscando productos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error buscando productos: " + e.getMessage()));
        }
    }

    @GetMapping("/stock-bajo")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> getProductosConStockBajo() {
        try {
            List<Producto> productos = productoService.findProductosConStockBajo();
            return ResponseEntity.ok(ApiResponse.success("Productos con stock bajo obtenidos", productos));
        } catch (Exception e) {
            log.error("Error obteniendo productos con stock bajo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo productos: " + e.getMessage()));
        }
    }

    @GetMapping("/con-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
    public ResponseEntity<ApiResponse> getProductosConStock() {
        try {
            List<Producto> productos = productoService.findProductosConStock();
            return ResponseEntity.ok(ApiResponse.success("Productos con stock obtenidos", productos));
        } catch (Exception e) {
            log.error("Error obteniendo productos con stock: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error obteniendo productos: " + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createProducto(@Valid @RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.create(producto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Producto creado exitosamente", nuevoProducto));
        } catch (Exception e) {
            log.error("Error creando producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error creando producto: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.update(id, producto);
            return ResponseEntity.ok(ApiResponse.success("Producto actualizado exitosamente", productoActualizado));
        } catch (Exception e) {
            log.error("Error actualizando producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error actualizando producto: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAQUILA')")
    public ResponseEntity<ApiResponse> actualizarStock(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer cantidad = request.get("cantidad");
            if (cantidad == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("La cantidad es requerida"));
            }
            
            productoService.actualizarStock(id, cantidad);
            return ResponseEntity.ok(ApiResponse.success("Stock actualizado exitosamente"));
        } catch (Exception e) {
            log.error("Error actualizando stock: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error actualizando stock: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteProducto(@PathVariable Long id) {
        try {
            productoService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Producto eliminado exitosamente"));
        } catch (Exception e) {
            log.error("Error eliminando producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error eliminando producto: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deactivateProducto(@PathVariable Long id) {
        try {
            productoService.deactivate(id);
            return ResponseEntity.ok(ApiResponse.success("Producto desactivado exitosamente"));
        } catch (Exception e) {
            log.error("Error desactivando producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error desactivando producto: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> activateProducto(@PathVariable Long id) {
        try {
            productoService.activate(id);
            return ResponseEntity.ok(ApiResponse.success("Producto activado exitosamente"));
        } catch (Exception e) {
            log.error("Error activando producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error activando producto: " + e.getMessage()));
        }
    }
}
