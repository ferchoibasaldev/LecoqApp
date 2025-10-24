package com.lecoq.erp.service;

import com.lecoq.erp.entity.Pedido;
import com.lecoq.erp.entity.Distribucion;
import com.lecoq.erp.entity.Maquilado;
import com.lecoq.erp.entity.DetallePedido;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import com.lecoq.erp.entity.Producto;
import com.lecoq.erp.entity.Usuario;
import com.lecoq.erp.repository.DetallePedidoRepository;
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
    private final PedidoService pedidoService;
    private final DistribucionService distribucionService;
    private final MaquiladoService maquiladoService;
    private final DetallePedidoRepository detallePedidoRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Inicializando datos de prueba...");
        
        // Crear usuarios de prueba si no existen
        createDefaultUsers();
        
        // Crear productos de prueba si no existen
        createDefaultProducts();

        createDefaultPedidos();          // crea 3 pedidos
        createDefaultDistribuciones();   // crea 2 distribuciones ligadas a pedidos
        createDefaultMaquilados();       // crea 3 órdenes de maquila
        
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

    private void createDefaultPedidos() {
        if (!pedidoService.findAll().isEmpty()) return;

        // resolvemos usuarios por username
        var admin  = usuarioService.findByUsername("admin").orElseThrow();
        var ventas = usuarioService.findByUsername("ventas").orElseThrow();

        // ---------- PEDIDO 1 (usuario: ventas) ----------
        Pedido p1 = new Pedido();
        p1.setNumeroPedido("PED-2025-0001");
        p1.setClienteNombre("Distribuidora Andina SAC");
        p1.setClienteRuc("20601234567");
        p1.setClienteTelefono("01 123-4567");
        p1.setClienteDireccion("Av. Los Próceres 123, Lima");
        p1.setFechaPedido(LocalDateTime.now().minusDays(2));
        p1.setEstado(Pedido.EstadoPedido.PENDIENTE);
        p1.setObservaciones("Entrega en almacén principal");
        p1.setTotal(new BigDecimal("47.50")); // 👈 ¡total NO nulo!
        pedidoService.create(p1, ventas.getId());
        log.info("Pedido PED-2025-0001 creado (ventas)");

        // ---------- PEDIDO 2 (usuario: admin) ----------
        Pedido p2 = new Pedido();
        p2.setNumeroPedido("PED-2025-0002");
        p2.setClienteNombre("Mayorista Norte SRL");
        p2.setClienteRuc("20123456789");
        p2.setClienteTelefono("044 555-777");
        p2.setClienteDireccion("Av. Industrial 890, Trujillo");
        p2.setFechaPedido(LocalDateTime.now().minusDays(1));
        p2.setEstado(Pedido.EstadoPedido.EN_PREPARACION);
        p2.setObservaciones("Prioridad alta");
        p2.setTotal(new BigDecimal("50.00"));
        pedidoService.create(p2, admin.getId());
        log.info("Pedido PED-2025-0002 creado (admin)");

        // ---------- PEDIDO 3 (usuario: ventas) ----------
        Pedido p3 = new Pedido();
        p3.setNumeroPedido("PED-2025-0003");
        p3.setClienteNombre("Bodega Central");
        p3.setClienteRuc("10456789012");
        p3.setClienteTelefono("987 111 222");
        p3.setClienteDireccion("Calle Comercio 12, Lima");
        p3.setFechaPedido(LocalDateTime.now().minusHours(6));
        p3.setEstado(Pedido.EstadoPedido.PENDIENTE);
        p3.setObservaciones("Cliente paga contra entrega");
        p3.setTotal(new BigDecimal("24.00"));
        pedidoService.create(p3, ventas.getId());
        log.info("Pedido PED-2025-0003 creado (ventas)");
    }

    private void createDefaultDistribuciones() {
        if (!distribucionService.findAll().isEmpty()) return;

        var admin  = usuarioService.findByUsername("admin").orElseThrow();
        var ventas = usuarioService.findByUsername("ventas").orElseThrow();

        // buscamos los pedidos por número (ya creados arriba)
        var ped1 = pedidoService.findByNumeroPedido("PED-2025-0001").orElseThrow();
        var ped2 = pedidoService.findByNumeroPedido("PED-2025-0002").orElseThrow();

        // ---------- Distribución para PED-2025-0001 (ventas) ----------
        Distribucion d1 = new Distribucion();
        d1.setDireccionEntrega("Jr. Lima 345, San Isidro");
        d1.setFechaSalida(LocalDateTime.now().minusDays(1).withHour(9).withMinute(0));
        d1.setEstado(Distribucion.EstadoDistribucion.EN_RUTA);
        d1.setChoferNombre("Carlos Huamán");
        d1.setChoferTelefono("987654321");
        d1.setVehiculoModelo("Furgón Sprinter");
        d1.setVehiculoPlaca("ABC-123");
        d1.setObservaciones("Entregar antes de las 3pm");
        distribucionService.create(d1, ped1.getId(), ventas.getId());
        log.info("Distribución creada para PED-2025-0001");

        // ---------- Distribución para PED-2025-0002 (admin) ----------
        Distribucion d2 = new Distribucion();
        d2.setDireccionEntrega("Parque Industrial Mz A Lote 8, Trujillo");
        d2.setFechaSalida(LocalDateTime.now().withHour(8).withMinute(30));
        d2.setEstado(Distribucion.EstadoDistribucion.PROGRAMADO);
        d2.setChoferNombre("Luis Paredes");
        d2.setChoferTelefono("991112223");
        d2.setVehiculoModelo("Camión NPR");
        d2.setVehiculoPlaca("XYZ-456");
        d2.setObservaciones("Coordinar con almacén de cliente");
        distribucionService.create(d2, ped2.getId(), admin.getId());
        log.info("Distribución creada para PED-2025-0002");
    }

    private void createDefaultMaquilados() {
        if (!maquiladoService.findAll().isEmpty()) return;

        var admin  = usuarioService.findByUsername("admin").orElseThrow();
        var maquila = usuarioService.findByUsername("maquila").orElseThrow();

        // ---------- MQ-1 (maquila) ----------
        Maquilado m1 = new Maquilado();
        m1.setNumeroOrden("MQ-1");
        m1.setProveedorNombre("Servicios Tercerizados SAC");
        m1.setProveedorRuc("20609999888");
        m1.setProveedorTelefono("01 555-8888");
        m1.setFechaOrden(LocalDateTime.now().minusDays(3));
        m1.setEstado(Maquilado.EstadoMaquilado.PENDIENTE);
        m1.setObservaciones("Lote inicial");
        m1.setCostoTotal(new BigDecimal("350.00")); // 👈 NO nulo
        maquiladoService.create(m1, maquila.getId());
        log.info("Maquilado MQ-1 creado (maquila)");

        // ---------- MQ-2 (admin) ----------
        Maquilado m2 = new Maquilado();
        m2.setNumeroOrden("MQ-2");
        m2.setProveedorNombre("Producciones Industriales EIRL");
        m2.setProveedorRuc("20556667771");
        m2.setProveedorTelefono("01 444-7777");
        m2.setFechaOrden(LocalDateTime.now().minusDays(2));
        m2.setEstado(Maquilado.EstadoMaquilado.EN_PROCESO);
        m2.setObservaciones("Urgente");
        m2.setCostoTotal(new BigDecimal("500.00"));
        maquiladoService.create(m2, admin.getId());
        log.info("Maquilado MQ-2 creado (admin)");

        // ---------- MQ-3 (maquila) ----------
        Maquilado m3 = new Maquilado();
        m3.setNumeroOrden("MQ-3");
        m3.setProveedorNombre("Outsource Fabril SAC");
        m3.setProveedorRuc("20601234001");
        m3.setProveedorTelefono("01 777-2222");
        m3.setFechaOrden(LocalDateTime.now().minusDays(1));
        m3.setEstado(Maquilado.EstadoMaquilado.FINALIZADO);
        m3.setObservaciones("Entrega completa");
        m3.setCostoTotal(new BigDecimal("420.00"));
        maquiladoService.create(m3, maquila.getId());
        log.info("Maquilado MQ-3 creado (maquila)");
    }




}
