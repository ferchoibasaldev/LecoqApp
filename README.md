# ERP LECOQ - Backend
---
## 🚀 Inicio Rápido

### Prerrequisitos
- Java 17+
- MySQL 8.0+
- Maven (incluido wrapper en el proyecto)

### Configuración de Base de Datos
1. Instalar y ejecutar MySQL
2. Crear base de datos:
   ```sql
   CREATE DATABASE lecoq_erp;
   ```
3. Configurar credenciales en `application.properties` (por defecto: root/root)

### Ejecutar la Aplicación
```bash
# Opción 1: Usar script incluido
./run.sh

# Opción 2: Comando directo
./mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 👥 Usuarios por Defecto

| Usuario | Contraseña | Rol | Descripción |
|---------|------------|-----|-------------|
| admin | admin123 | ADMIN | Administrador del sistema |
| ventas | ventas123 | VENTAS | Usuario de ventas |
| maquila | maquila123 | MAQUILA | Usuario de maquilado |

## 📚 Documentación

- **[API_DOCUMENTATION.md](API_DOCUMENTATION.md)** - Documentación completa de la API
- **[POSTMAN_EXAMPLES.md](POSTMAN_EXAMPLES.md)** - Ejemplos de uso con Postman/curl

## 🏗️ Arquitectura del Sistema

### Módulos Principales
- **Autenticación**: JWT + Spring Security
- **Usuarios**: Gestión de usuarios y roles
- **Productos**: Catálogo e inventario
- **Pedidos**: Gestión de pedidos mayoristas
- **Distribución**: Control de envíos
- **Maquilado**: Órdenes de producción tercerizada

### Tecnologías Utilizadas
- **Backend**: Spring Boot 3.5.6
- **Seguridad**: Spring Security + JWT
- **Base de Datos**: MySQL + JPA/Hibernate
- **Validación**: Bean Validation
- **Documentación**: OpenAPI/Swagger ready

## 🔧 Endpoints Principales

### Autenticación
```http
POST /api/auth/login    # Login y obtención de token JWT
POST /api/auth/validate # Validar token
POST /api/auth/logout   # Cerrar sesión
```

### Módulos de Negocio
```http
# Productos (ADMIN, VENTAS, MAQUILA)
GET /api/productos
POST /api/productos     # Solo ADMIN

# Pedidos (ADMIN, VENTAS)
GET /api/pedidos
POST /api/pedidos

# Distribuciones (ADMIN, VENTAS)
GET /api/distribuciones
POST /api/distribuciones

# Maquilados (ADMIN, MAQUILA)
GET /api/maquilados
POST /api/maquilados

# Usuarios (Solo ADMIN)
GET /api/usuarios
POST /api/usuarios
```

## 🔒 Seguridad y Permisos

### Roles del Sistema
- **ADMIN**: Acceso completo a todos los módulos
- **VENTAS**: Gestión de pedidos y distribuciones
- **MAQUILA**: Gestión de maquilados y stock

### Autenticación JWT
Todas las peticiones (excepto login) requieren header:
```
Authorization: Bearer <jwt_token>
```

## 📊 Flujos de Trabajo

### Proceso de Venta
1. Crear pedido → 2. Confirmar pedido → 3. Programar distribución → 4. Entregar

### Proceso de Maquilado
1. Crear orden → 2. En proceso → 3. Finalizado → 4. Recibir (actualiza stock)

## 🗄️ Estructura de Base de Datos

### Entidades Principales
- **usuarios**: Gestión de usuarios del sistema
- **productos**: Catálogo de productos
- **pedidos / detalles_pedido**: Órdenes de venta
- **distribuciones**: Control de envíos
- **maquilados / detalles_maquilado**: Órdenes de producción

## 🧪 Datos de Prueba

El sistema incluye datos de prueba que se cargan automáticamente:
- 3 usuarios (admin, ventas, maquila)
- 5 productos de bebidas energizantes
- Configuración lista para usar

## 🚀 Despliegue en Producción

### Variables de Entorno Importantes
```bash
# Base de datos
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/lecoq_erp
SPRING_DATASOURCE_USERNAME=tu_usuario
SPRING_DATASOURCE_PASSWORD=tu_password

# JWT
APP_JWT_SECRET=tu_secret_key_segura
APP_JWT_EXPIRATION=86400000

# CORS
APP_CORS_ALLOWED_ORIGINS=http://tu-frontend.com
```

### Compilar para Producción
```bash
./mvnw clean package -DskipTests
java -jar target/erp-backend-0.0.1-SNAPSHOT.jar
```

## 📞 Soporte

Para problemas o consultas:
1. Revisar logs de la aplicación
2. Verificar configuración de base de datos
3. Consultar documentación de API
4. Verificar permisos de usuario


**Desarrollado para LECOQ** - Sistema ERP de gestión integral para distribución de bebidas energizantes.
