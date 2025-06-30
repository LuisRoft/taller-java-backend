# Taller Transaccional - Spring Boot

Sistema de gestión transaccional que demuestra las propiedades ACID en operaciones de compra, transferencias y manejo de usuarios.

## 🚀 Características

- **API RESTful** con endpoints para usuarios, pedidos y transferencias
- **Manejo transaccional robusto** con @Transactional y rollbacks automáticos
- **Control de concurrencia** con locks pesimistas
- **Logging de errores** automático en base de datos
- **Validaciones** de entrada con mensajes personalizados
- **Manejo centralizado de excepciones**

## 📋 Requisitos Previos

- Java 17 o superior
- Docker y Docker Compose
- Maven 3.6+
- Git

## 🛠️ Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone <https://github.com/LuisRoft/taller-java-backend>
cd taller-java-backend
```

### 2. Levantar la base de datos con Docker

```bash
docker-compose up -d
```

Esto creará:

- Contenedor PostgreSQL en puerto 5432
- Base de datos `taller_db` con las tablas necesarias
- Datos de prueba precargados

### 3. Verificar que PostgreSQL esté funcionando

```bash
docker logs taller_postgres
```

### 4. Ejecutar la aplicación Java

```bash
./mvnw spring-boot:run
```

O en Windows:

```bash
mvnw.cmd spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 📡 API Endpoints

### Base URL: `http://localhost:8080/api`

### 👥 Usuarios

#### Crear Usuario

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "email": "juan.perez@example.com"
  }'
```

#### Obtener Todos los Usuarios

```bash
curl http://localhost:8080/api/users
```

#### Obtener Usuario por ID

```bash
curl http://localhost:8080/api/users/1
```

### 🛒 Pedidos

#### Crear Pedido (Compra)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 2,
    "quantity": 1
  }'
```

#### Obtener Todos los Pedidos

```bash
curl http://localhost:8080/api/orders
```

#### Obtener Pedidos por Usuario

```bash
curl http://localhost:8080/api/orders/user/1
```

### 💰 Transferencias

#### Realizar Transferencia

```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserId": 1,
    "toUserId": 2,
    "amount": 50.00
  }'
```

## 🧪 Pruebas

### Ejecutar todas las pruebas

```bash
./mvnw test
```

### Ejecutar solo las pruebas de concurrencia

```bash
./mvnw test -Dtest=ConcurrencyTest
```

## 🔍 Verificación de Transacciones ACID

### 1. Atomicidad (Rollback automático)

Intenta crear un pedido con saldo insuficiente:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 5,
    "productId": 1,
    "quantity": 100
  }'
```

Verifica que ni el stock ni el saldo hayan cambiado.

### 2. Consistencia

Verifica que las constraintes de la base de datos se respeten:

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Duplicado",
    "email": "juan.perez@example.com"
  }'
```

### 3. Aislamiento

Ejecuta las pruebas de concurrencia para verificar que múltiples transacciones no interfieran entre sí.

### 4. Durabilidad

Reinicia la aplicación y verifica que los datos persistan.

## 📊 Monitoreo y Logs

### Ver logs de la aplicación

```bash
tail -f logs/application.log
```

### Ver logs de errores en la base de datos

```sql
SELECT * FROM log_errores ORDER BY timestamp_error DESC;
```

### Conectarse a PostgreSQL directamente

```bash
docker exec -it taller_postgres psql -U postgres -d taller_db
```

## 🐛 Simulación de Errores

### Error de Stock Insuficiente

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 2,
    "quantity": 999
  }'
```

### Error de Saldo Insuficiente

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 5,
    "productId": 1,
    "quantity": 1
  }'
```

### Error de Email Duplicado

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Nuevo Usuario",
    "email": "juan.perez@example.com"
  }'
```

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/luisdev/taller/
│   │   ├── dto/                 # Objetos de transferencia de datos
│   │   ├── entity/             # Entidades JPA
│   │   ├── exception/          # Excepciones personalizadas
│   │   ├── repository/         # Repositorios JPA
│   │   ├── service/           # Lógica de negocio
│   │   └── TallerController.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/luisdev/taller/
        └── ConcurrencyTest.java
```

## 🔧 Configuración

### Variables de Entorno (Opcional)

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=taller_db
export DB_USER=postgres
export DB_PASSWORD=password
```

### Profiles de Spring

- `default`: Configuración para desarrollo con PostgreSQL
- `test`: Configuración para pruebas con H2 en memoria

## 🚨 Solución de Problemas

### PostgreSQL no se conecta

```bash
# Verificar que el contenedor esté corriendo
docker ps

# Revisar logs del contenedor
docker logs taller_postgres

# Reiniciar el contenedor
docker-compose restart postgres
```

### Error de dependencias

```bash
# Limpiar y recompilar
./mvnw clean install
```

### Puerto ocupado

Si el puerto 8080 está ocupado, cambia en `application.properties`:

```properties
server.port=8081
```

## 📈 Métricas de Rendimiento

El sistema incluye logging detallado de transacciones. Para análisis de rendimiento, consulta:

- Tiempo de ejecución de queries en logs
- Número de rollbacks en `log_errores`
- Estadísticas de pool de conexiones

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es con fines educativos para demostrar manejo transaccional en Spring Boot.

---

**¡El sistema está listo para demostrar las propiedades ACID en acción!** 🎉
