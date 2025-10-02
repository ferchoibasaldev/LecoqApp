package com.lecoq.erp.service;

import com.lecoq.erp.entity.Producto;
import com.lecoq.erp.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public List<Producto> findAllActive() {
        return productoRepository.findByActivoTrue();
    }

    public List<Producto> findAllActiveOrderByName() {
        return productoRepository.findByActivoTrueOrderByNombreAsc();
    }

    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> findByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> findProductosConStockBajo() {
        return productoRepository.findProductosConStockBajo();
    }

    public List<Producto> findProductosConStock() {
        return productoRepository.findProductosConStock();
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto create(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto update(Long id, Producto producto) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        existente.setNombre(producto.getNombre());
        existente.setDescripcion(producto.getDescripcion());
        existente.setPresentacion(producto.getPresentacion());
        existente.setPrecio(producto.getPrecio());
        existente.setStock(producto.getStock());
        existente.setStockMinimo(producto.getStockMinimo());
        existente.setActivo(producto.getActivo());

        return productoRepository.save(existente);
    }

    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    public void deactivate(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    public void activate(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setActivo(true);
        productoRepository.save(producto);
    }

    public void actualizarStock(Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        
        producto.setStock(nuevoStock);
        productoRepository.save(producto);
    }

    public void reducirStock(Long productoId, Integer cantidad) {
        actualizarStock(productoId, -cantidad);
    }

    public void incrementarStock(Long productoId, Integer cantidad) {
        actualizarStock(productoId, cantidad);
    }

    public boolean validarStock(Long productoId, Integer cantidadRequerida) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return producto.getStock() >= cantidadRequerida;
    }
}
