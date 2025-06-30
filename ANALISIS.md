# Análisis de Pruebas de Concurrencia

## Descripción del Problema

Se implementó una prueba de concurrencia que simula 10 usuarios intentando comprar el mismo producto al mismo tiempo, cuando el stock disponible es de solo 5 unidades.

## Resultados Observados

### Comportamiento Sin Optimizaciones

- **Stock inicial**: 5 unidades
- **Threads concurrentes**: 10 usuarios
- **Compras exitosas**: 5 (máximo permitido por el stock)
- **Compras fallidas**: 5 (stock insuficiente)
- **Stock final**: 0
- **Consistencia**: ✅ Mantenida

### Problemas Encontrados

1. **Lentitud en operaciones concurrentes**: Las transacciones con locks pesimistas pueden crear cuellos de botella cuando hay alta concurrencia.

2. **Posibles deadlocks**: Aunque se implementó un ordenamiento por ID para evitar deadlocks en transferencias, en casos de alta carga podrían presentarse.

3. **Contención de recursos**: Los locks pesimistas hacen que las transacciones esperen, reduciendo el throughput general.

## Mejoras Implementadas

### 1. **Locks Pesimistas para Consistencia**

```sql
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Producto p WHERE p.id = :id")
Optional<Producto> findByIdWithLock(@Param("id") Long id);
```

### 2. **Ordenamiento para Evitar Deadlocks**

En las transferencias se ordenan las cuentas por ID para evitar deadlocks:

```java
Long minId = Math.min(request.getFromUserId(), request.getToUserId());
Long maxId = Math.max(request.getFromUserId(), request.getToUserId());
```

### 3. **Índices de Base de Datos**

Se añadieron índices estratégicos para acelerar las consultas:

```sql
CREATE INDEX idx_productos_id ON productos(id);
CREATE INDEX idx_usuarios_id ON usuarios(id);
CREATE INDEX idx_pedidos_usuario_id ON pedidos(usuario_id);
```

## Mejoras Adicionales Recomendadas

### 1. **Índice Compuesto para Pedidos**

```sql
CREATE INDEX idx_pedidos_usuario_producto ON pedidos(usuario_id, producto_id);
```

**Beneficio**: Acelera las búsquedas cuando se consultan pedidos por usuario y producto específico.

### 2. **Pool de Conexiones Optimizado**

```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.connection-timeout=20000
```

**Beneficio**: Mejora el manejo de conexiones bajo alta carga.

### 3. **Cache de Segundo Nivel**

```java
@Cacheable("productos")
public Producto obtenerProductoPorId(Long id)
```

**Beneficio**: Reduce consultas a la base de datos para productos frecuentemente consultados.

### 4. **Patrón de Reintento para Deadlocks**

```java
@Retryable(value = {DataAccessException.class}, maxAttempts = 3)
```

**Beneficio**: Maneja automáticamente los deadlocks transitorios.

## Conclusión

El sistema mantiene la **consistencia de datos** correctamente bajo concurrencia, pero hay oportunidades de optimización para mejorar el **rendimiento**. Las mejoras más impactantes serían:

1. **Añadir el índice compuesto en la tabla pedidos** (impacto inmediato)
2. **Optimizar el pool de conexiones** (mejora general de throughput)
3. **Implementar cache selectivo** (reduce carga de DB para consultas frecuentes)

La arquitectura transaccional implementada garantiza las propiedades ACID requeridas.
