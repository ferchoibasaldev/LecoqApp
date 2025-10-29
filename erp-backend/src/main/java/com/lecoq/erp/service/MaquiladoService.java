package com.lecoq.erp.service;

import com.lecoq.erp.entity.DetalleMaquilado;
import com.lecoq.erp.entity.Maquilado;
import com.lecoq.erp.entity.Usuario;
import com.lecoq.erp.repository.DetalleMaquiladoRepository;
import com.lecoq.erp.repository.MaquiladoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MaquiladoService {

    private final MaquiladoRepository maquiladoRepository;
    private final DetalleMaquiladoRepository detalleMaquiladoRepository;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public List<Maquilado> findAll() {
        return maquiladoRepository.findAll();
    }

    public Optional<Maquilado> findById(Long id) {
        return maquiladoRepository.findById(id);
    }

    public Optional<Maquilado> findByNumeroOrden(String numeroOrden) {
        return maquiladoRepository.findByNumeroOrden(numeroOrden);
    }

    public List<Maquilado> findByUsuario(Usuario usuario) {
        return maquiladoRepository.findByUsuario(usuario);
    }

    public List<Maquilado> findByEstado(Maquilado.EstadoMaquilado estado) {
        return maquiladoRepository.findByEstado(estado);
    }

    public List<Maquilado> findByProveedorNombre(String proveedorNombre) {
        return maquiladoRepository.findByProveedorNombreContainingIgnoreCase(proveedorNombre);
    }

    public List<Maquilado> findByUsuarioId(Long usuarioId) {
        return maquiladoRepository.findByUsuarioId(usuarioId);
    }

    public List<Maquilado> findByFechaOrdenBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return maquiladoRepository.findByFechaOrdenBetween(fechaInicio, fechaFin);
    }

    public List<Maquilado> findByEstadoIn(List<Maquilado.EstadoMaquilado> estados) {
        return maquiladoRepository.findByEstadoIn(estados);
    }

    public Maquilado create(Maquilado maquilado, Long usuarioId) {
        var usuario = usuarioService.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));
        maquilado.setUsuario(usuario);

        if (maquilado.getDetalles() == null) {
            maquilado.setDetalles(new ArrayList<>()); // evita NPE
        }

        boolean hayDetalles = !maquilado.getDetalles().isEmpty();

        // Si NO hay detalles: NO recalcules ni pises el costoTotal si ya vino > 0 (caso seeder)
        if (!hayDetalles) {
            if (maquilado.getCostoTotal() == null || maquilado.getCostoTotal().compareTo(BigDecimal.ZERO) <= 0) {
                // puedes lanzar excepción específica, o dejar que Bean Validation la dispare
                // throw new RuntimeException("El costo total debe ser mayor a 0");
            }
            return maquiladoRepository.save(maquilado);
        }

        // Si HAY detalles: calcula costoTotal a partir de las líneas
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleMaquilado det : maquilado.getDetalles()) {
            // …(tu lógica para setear producto, cantidades, costo unitario/subtotal, etc.)
            // total = total.add(det.getSubtotal());
        }
        maquilado.setCostoTotal(total);

        return maquiladoRepository.save(maquilado);
    }

    public Maquilado update(Long id, Maquilado maquilado) {
        Maquilado existente = maquiladoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maquilado no encontrado"));

        existente.setProveedorNombre(maquilado.getProveedorNombre());
        existente.setProveedorRuc(maquilado.getProveedorRuc());
        existente.setProveedorContacto(maquilado.getProveedorContacto());
        existente.setProveedorTelefono(maquilado.getProveedorTelefono());
        existente.setFechaEntregaEstimada(maquilado.getFechaEntregaEstimada());
        existente.setObservaciones(maquilado.getObservaciones());

        return maquiladoRepository.save(existente);
    }

    public Maquilado cambiarEstado(Long id, Maquilado.EstadoMaquilado nuevoEstado) {
        Maquilado maquilado = maquiladoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maquilado no encontrado"));

        maquilado.setEstado(nuevoEstado);

        // Si se marca como finalizado, registrar fecha de entrega real
        if (nuevoEstado == Maquilado.EstadoMaquilado.FINALIZADO) {
            maquilado.setFechaEntregaReal(LocalDateTime.now());
        }

        return maquiladoRepository.save(maquilado);
    }

    public Maquilado recibirMaquilado(Long id) {
        Maquilado maquilado = maquiladoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maquilado no encontrado"));

        if (maquilado.getEstado() != Maquilado.EstadoMaquilado.FINALIZADO) {
            throw new RuntimeException("Solo se pueden recibir maquilados finalizados");
        }

        // Actualizar stock de productos
        for (DetalleMaquilado detalle : maquilado.getDetalles()) {
            if (detalle.getCantidadRecibida() > 0) {
                productoService.incrementarStock(detalle.getProducto().getId(), detalle.getCantidadRecibida());
            }
        }

        maquilado.setEstado(Maquilado.EstadoMaquilado.RECIBIDO);
        return maquiladoRepository.save(maquilado);
    }

    public Maquilado actualizarCantidadesRecibidas(Long id, List<DetalleMaquilado> detallesActualizados) {
        Maquilado maquilado = maquiladoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maquilado no encontrado"));

        if (maquilado.getEstado() != Maquilado.EstadoMaquilado.FINALIZADO) {
            throw new RuntimeException("Solo se pueden actualizar cantidades de maquilados finalizados");
        }

        for (DetalleMaquilado detalleActualizado : detallesActualizados) {
            DetalleMaquilado detalleExistente = maquilado.getDetalles().stream()
                    .filter(d -> d.getId().equals(detalleActualizado.getId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Detalle de maquilado no encontrado"));

            if (detalleActualizado.getCantidadRecibida() > detalleActualizado.getCantidadSolicitada()) {
                throw new RuntimeException("La cantidad recibida no puede ser mayor a la solicitada");
            }

            detalleExistente.setCantidadRecibida(detalleActualizado.getCantidadRecibida());
        }

        return maquiladoRepository.save(maquilado);
    }

    public void deleteById(Long id) {
        Maquilado maquilado = maquiladoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maquilado no encontrado"));

        if (maquilado.getEstado() == Maquilado.EstadoMaquilado.RECIBIDO) {
            throw new RuntimeException("No se puede eliminar un maquilado que ya fue recibido");
        }

        maquiladoRepository.deleteById(id);
    }

    public List<DetalleMaquilado> findDetallesByMaquiladoId(Long maquiladoId) {
        return detalleMaquiladoRepository.findByMaquiladoId(maquiladoId);
    }

    public void marcarComoEnProceso(Long id) {
        cambiarEstado(id, Maquilado.EstadoMaquilado.EN_PROCESO);
    }

    public void marcarComoFinalizado(Long id) {
        cambiarEstado(id, Maquilado.EstadoMaquilado.FINALIZADO);
    }

    public void marcarComoCancelado(Long id) {
        cambiarEstado(id, Maquilado.EstadoMaquilado.CANCELADO);
    }
}
