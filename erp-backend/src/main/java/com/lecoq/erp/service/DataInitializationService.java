package com.lecoq.erp.service;

import com.lecoq.erp.entity.Producto;
import com.lecoq.erp.entity.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Inicializando datos de prueba...");
        
        // Crear usuarios de prueba si no existen
        createDefaultUsers();
        
        // Crear productos de prueba si no existen
        createDefaultProducts();
        
        log.info("Datos de prueba inicializados exitosamente");
    }

    private void createDefaultUsers() {
        // Admin por defecto
        if (usuarioService.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setNombreCompleto("Administrador Sistema");
            admin.setEmail("admin@lecoq.com");
            admin.setRol(Usuario.Rol.ADMIN);
            admin.setActivo(true);
            
            usuarioService.create(admin);
            log.info("Usuario admin creado");
        }

        // Usuario de ventas
        if (usuarioService.findByUsername("ventas").isEmpty()) {
            Usuario ventas = new Usuario();
            ventas.setUsername("ventas");
            ventas.setPassword("ventas123");
            ventas.setNombreCompleto("Usuario Ventas");
            ventas.setEmail("ventas@lecoq.com");
            ventas.setRol(Usuario.Rol.VENTAS);
            ventas.setActivo(true);
            
            usuarioService.create(ventas);
            log.info("Usuario ventas creado");
        }

        // Usuario de maquila
        if (usuarioService.findByUsername("maquila").isEmpty()) {
            Usuario maquila = new Usuario();
            maquila.setUsername("maquila");
            maquila.setPassword("maquila123");
            maquila.setNombreCompleto("Usuario Maquila");
            maquila.setEmail("maquila@lecoq.com");
            maquila.setRol(Usuario.Rol.MAQUILA);
            maquila.setActivo(true);
            
            usuarioService.create(maquila);
            log.info("Usuario maquila creado");
        }
    }

    private void createDefaultProducts() {
        if (productoService.findAll().isEmpty()) {
            // Producto 1
            Producto producto1 = new Producto();
            producto1.setNombre("Energy Drink Original");
            producto1.setDescripcion("Bebida energizante sabor original");
            producto1.setPresentacion("Lata 355ml");
            producto1.setPrecio(new BigDecimal("2.50"));
            producto1.setStock(100);
            producto1.setStockMinimo(20);
            producto1.setActivo(true);
            
            productoService.create(producto1);
            log.info("Producto Energy Drink Original creado");

            // Producto 2
            Producto producto2 = new Producto();
            producto2.setNombre("Energy Drink Tropical");
            producto2.setDescripcion("Bebida energizante sabor tropical");
            producto2.setPresentacion("Lata 355ml");
            producto2.setPrecio(new BigDecimal("2.75"));
            producto2.setStock(80);
            producto2.setStockMinimo(15);
            producto2.setActivo(true);
            
            productoService.create(producto2);
            log.info("Producto Energy Drink Tropical creado");

            // Producto 3
            Producto producto3 = new Producto();
            producto3.setNombre("Energy Drink Zero");
            producto3.setDescripcion("Bebida energizante sin azúcar");
            producto3.setPresentacion("Lata 355ml");
            producto3.setPrecio(new BigDecimal("3.00"));
            producto3.setStock(60);
            producto3.setStockMinimo(10);
            producto3.setActivo(true);
            
            productoService.create(producto3);
            log.info("Producto Energy Drink Zero creado");

            // Producto 4
            Producto producto4 = new Producto();
            producto4.setNombre("Energy Drink Familiar");
            producto4.setDescripcion("Bebida energizante presentación familiar");
            producto4.setPresentacion("Botella 1L");
            producto4.setPrecio(new BigDecimal("6.50"));
            producto4.setStock(40);
            producto4.setStockMinimo(8);
            producto4.setActivo(true);
            
            productoService.create(producto4);
            log.info("Producto Energy Drink Familiar creado");

            // Producto 5
            Producto producto5 = new Producto();
            producto5.setNombre("Energy Shot");
            producto5.setDescripcion("Concentrado energizante");
            producto5.setPresentacion("Botella 60ml");
            producto5.setPrecio(new BigDecimal("4.00"));
            producto5.setStock(50);
            producto5.setStockMinimo(12);
            producto5.setActivo(true);
            
            productoService.create(producto5);
            log.info("Producto Energy Shot creado");
        }
    }
}
