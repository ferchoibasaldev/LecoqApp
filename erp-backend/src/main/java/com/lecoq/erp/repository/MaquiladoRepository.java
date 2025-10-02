package com.lecoq.erp.repository;

import com.lecoq.erp.entity.Maquilado;
import com.lecoq.erp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaquiladoRepository extends JpaRepository<Maquilado, Long> {
    
    Optional<Maquilado> findByNumeroOrden(String numeroOrden);
    
    List<Maquilado> findByUsuario(Usuario usuario);
    
    List<Maquilado> findByEstado(Maquilado.EstadoMaquilado estado);
    
    List<Maquilado> findByProveedorNombreContainingIgnoreCase(String proveedorNombre);
    
    @Query("SELECT m FROM Maquilado m WHERE m.fechaOrden BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fechaOrden DESC")
    List<Maquilado> findByFechaOrdenBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                           @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT m FROM Maquilado m WHERE m.estado IN (:estados) ORDER BY m.fechaOrden DESC")
    List<Maquilado> findByEstadoIn(@Param("estados") List<Maquilado.EstadoMaquilado> estados);
    
    @Query("SELECT m FROM Maquilado m WHERE m.usuario.id = :usuarioId ORDER BY m.fechaOrden DESC")
    List<Maquilado> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
