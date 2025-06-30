-- Script para crear la base de datos y las tablas necesarias

-- Crear la base de datos (ejecutar como superusuario)
-- CREATE DATABASE taller_db;

-- Conectarse a la base de datos taller_db y ejecutar lo siguiente:

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    saldo NUMERIC(10,2) NOT NULL DEFAULT 0.00
);

-- Tabla de productos  
CREATE TABLE IF NOT EXISTS productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    precio NUMERIC(10,2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0
);

-- Tabla de pedidos
CREATE TABLE IF NOT EXISTS pedidos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    total NUMERIC(10,2) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabla de cuentas
CREATE TABLE IF NOT EXISTS cuentas (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    saldo NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabla de log de errores (campo mensaje_error ampliado)
CREATE TABLE IF NOT EXISTS log_errores (
    id BIGSERIAL PRIMARY KEY,
    timestamp_error TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mensaje_error VARCHAR(1000) NOT NULL,
    detalles TEXT
);

-- Índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_pedidos_usuario_id ON pedidos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_pedidos_creado_en ON pedidos(creado_en);
CREATE INDEX IF NOT EXISTS idx_cuentas_usuario_id ON cuentas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_log_errores_timestamp ON log_errores(timestamp_error);

-- Constraints adicionales
ALTER TABLE usuarios ADD CONSTRAINT chk_saldo_positivo CHECK (saldo >= 0);
ALTER TABLE productos ADD CONSTRAINT chk_precio_positivo CHECK (precio > 0);
ALTER TABLE productos ADD CONSTRAINT chk_stock_no_negativo CHECK (stock >= 0);
ALTER TABLE cuentas ADD CONSTRAINT chk_cuenta_saldo_positivo CHECK (saldo >= 0);

COMMENT ON TABLE usuarios IS 'Tabla de usuarios del sistema';
COMMENT ON TABLE productos IS 'Tabla de productos disponibles para compra';
COMMENT ON TABLE pedidos IS 'Tabla de pedidos realizados por los usuarios';
COMMENT ON TABLE cuentas IS 'Tabla de cuentas bancarias de los usuarios para transferencias';
COMMENT ON TABLE log_errores IS 'Tabla para el registro de errores del sistema'; 