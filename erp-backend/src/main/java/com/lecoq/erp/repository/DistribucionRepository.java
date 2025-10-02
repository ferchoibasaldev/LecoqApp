package com.lecoq.erp.repository;

import com.lecoq.erp.entity.Distribucion;
import com.lecoq.erp.entity.Pedido;
import com.lecoq.erp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DistribucionRepository extends JpaRepository<Distribucion, Long> {
    
    Optional<Distribucion> findByPedido(Pedido pedido);
    
    List<Distribucion> findByUsuario(Usuario usuario);
    
    List<Distribucion> findByEstado(Distribucion.EstadoDistribucion estado);
    
    List<Distribucion> findByChoferNombreContainingIgnoreCase(String choferNombre);
    
    List<Distribucion> findByVehiculoPlaca(String vehiculoPlaca);
    
    @Query("SELECT d FROM Distribucion d WHERE d.fechaSalida BETWEEN :fechaInicio AND :fechaFin ORDER BY d.fechaSalida DESC")
    List<Distribucion> findByFechaSalidaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                               @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT d FROM Distribucion d WHERE d.estado IN (:estados) ORDER BY d.fechaSalida DESC")
    List<Distribucion> findByEstadoIn(@Param("estados") List<Distribucion.EstadoDistribucion> estados);
    
    @Query("SELECT d FROM Distribucion d WHERE d.usuario.id = :usuarioId ORDER BY d.fechaSalida DESC")
    List<Distribucion> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
