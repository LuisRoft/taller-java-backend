-- Script para insertar datos de prueba

-- Insertar usuarios de prueba
INSERT INTO usuarios (nombre, email, saldo) VALUES
('Juan Pérez', 'juan.perez@example.com', 1000.00),
('María García', 'maria.garcia@example.com', 750.50),
('Carlos Rodríguez', 'carlos.rodriguez@example.com', 500.00),
('Ana López', 'ana.lopez@example.com', 1200.75),
('Luis Martínez', 'luis.martinez@example.com', 300.25);

-- Insertar productos de prueba
INSERT INTO productos (nombre, precio, stock) VALUES
('Laptop HP', 899.99, 15),
('Mouse Inalámbrico', 45.00, 50),
('Teclado Mecánico', 120.00, 25),
('Monitor 24"', 299.99, 12),
('Auriculares Bluetooth', 85.50, 30),
('Webcam HD', 65.00, 20),
('Disco Duro Externo 1TB', 75.99, 40),
('Memoria USB 32GB', 15.00, 100),
('Cable HDMI', 12.50, 75),
('Hub USB', 25.00, 35);

-- Insertar cuentas bancarias para los usuarios
INSERT INTO cuentas (usuario_id, saldo) VALUES
(1, 2000.00),
(2, 1500.00),
(3, 800.00),
(4, 2500.00),
(5, 600.00);

-- Insertar algunos pedidos de ejemplo
INSERT INTO pedidos (usuario_id, total, estado, creado_en) VALUES
(1, 45.00, 'pagado', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(2, 120.00, 'pagado', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(3, 299.99, 'pagado', CURRENT_TIMESTAMP - INTERVAL '3 hours'),
(1, 85.50, 'pagado', CURRENT_TIMESTAMP - INTERVAL '1 hour');

-- Verificar que los datos se insertaron correctamente
SELECT 'Usuarios insertados: ' || COUNT(*) FROM usuarios;
SELECT 'Productos insertados: ' || COUNT(*) FROM productos;
SELECT 'Cuentas insertadas: ' || COUNT(*) FROM cuentas;
SELECT 'Pedidos insertados: ' || COUNT(*) FROM pedidos; 