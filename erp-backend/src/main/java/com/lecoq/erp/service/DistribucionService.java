package com.lecoq.erp.service;

import com.lecoq.erp.entity.Distribucion;
import com.lecoq.erp.entity.Pedido;
import com.lecoq.erp.entity.Usuario;
import com.lecoq.erp.repository.DistribucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DistribucionService {

    private final DistribucionRepository distribucionRepository;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    public List<Distribucion> findAll() {
        return distribucionRepository.findAll();
    }

    public Optional<Distribucion> findById(Long id) {
        return distribucionRepository.findById(id);
    }

    public Optional<Distribucion> findByPedido(Pedido pedido) {
        return distribucionRepository.findByPedido(pedido);
    }

    public List<Distribucion> findByUsuario(Usuario usuario) {
        return distribucionRepository.findByUsuario(usuario);
    }

    public List<Distribucion> findByEstado(Distribucion.EstadoDistribucion estado) {
        return distribucionRepository.findByEstado(estado);
    }

    public List<Distribucion> findByChoferNombre(String choferNombre) {
        return distribucionRepository.findByChoferNombreContainingIgnoreCase(choferNombre);
    }

    public List<Distribucion> findByVehiculoPlaca(String vehiculoPlaca) {
        return distribucionRepository.findByVehiculoPlaca(vehiculoPlaca);
    }

    public List<Distribucion> findByUsuarioId(Long usuarioId) {
        return distribucionRepository.findByUsuarioId(usuarioId);
    }

    public List<Distribucion> findByFechaSalidaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return distribucionRepository.findByFechaSalidaBetween(fechaInicio, fechaFin);
    }

    public List<Distribucion> findByEstadoIn(List<Distribucion.EstadoDistribucion> estados) {
        return distribucionRepository.findByEstadoIn(estados);
    }

    public Distribucion create(Distribucion distribucion, Long pedidoId, Long usuarioId) {
        Pedido pedido = pedidoService.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        Usuario usuario = usuarioService.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que el pedido esté confirmado REVISAR SI ES NECESARIO
//        if (pedido.getEstado() != Pedido.EstadoPedido.CONFIRMADO) {
//            throw new RuntimeException("Solo se pueden programar distribuciones para pedidos confirmados");
//        }

        // Verificar que no exista ya una distribución para este pedido
        if (distribucionRepository.findByPedido(pedido).isPresent()) {
            throw new RuntimeException("Ya existe una distribución programada para este pedido");
        }

        distribucion.setPedido(pedido);
        distribucion.setUsuario(usuario);

        // Cambiar estado del pedido a EN_PREPARACION
        pedidoService.cambiarEstado(pedidoId, Pedido.EstadoPedido.EN_PREPARACION);

        return distribucionRepository.save(distribucion);
    }

    public Distribucion update(Long id, Distribucion distribucion) {
        Distribucion existente = distribucionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Distribución no encontrada"));

        existente.setChoferNombre(distribucion.getChoferNombre());
        existente.setChoferTelefono(distribucion.getChoferTelefono());
        existente.setVehiculoPlaca(distribucion.getVehiculoPlaca());
        existente.setVehiculoModelo(distribucion.getVehiculoModelo());
        existente.setFechaSalida(distribucion.getFechaSalida());
        existente.setDireccionEntrega(distribucion.getDireccionEntrega());
        existente.setObservaciones(distribucion.getObservaciones());

        return distribucionRepository.save(existente);
    }

    public Distribucion cambiarEstado(Long id, Distribucion.EstadoDistribucion nuevoEstado) {
        Distribucion distribucion = distribucionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Distribución no encontrada"));

        Distribucion.EstadoDistribucion estadoAnterior = distribucion.getEstado();
        distribucion.setEstado(nuevoEstado);

        // Actualizar estado del pedido según el estado de distribución
        Pedido pedido = distribucion.getPedido();
        
        switch (nuevoEstado) {
            case EN_RUTA:
                if (estadoAnterior == Distribucion.EstadoDistribucion.PROGRAMADO) {
                    pedidoService.cambiarEstado(pedido.getId(), Pedido.EstadoPedido.ENVIADO);
                }
                break;
            case ENTREGADO:
                distribucion.setFechaEntrega(LocalDateTime.now());
                pedidoService.cambiarEstado(pedido.getId(), Pedido.EstadoPedido.ENTREGADO);
                break;
            case FALLIDO:
                // El pedido vuelve a estar confirmado para reprogramar entrega
                pedidoService.cambiarEstado(pedido.getId(), Pedido.EstadoPedido.CONFIRMADO);
                break;
        }

        return distribucionRepository.save(distribucion);
    }

    public void deleteById(Long id) {
        Distribucion distribucion = distribucionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Distribución no encontrada"));

        // Si la distribución no se ha iniciado, volver el pedido a confirmado
        if (distribucion.getEstado() == Distribucion.EstadoDistribucion.PROGRAMADO) {
            pedidoService.cambiarEstado(distribucion.getPedido().getId(), Pedido.EstadoPedido.CONFIRMADO);
        }

        distribucionRepository.deleteById(id);
    }

    public void marcarComoEntregado(Long id) {
        cambiarEstado(id, Distribucion.EstadoDistribucion.ENTREGADO);
    }

    public void marcarComoEnRuta(Long id) {
        cambiarEstado(id, Distribucion.EstadoDistribucion.EN_RUTA);
    }

    public void marcarComoFallido(Long id) {
        cambiarEstado(id, Distribucion.EstadoDistribucion.FALLIDO);
    }
}
