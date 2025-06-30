package com.luisdev.taller;

import com.luisdev.taller.dto.CrearPedidoRequest;
import com.luisdev.taller.dto.CrearUsuarioRequest;
import com.luisdev.taller.entity.Producto;
import com.luisdev.taller.entity.Usuario;
import com.luisdev.taller.repository.ProductoRepository;
import com.luisdev.taller.repository.UsuarioRepository;
import com.luisdev.taller.service.PedidoService;
import com.luisdev.taller.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@ActiveProfiles("test")
public class ConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(ConcurrencyTest.class);

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final int NUM_THREADS = 10;
    private static final int STOCK_INICIAL = 5;
    private static final BigDecimal PRECIO_PRODUCTO = new BigDecimal("50.00");
    private static final BigDecimal SALDO_USUARIO = new BigDecimal("1000.00");

    @BeforeEach
    @Transactional
    void setUp() {
        // Limpiar datos previos
        productoRepository.deleteAll();
        usuarioRepository.deleteAll();
        
        // Crear producto de prueba con stock limitado
        Producto producto = new Producto("Producto Concurrencia Test", PRECIO_PRODUCTO, STOCK_INICIAL);
        productoRepository.save(producto);
        logger.info("Producto creado con ID: {} y stock: {}", producto.getId(), producto.getStock());

        // Crear usuarios de prueba con saldo suficiente
        for (int i = 1; i <= NUM_THREADS; i++) {
            try {
                CrearUsuarioRequest request = new CrearUsuarioRequest("Usuario " + i, "usuario" + i + "@test.com");
                Usuario usuario = usuarioService.crearUsuario(request);
                
                // Asegurar que el usuario tenga saldo suficiente
                usuario.setSaldo(SALDO_USUARIO);
                usuarioRepository.save(usuario);
                
                logger.info("Usuario creado: ID {}, Email: {}, Saldo: {}", 
                           usuario.getId(), usuario.getEmail(), usuario.getSaldo());
                           
            } catch (Exception e) {
                logger.warn("Error creando usuario {}: {}", i, e.getMessage());
            }
        }
        
        // Verificar que tenemos los datos correctos
        long productCount = productoRepository.count();
        long userCount = usuarioRepository.count();
        logger.info("Setup completado: {} productos, {} usuarios", productCount, userCount);
    }

    @Test
    void testConcurrentPurchases() throws InterruptedException {
        // Usar un pool más pequeño para evitar saturar H2
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(NUM_THREADS);
        
        AtomicInteger successfulPurchases = new AtomicInteger(0);
        AtomicInteger failedPurchases = new AtomicInteger(0);
        
        List<Future<Boolean>> futures = new ArrayList<>();

        // Simular NUM_THREADS usuarios intentando comprar el mismo producto al mismo tiempo
        for (int i = 1; i <= NUM_THREADS; i++) {
            final Long userId = (long) i;
            final Long productId = 1L;
            
            Future<Boolean> future = executor.submit(() -> {
                try {
                    // Esperar señal de inicio para sincronizar todos los threads
                    startLatch.await();
                    
                    CrearPedidoRequest request = new CrearPedidoRequest(userId, productId, 1);
                    pedidoService.crearPedido(request);
                    
                    successfulPurchases.incrementAndGet();
                    logger.info("✅ Usuario {} compró exitosamente", userId);
                    return true;
                    
                } catch (Exception e) {
                    failedPurchases.incrementAndGet();
                    // Solo logear el tipo de error sin detalles para evitar spam
                    String errorType = e.getClass().getSimpleName();
                    logger.info("❌ Usuario {} falló: {}", userId, errorType);
                    return false;
                } finally {
                    finishLatch.countDown();
                }
            });
            
            futures.add(future);
        }

        // Dar inicio a todos los threads al mismo tiempo
        logger.info("Iniciando {} threads concurrentes...", NUM_THREADS);
        startLatch.countDown();
        
        // Esperar a que todos terminen (máximo 45 segundos)
        boolean finished = finishLatch.await(45, TimeUnit.SECONDS);
        if (!finished) {
            logger.error("Test timeout - algunos threads no terminaron a tiempo");
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Obtener resultados
        for (Future<Boolean> future : futures) {
            try {
                future.get(1, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error("Error obteniendo resultado de thread: {}", e.getMessage());
            }
        }

        // Verificar estado final
        Producto producto = productoRepository.findById(1L).orElse(null);
        int stockFinal = producto != null ? producto.getStock() : -1;

        logger.info("\n📊 RESULTADOS DE LA PRUEBA DE CONCURRENCIA:");
        logger.info("Stock inicial: {}", STOCK_INICIAL);
        logger.info("Threads lanzados: {}", NUM_THREADS);
        logger.info("Compras exitosas: {}", successfulPurchases.get());
        logger.info("Compras fallidas: {}", failedPurchases.get());
        logger.info("Stock final: {}", stockFinal);

        // Verificaciones
        assert successfulPurchases.get() <= STOCK_INICIAL : 
            String.format("No se pueden vender más productos que el stock disponible. Exitosas: %d, Stock: %d", 
                         successfulPurchases.get(), STOCK_INICIAL);
                         
        assert stockFinal >= 0 : 
            String.format("El stock no puede ser negativo. Stock final: %d", stockFinal);
            
        assert stockFinal == (STOCK_INICIAL - successfulPurchases.get()) : 
            String.format("El stock final debe ser consistente. Esperado: %d, Actual: %d", 
                         (STOCK_INICIAL - successfulPurchases.get()), stockFinal);
        
        logger.info("✅ Prueba de concurrencia exitosa - Consistencia mantenida");
        
        // Verificación adicional: debe haber al menos algunas compras exitosas
        assert successfulPurchases.get() > 0 : "Debería haber al menos algunas compras exitosas";
    }
} 