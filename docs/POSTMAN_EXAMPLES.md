# Ejemplos de Postman para ERP LECOQ

## Configuración Inicial

1. **Ejecutar la aplicación:**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **URL Base:** `http://localhost:8080`

3. **Base de datos:** MySQL en `localhost:3306` con base de datos `lecoq_erp`

## Colección de Postman - Ejemplos Paso a Paso

### 1. Autenticación

#### Login como Admin
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Respuesta esperada:**
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

⚠️ **Importante:** Copia el token y úsalo en todas las siguientes peticiones como header:
`Authorization: Bearer <token>`

### 2. Gestión de Productos

#### Listar Productos
```http
GET http://localhost:8080/api/productos
Authorization: Bearer <token>
```

#### Crear Producto Nuevo
```http
POST http://localhost:8080/api/productos
Authorization: Bearer <token>
Content-Type: application/json

{
  "nombre": "Energy Drink Citrus",
  "descripcion": "Bebida energizante sabor cítrico",
  "presentacion": "Lata 355ml",
  "precio": 2.90,
  "stock": 75,
  "stockMinimo": 15,
  "activo": true
}
```

#### Actualizar Stock de Producto
```http
PUT http://localhost:8080/api/productos/1/stock
Authorization: Bearer <token>
Content-Type: application/json

{
  "cantidad": 50
}
```

### 3. Gestión de Usuarios

#### Crear Usuario de Ventas
```http
POST http://localhost:8080/api/usuarios
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "carlos_ventas",
  "password": "ventas2024",
  "nombreCompleto": "Carlos Mendoza",
  "email": "carlos@lecoq.com",
  "rol": "VENTAS",
  "activo": true
}
```

#### Listar Usuarios Activos
```http
GET http://localhost:8080/api/usuarios/activos
Authorization: Bearer <token>
```

### 4. Gestión de Pedidos

#### Crear Pedido Completo
```http
POST http://localhost:8080/api/pedidos
Authorization: Bearer <token>
Content-Type: application/json

{
  "clienteNombre": "Distribuidora Norte S.A.C.",
  "clienteRuc": "20123456789",
  "clienteDireccion": "Av. Túpac Amaru 1250, Lima",
  "clienteTelefono": "014567890",
  "fechaEntregaEstimada": "2024-12-15T10:00:00",
  "observaciones": "Cliente preferencial - entrega prioritaria",
  "detalles": [
    {
      "producto": {"id": 1},
      "cantidad": 100
    },
    {
      "producto": {"id": 2},
      "cantidad": 50
    },
    {
      "producto": {"id": 3},
      "cantidad": 75
    }
  ]
}
```

#### Confirmar Pedido (Cambiar Estado)
```http
PUT http://localhost:8080/api/pedidos/1/estado
Authorization: Bearer <token>
Content-Type: application/json

{
  "estado": "CONFIRMADO"
}
```

#### Listar Pedidos por Estado
```http
GET http://localhost:8080/api/pedidos/estado/CONFIRMADO
Authorization: Bearer <token>
```

### 5. Gestión de Distribuciones

#### Crear Distribución para un Pedido
```http
POST http://localhost:8080/api/distribuciones?pedidoId=1
Authorization: Bearer <token>
Content-Type: application/json

{
  "choferNombre": "Miguel Rodriguez",
  "choferTelefono": "999123456",
  "vehiculoPlaca": "ABC-123",
  "vehiculoModelo": "Toyota Hiace 2020",
  "fechaSalida": "2024-12-10T08:00:00",
  "direccionEntrega": "Av. Túpac Amaru 1250, Lima",
  "observaciones": "Coordinar con Sr. Pérez en recepción"
}
```

#### Marcar Distribución como En Ruta
```http
PUT http://localhost:8080/api/distribuciones/1/en-ruta
Authorization: Bearer <token>
```

#### Marcar Distribución como Entregada
```http
PUT http://localhost:8080/api/distribuciones/1/entregar
Authorization: Bearer <token>
```

### 6. Gestión de Maquilados

#### Login como Usuario Maquila
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "maquila",
  "password": "maquila123"
}
```

#### Crear Orden de Maquilado
```http
POST http://localhost:8080/api/maquilados
Authorization: Bearer <token_maquila>
Content-Type: application/json

{
  "proveedorNombre": "Industrias Alimentarias del Sur S.A.C.",
  "proveedorRuc": "20987654321",
  "proveedorContacto": "María García - Gerente Producción",
  "proveedorTelefono": "014445555",
  "fechaEntregaEstimada": "2024-12-20T14:00:00",
  "observaciones": "Producción urgente para campaña navideña",
  "detalles": [
    {
      "producto": {"id": 1},
      "cantidadSolicitada": 2000,
      "costoUnitario": 1.50
    },
    {
      "producto": {"id": 2},
      "cantidadSolicitada": 1500,
      "costoUnitario": 1.65
    }
  ]
}
```

#### Cambiar Estado de Maquilado a En Proceso
```http
PUT http://localhost:8080/api/maquilados/1/en-proceso
Authorization: Bearer <token_maquila>
```

#### Finalizar Maquilado
```http
PUT http://localhost:8080/api/maquilados/1/finalizar
Authorization: Bearer <token_maquila>
```

#### Actualizar Cantidades Recibidas
```http
PUT http://localhost:8080/api/maquilados/1/cantidades-recibidas
Authorization: Bearer <token_maquila>
Content-Type: application/json

[
  {
    "id": 1,
    "cantidadRecibida": 1950
  },
  {
    "id": 2,
    "cantidadRecibida": 1500
  }
]
```

#### Recibir Maquilado (Actualiza Stock Automáticamente)
```http
PUT http://localhost:8080/api/maquilados/1/recibir
Authorization: Bearer <token_maquila>
```

### 7. Consultas Útiles

#### Productos con Stock Bajo
```http
GET http://localhost:8080/api/productos/stock-bajo
Authorization: Bearer <token>
```

#### Pedidos por Rango de Fechas
```http
GET http://localhost:8080/api/pedidos/fecha?fechaInicio=2024-12-01T00:00:00&fechaFin=2024-12-31T23:59:59
Authorization: Bearer <token>
```

#### Buscar Pedidos por Cliente
```http
GET http://localhost:8080/api/pedidos/cliente?nombre=Distribuidora
Authorization: Bearer <token>
```

#### Distribuciones Programadas
```http
GET http://localhost:8080/api/distribuciones/estado/PROGRAMADO
Authorization: Bearer <token>
```

## Flujo Completo de Trabajo

### Escenario: Proceso Completo de Venta

1. **Login como usuario de ventas**
2. **Consultar productos con stock disponible**
3. **Crear pedido con productos seleccionados**
4. **Confirmar pedido (cambia a CONFIRMADO y reduce stock)**
5. **Crear distribución para el pedido**
6. **Marcar distribución como en ruta**
7. **Marcar distribución como entregada**

### Escenario: Proceso de Reposición de Stock

1. **Login como usuario de maquila**
2. **Consultar productos con stock bajo**
3. **Crear orden de maquilado para reponer stock**
4. **Marcar maquilado como en proceso**
5. **Marcar maquilado como finalizado**
6. **Actualizar cantidades recibidas**
7. **Recibir maquilado (actualiza stock automáticamente)**

## Headers Comunes

Para todas las peticiones autenticadas:
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

## Variables de Entorno Sugeridas para Postman

- `baseUrl`: `http://localhost:8080`
- `adminToken`: Token del admin después del login
- `ventasToken`: Token del usuario ventas
- `maquilaToken`: Token del usuario maquila

## Códigos de Error Comunes

- `400`: Datos inválidos en la petición
- `401`: Token JWT inválido o expirado
- `403`: Sin permisos para la operación
- `404`: Recurso no encontrado
- `500`: Error interno del servidor

## Notas Importantes

1. **Los tokens JWT expiran en 24 horas** por defecto
2. **El stock se actualiza automáticamente** al confirmar pedidos y recibir maquilados
3. **Los usuarios de VENTAS solo ven sus propios pedidos**, los ADMIN ven todos
4. **Solo los ADMIN pueden eliminar registros**
5. **El sistema valida stock disponible** antes de confirmar pedidos
6. **Los estados siguen un flujo lógico** (no puedes saltar estados)
