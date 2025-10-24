package com.lecoq.erp.service;

import com.lecoq.erp.entity.DetallePedido;
import com.lecoq.erp.entity.Pedido;
import com.lecoq.erp.entity.Producto;
import com.lecoq.erp.entity.Usuario;
import com.lecoq.erp.repository.DetallePedidoRepository;
import com.lecoq.erp.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    public Optional<Pedido> findByNumeroPedido(String numeroPedido) {
        return pedidoRepository.findByNumeroPedido(numeroPedido);
    }

    public List<Pedido> findByUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario);
    }

    public List<Pedido> findByEstado(Pedido.EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public List<Pedido> findByClienteNombre(String clienteNombre) {
        return pedidoRepository.findByClienteNombreContainingIgnoreCase(clienteNombre);
    }

    public List<Pedido> findByUsuarioId(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    public List<Pedido> findByFechaPedidoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pedidoRepository.findPedidosByFechaPedidoBetween(fechaInicio, fechaFin);
    }

    public List<Pedido> findByEstadoIn(List<Pedido.EstadoPedido> estados) {
        return pedidoRepository.findByEstadoIn(estados);
    }

    public Pedido create(Pedido pedido, Long usuarioId) {
        Usuario usuario = usuarioService.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));
        pedido.setUsuario(usuario);

        if (pedido.getDetalles() == null) {
            pedido.setDetalles(new ArrayList<>()); // evita NPE
        }

        boolean hayDetalles = !pedido.getDetalles().isEmpty();

        // Si NO hay detalles: NO recalcules ni pises el total si ya vino > 0 (caso seeder)
        if (!hayDetalles) {
            // Si tampoco vino total válido, puedes decidir lanzar error:
            if (pedido.getTotal() == null || pedido.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
                // throw new RuntimeException("Pedido sin detalles debe tener un total > 0");
                // o deja que falle bean validation si tienes @Positive/@DecimalMin en la entidad
            }
            return pedidoRepository.save(pedido);
        }

        // Si HAY detalles: valida stock y calcula total desde las líneas
        BigDecimal totalCalc = BigDecimal.ZERO;

        for (DetallePedido detalle : pedido.getDetalles()) {
            // Validar stock disponible
            if (detalle.getProducto() == null || detalle.getProducto().getId() == null) {
                throw new RuntimeException("Detalle sin producto válido");
            }
            if (!productoService.validarStock(detalle.getProducto().getId(), detalle.getCantidad())) {
                throw new RuntimeException("Stock insuficiente para el producto: " +
                        detalle.getProducto().getNombre());
            }

            // Obtener producto actualizado para precio
            Producto producto = productoService.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            detalle.setProducto(producto);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setPedido(pedido);

            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()));
            detalle.setSubtotal(subtotal);
            totalCalc = totalCalc.add(subtotal);
        }

        pedido.setTotal(totalCalc); // ahora sí, con detalles, pisamos por el total calculado
        return pedidoRepository.save(pedido);
    }


    public Pedido update(Long id, Pedido pedido) {
        Pedido existente = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        existente.setClienteNombre(pedido.getClienteNombre());
        existente.setClienteRuc(pedido.getClienteRuc());
        existente.setClienteDireccion(pedido.getClienteDireccion());
        existente.setClienteTelefono(pedido.getClienteTelefono());
        existente.setFechaEntregaEstimada(pedido.getFechaEntregaEstimada());
        existente.setObservaciones(pedido.getObservaciones());

        return pedidoRepository.save(existente);
    }

    public Pedido cambiarEstado(Long id, Pedido.EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Pedido.EstadoPedido estadoAnterior = pedido.getEstado();
        pedido.setEstado(nuevoEstado);

        // Si se confirma el pedido, reducir stock
        if (nuevoEstado == Pedido.EstadoPedido.CONFIRMADO && 
            estadoAnterior == Pedido.EstadoPedido.PENDIENTE) {
            
            for (DetallePedido detalle : pedido.getDetalles()) {
                productoService.reducirStock(detalle.getProducto().getId(), detalle.getCantidad());
            }
        }

        // Si se cancela un pedido confirmado, restaurar stock
        if (nuevoEstado == Pedido.EstadoPedido.CANCELADO && 
            estadoAnterior == Pedido.EstadoPedido.CONFIRMADO) {
            
            for (DetallePedido detalle : pedido.getDetalles()) {
                productoService.incrementarStock(detalle.getProducto().getId(), detalle.getCantidad());
            }
        }

        return pedidoRepository.save(pedido);
    }

    public void deleteById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Si el pedido está confirmado, restaurar stock antes de eliminar
        if (pedido.getEstado() == Pedido.EstadoPedido.CONFIRMADO) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                productoService.incrementarStock(detalle.getProducto().getId(), detalle.getCantidad());
            }
        }

        pedidoRepository.deleteById(id);
    }

    public List<DetallePedido> findDetallesByPedidoId(Long pedidoId) {
        return detallePedidoRepository.findByPedidoId(pedidoId);
    }
}
