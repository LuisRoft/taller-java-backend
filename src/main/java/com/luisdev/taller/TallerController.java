package com.luisdev.taller;

import com.luisdev.taller.dto.ApiResponse;
import com.luisdev.taller.dto.CrearPedidoRequest;
import com.luisdev.taller.dto.CrearUsuarioRequest;
import com.luisdev.taller.dto.TransferirRequest;
import com.luisdev.taller.entity.Pedido;
import com.luisdev.taller.entity.Producto;
import com.luisdev.taller.entity.Usuario;
import com.luisdev.taller.service.PedidoService;
import com.luisdev.taller.service.ProductoService;
import com.luisdev.taller.service.TransferenciaService;
import com.luisdev.taller.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TallerController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private TransferenciaService transferenciaService;
    
    @GetMapping("/")
    public ResponseEntity<ApiResponse<String>> helloWorld() {
        return ResponseEntity.ok(ApiResponse.success("API Transaccional funcionando correctamente"));
    }
    
    /**
     * POST /api/users
     * Registra un nuevo usuario
     */
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<Usuario>> crearUsuario(@Valid @RequestBody CrearUsuarioRequest request) {
        Usuario usuario = usuarioService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario creado exitosamente", usuario));
    }
    
    /**
     * GET /api/users
     * Obtiene todos los usuarios
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Usuario>>> obtenerUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(ApiResponse.success(usuarios));
    }
    
    /**
     * GET /api/users/{id}
     * Obtiene un usuario por ID
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Usuario>> obtenerUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }
    
    /**
     * GET /api/products
     * Obtiene todos los productos
     */
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<Producto>>> obtenerProductos() {
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        return ResponseEntity.ok(ApiResponse.success(productos));
    }
    
    /**
     * GET /api/products/{id}
     * Obtiene un producto por ID
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Producto>> obtenerProducto(@PathVariable Long id) {
        Producto producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(ApiResponse.success(producto));
    }
    
    /**
     * POST /api/orders
     * Procesa la compra de un producto
     */
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<Pedido>> crearPedido(@Valid @RequestBody CrearPedidoRequest request) {
        Pedido pedido = pedidoService.crearPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pedido creado exitosamente", pedido));
    }
    
    /**
     * GET /api/orders
     * Obtiene todos los pedidos
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<Pedido>>> obtenerPedidos() {
        List<Pedido> pedidos = pedidoService.obtenerTodosLosPedidos();
        return ResponseEntity.ok(ApiResponse.success(pedidos));
    }
    
    /**
     * GET /api/orders/user/{userId}
     * Obtiene pedidos de un usuario específico
     */
    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<ApiResponse<List<Pedido>>> obtenerPedidosPorUsuario(@PathVariable Long userId) {
        List<Pedido> pedidos = pedidoService.obtenerPedidosPorUsuario(userId);
        return ResponseEntity.ok(ApiResponse.success(pedidos));
    }
    
    /**
     * POST /api/transfers
     * Realiza una transferencia de saldo entre cuentas
     */
    @PostMapping("/transfers")
    public ResponseEntity<ApiResponse<String>> transferir(@Valid @RequestBody TransferirRequest request) {
        String resultado = transferenciaService.transferir(request);
        return ResponseEntity.ok(ApiResponse.success(resultado));
    }
}

// Controlador separado para manejar favicon y evitar confusión con la API
@RestController
class StaticResourceController {
    
    @GetMapping("/favicon.ico")
    @ResponseBody
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
