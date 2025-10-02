# API Documentation - ERP LECOQ Backend

## Descripción General

Sistema ERP completo para la empresa LECOQ, especializada en la venta al por mayor de bebidas energizantes. El sistema maneja ventas, distribución, maquilado e inventario.

## Configuración Inicial

### Base de Datos
Asegúrate de tener MySQL corriendo y crea la base de datos:
```sql
CREATE DATABASE lecoq_erp;
```

### Usuarios por Defecto
El sistema crea automáticamente usuarios de prueba:

| Username | Password | Rol | Email |
|----------|----------|-----|-------|
| admin | admin123 | ADMIN | admin@lecoq.com |
| ventas | ventas123 | VENTAS | ventas@lecoq.com |
| maquila | maquila123 | MAQUILA | maquila@lecoq.com |

## Autenticación

### POST /api/auth/login
Autenticar usuario y obtener token JWT.

**Request:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "id": 1,
    "username": "admin",
    "nombreCompleto": "Administrador Sistema",
    "email": "admin@lecoq.com",
    "rol": "ADMIN"
  }
}
```

### POST /api/auth/validate
Validar token JWT.

**Headers:**
```
Authorization: Bearer <token>
```

### POST /api/auth/logout
Cerrar sesión (invalida el contexto de seguridad).

---

## Usuarios (Solo ADMIN)

### GET /api/usuarios
Obtener todos los usuarios.

### GET /api/usuarios/activos
Obtener usuarios activos.

### GET /api/usuarios/{id}
Obtener usuario por ID.

### GET /api/usuarios/rol/{rol}
Obtener usuarios por rol (ADMIN, VENTAS, MAQUILA).

### POST /api/usuarios
Crear nuevo usuario.

**Request:**
```json
{
  "username": "nuevo_usuario",
  "password": "password123",
  "nombreCompleto": "Nombre Completo",
  "email": "usuario@lecoq.com",
  "rol": "VENTAS",
  "activo": true
}
```

### PUT /api/usuarios/{id}
Actualizar usuario.

### DELETE /api/usuarios/{id}
Eliminar usuario.

### PUT /api/usuarios/{id}/desactivar
Desactivar usuario.

### PUT /api/usuarios/{id}/activar
Activar usuario.

---

## Productos (ADMIN, VENTAS, MAQUILA)

### GET /api/productos
Obtener productos activos ordenados por nombre.

### GET /api/productos/todos (Solo ADMIN)
Obtener todos los productos incluyendo inactivos.

### GET /api/productos/{id}
Obtener producto por ID.

### GET /api/productos/buscar?nombre={nombre}
Buscar productos por nombre.

### GET /api/productos/stock-bajo (ADMIN, MAQUILA)
Obtener productos con stock bajo.

### GET /api/productos/con-stock (ADMIN, VENTAS)
Obtener productos con stock disponible.

### POST /api/productos (Solo ADMIN)
Crear nuevo producto.

**Request:**
```json
{
  "nombre": "Energy Drink Nuevo",
  "descripcion": "Nueva bebida energizante",
  "presentacion": "Lata 355ml",
  "precio": 2.80,
  "stock": 50,
  "stockMinimo": 10,
  "activo": true
}
```

### PUT /api/productos/{id} (Solo ADMIN)
Actualizar producto.

### PUT /api/productos/{id}/stock (ADMIN, MAQUILA)
Actualizar stock del producto.

**Request:**
```json
{
  "cantidad": 10
}
```

### DELETE /api/productos/{id} (Solo ADMIN)
Eliminar producto.

### PUT /api/productos/{id}/desactivar (Solo ADMIN)
Desactivar producto.

### PUT /api/productos/{id}/activar (Solo ADMIN)
Activar producto.

---

## Pedidos (ADMIN, VENTAS)

### GET /api/pedidos
Obtener pedidos (ADMIN ve todos, VENTAS ve solo los suyos).

### GET /api/pedidos/{id}
Obtener pedido por ID.

### GET /api/pedidos/numero/{numeroPedido}
Obtener pedido por número.

### GET /api/pedidos/estado/{estado}
Obtener pedidos por estado (PENDIENTE, CONFIRMADO, EN_PREPARACION, ENVIADO, ENTREGADO, CANCELADO).

### GET /api/pedidos/cliente?nombre={nombre}
Buscar pedidos por nombre de cliente.

### GET /api/pedidos/fecha?fechaInicio={fecha}&fechaFin={fecha}
Obtener pedidos por rango de fechas.

### GET /api/pedidos/{id}/detalles
Obtener detalles de un pedido.

### POST /api/pedidos
Crear nuevo pedido.

**Request:**
```json
{
  "clienteNombre": "Cliente Mayorista S.A.",
  "clienteRuc": "20123456789",
  "clienteDireccion": "Av. Principal 123",
  "clienteTelefono": "999888777",
  "fechaEntregaEstimada": "2024-12-15T10:00:00",
  "observaciones": "Entrega urgente",
  "detalles": [
    {
      "producto": {"id": 1},
      "cantidad": 50
    },
    {
      "producto": {"id": 2},
      "cantidad": 30
    }
  ]
}
```

### PUT /api/pedidos/{id}
Actualizar pedido.

### PUT /api/pedidos/{id}/estado
Cambiar estado del pedido.

**Request:**
```json
{
  "estado": "CONFIRMADO"
}
```

### DELETE /api/pedidos/{id} (Solo ADMIN)
Eliminar pedido.

---

## Distribuciones (ADMIN, VENTAS)

### GET /api/distribuciones
Obtener distribuciones.

### GET /api/distribuciones/{id}
Obtener distribución por ID.

### GET /api/distribuciones/estado/{estado}
Obtener distribuciones por estado (PROGRAMADO, EN_RUTA, ENTREGADO, FALLIDO).

### GET /api/distribuciones/chofer?nombre={nombre}
Buscar distribuciones por chofer.

### GET /api/distribuciones/vehiculo/{placa}
Buscar distribuciones por placa de vehículo.

### GET /api/distribuciones/fecha?fechaInicio={fecha}&fechaFin={fecha}
Obtener distribuciones por rango de fechas.

### POST /api/distribuciones?pedidoId={id}
Crear nueva distribución.

**Request:**
```json
{
  "choferNombre": "Juan Pérez",
  "choferTelefono": "999111222",
  "vehiculoPlaca": "ABC-123",
  "vehiculoModelo": "Toyota Hiace",
  "fechaSalida": "2024-12-10T08:00:00",
  "direccionEntrega": "Av. Los Olivos 456",
  "observaciones": "Llamar antes de llegar"
}
```

### PUT /api/distribuciones/{id}
Actualizar distribución.

### PUT /api/distribuciones/{id}/estado
Cambiar estado de la distribución.

**Request:**
```json
{
  "estado": "EN_RUTA"
}
```

### PUT /api/distribuciones/{id}/entregar
Marcar distribución como entregada.

### PUT /api/distribuciones/{id}/en-ruta
Marcar distribución como en ruta.

### PUT /api/distribuciones/{id}/fallido
Marcar distribución como fallida.

### DELETE /api/distribuciones/{id} (Solo ADMIN)
Eliminar distribución.

---

## Maquilados (ADMIN, MAQUILA)

### GET /api/maquilados
Obtener maquilados.

### GET /api/maquilados/{id}
Obtener maquilado por ID.

### GET /api/maquilados/numero/{numeroOrden}
Obtener maquilado por número de orden.

### GET /api/maquilados/estado/{estado}
Obtener maquilados por estado (PENDIENTE, EN_PROCESO, FINALIZADO, RECIBIDO, CANCELADO).

### GET /api/maquilados/proveedor?nombre={nombre}
Buscar maquilados por proveedor.

### GET /api/maquilados/fecha?fechaInicio={fecha}&fechaFin={fecha}
Obtener maquilados por rango de fechas.

### GET /api/maquilados/{id}/detalles
Obtener detalles de un maquilado.

### POST /api/maquilados
Crear nuevo maquilado.

**Request:**
```json
{
  "proveedorNombre": "Maquiladora XYZ S.A.C.",
  "proveedorRuc": "20987654321",
  "proveedorContacto": "María García",
  "proveedorTelefono": "999333444",
  "fechaEntregaEstimada": "2024-12-20T14:00:00",
  "observaciones": "Producción urgente",
  "detalles": [
    {
      "producto": {"id": 1},
      "cantidadSolicitada": 1000,
      "costoUnitario": 1.50
    },
    {
      "producto": {"id": 2},
      "cantidadSolicitada": 800,
      "costoUnitario": 1.65
    }
  ]
}
```

### PUT /api/maquilados/{id}
Actualizar maquilado.

### PUT /api/maquilados/{id}/estado
Cambiar estado del maquilado.

**Request:**
```json
{
  "estado": "EN_PROCESO"
}
```

### PUT /api/maquilados/{id}/recibir
Recibir maquilado (actualiza stock automáticamente).

### PUT /api/maquilados/{id}/cantidades-recibidas
Actualizar cantidades recibidas antes de confirmar recepción.

**Request:**
```json
[
  {
    "id": 1,
    "cantidadRecibida": 950
  },
  {
    "id": 2,
    "cantidadRecibida": 800
  }
]
```

### PUT /api/maquilados/{id}/en-proceso
Marcar como en proceso.

### PUT /api/maquilados/{id}/finalizar
Marcar como finalizado.

### PUT /api/maquilados/{id}/cancelar
Marcar como cancelado.

### DELETE /api/maquilados/{id} (Solo ADMIN)
Eliminar maquilado.

---

## Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK - Operación exitosa |
| 201 | Created - Recurso creado exitosamente |
| 400 | Bad Request - Error en la solicitud |
| 401 | Unauthorized - No autorizado |
| 403 | Forbidden - Acceso prohibido |
| 404 | Not Found - Recurso no encontrado |
| 500 | Internal Server Error - Error interno del servidor |

## Estructura de Respuesta Estándar

Todas las respuestas siguen esta estructura:

```json
{
  "success": true/false,
  "message": "Mensaje descriptivo",
  "data": {} // Datos de respuesta (opcional)
}
```

## Autenticación JWT

Para endpoints protegidos, incluir el header:
```
Authorization: Bearer <token_jwt>
```

## Estados del Sistema

### Estados de Pedido
- PENDIENTE: Pedido creado, esperando confirmación
- CONFIRMADO: Pedido confirmado, stock reservado
- EN_PREPARACION: Pedido en preparación para envío
- ENVIADO: Pedido enviado al cliente
- ENTREGADO: Pedido entregado exitosamente
- CANCELADO: Pedido cancelado

### Estados de Distribución
- PROGRAMADO: Distribución programada
- EN_RUTA: Vehículo en camino
- ENTREGADO: Entrega completada
- FALLIDO: Entrega fallida

### Estados de Maquilado
- PENDIENTE: Orden enviada al proveedor
- EN_PROCESO: Proveedor procesando la orden
- FINALIZADO: Producción completada
- RECIBIDO: Mercadería recibida y stock actualizado
- CANCELADO: Orden cancelada

## Roles y Permisos

### ADMIN
- Acceso completo a todos los módulos
- Gestión de usuarios
- Eliminación de registros

### VENTAS
- Gestión de pedidos
- Gestión de distribuciones
- Consulta de productos con stock

### MAQUILA
- Gestión de maquilados
- Actualización de stock de productos
- Consulta de productos con stock bajo
