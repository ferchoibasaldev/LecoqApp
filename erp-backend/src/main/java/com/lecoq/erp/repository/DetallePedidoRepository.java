package com.lecoq.erp.repository;

import com.lecoq.erp.entity.DetallePedido;
import com.lecoq.erp.entity.Pedido;
import com.lecoq.erp.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    
    List<DetallePedido> findByPedido(Pedido pedido);
    
    List<DetallePedido> findByProducto(Producto producto);
    
    @Query("SELECT dp FROM DetallePedido dp WHERE dp.pedido.id = :pedidoId")
    List<DetallePedido> findByPedidoId(@Param("pedidoId") Long pedidoId);
    
    @Query("SELECT dp FROM DetallePedido dp WHERE dp.producto.id = :productoId")
    List<DetallePedido> findByProductoId(@Param("productoId") Long productoId);
}
