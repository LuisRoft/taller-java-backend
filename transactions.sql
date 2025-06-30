-- Registro de nuevo usuario
BEGIN;
INSERT INTO usuarios (nombre, email, saldo)
VALUES ('Duplicado', 'juan.perez@example.com', 0.00); -- Violación UNIQUE
COMMIT; -- Falla y hace rollback


-- Realizar una compra
DO $$
DECLARE
    stock_actual INT;
    saldo_usuario DECIMAL(10,2);
    precio_producto CONSTANT DECIMAL(10,2) := 45.00; -- Precio del producto
    cantidad_compra CONSTANT INT := 1; -- Cantidad a comprar
    producto_id_compra CONSTANT INT := 1; -- ID del producto
    usuario_id_compra CONSTANT INT := 1; -- ID del usuario
BEGIN
    -- Verificar stock del producto
    SELECT stock INTO stock_actual FROM productos WHERE id = producto_id_compra;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Producto ID % no encontrado', producto_id_compra;
    END IF;
    IF stock_actual < cantidad_compra THEN
        RAISE EXCEPTION 'Stock insuficiente para el producto ID %. Disponible: %, Requerido: %', producto_id_compra, stock_actual, cantidad_compra;
    END IF;

    -- Verificar saldo del usuario
    SELECT saldo INTO saldo_usuario FROM usuarios WHERE id = usuario_id_compra;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Usuario ID % no encontrado', usuario_id_compra;
    END IF;
    IF saldo_usuario < precio_producto * cantidad_compra THEN
        RAISE EXCEPTION 'Saldo insuficiente para el usuario ID %. Disponible: %, Requerido: %', usuario_id_compra, saldo_usuario, precio_producto * cantidad_compra;
    END IF;

    -- Actualizar stock
    UPDATE productos SET stock = stock - cantidad_compra WHERE id = producto_id_compra;
    -- Actualizar saldo del usuario
    UPDATE usuarios SET saldo = saldo - (precio_producto * cantidad_compra) WHERE id = usuario_id_compra;
    -- Insertar pedido
    INSERT INTO pedidos (usuario_id, total, estado)
    VALUES (usuario_id_compra, precio_producto * cantidad_compra, 'pagado');

    -- No hay COMMIT explícito aquí porque el bloque DO $$...END $$; en PostgreSQL
    -- se ejecuta como una transacción única. Si no hay excepciones, se hace COMMIT implícito.
    -- Si hay una EXCEPTION, se hace ROLLBACK implícito.
END $$;


-- Transferencia entre cuentas
DO $$
DECLARE
saldo_origen DECIMAL(10,2);
BEGIN
SELECT saldo INTO saldo_origen FROM cuentas WHERE usuario_id = 1;
IF saldo_origen < 50 THEN
RAISE EXCEPTION 'Fondos insuficientes';
END IF;
UPDATE cuentas SET saldo = saldo - 50 WHERE usuario_id = 1;
UPDATE cuentas SET saldo = saldo + 50 WHERE usuario_id = 2;
END $$;


-- Cancelación de pedido en proceso de pago.
DO $$
BEGIN
UPDATE pedidos SET estado = 'cancelado' WHERE id = 3;
DELETE FROM pagos WHERE pedido_id = 3;
RAISE EXCEPTION 'Error en el sistema'; -- Fuerza rollback
END $$;

-- Actualización masiva de precios (Aislamiento)
BEGIN;
UPDATE productos SET precio = precio * 1.10;
COMMIT;

-- Aplicación de descuento en lote (Durabilidad + Aislamiento)
BEGIN;
UPDATE productos SET precio = precio * 0.9 WHERE stock > 10;
COMMIT

-- Reporte de ventas diarias (Consistencia)
-- SET TRANSACTION ISOLATION LEVEL REPEATABLE READ; -- Ejemplo de cómo se establecería
SELECT SUM(total) FROM pedidos WHERE creado_en::date = CURRENT_DATE;

