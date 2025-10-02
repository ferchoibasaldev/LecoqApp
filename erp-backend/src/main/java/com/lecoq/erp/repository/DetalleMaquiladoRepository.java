package com.lecoq.erp.repository;

import com.lecoq.erp.entity.DetalleMaquilado;
import com.lecoq.erp.entity.Maquilado;
import com.lecoq.erp.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleMaquiladoRepository extends JpaRepository<DetalleMaquilado, Long> {
    
    List<DetalleMaquilado> findByMaquilado(Maquilado maquilado);
    
    List<DetalleMaquilado> findByProducto(Producto producto);
    
    @Query("SELECT dm FROM DetalleMaquilado dm WHERE dm.maquilado.id = :maquiladoId")
    List<DetalleMaquilado> findByMaquiladoId(@Param("maquiladoId") Long maquiladoId);
    
    @Query("SELECT dm FROM DetalleMaquilado dm WHERE dm.producto.id = :productoId")
    List<DetalleMaquilado> findByProductoId(@Param("productoId") Long productoId);
}
