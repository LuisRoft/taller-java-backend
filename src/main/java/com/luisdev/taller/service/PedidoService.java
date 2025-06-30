package com.luisdev.taller.service;

import com.luisdev.taller.dto.CrearPedidoRequest;
import com.luisdev.taller.entity.Pedido;
import com.luisdev.taller.entity.Producto;
import com.luisdev.taller.entity.Usuario;
import com.luisdev.taller.exception.ProductoNoEncontradoException;
import com.luisdev.taller.exception.SaldoInsuficienteException;
import com.luisdev.taller.exception.StockInsuficienteException;
import com.luisdev.taller.exception.UsuarioNoEncontradoException;
import com.luisdev.taller.repository.PedidoRepository;
import com.luisdev.taller.repository.ProductoRepository;
import com.luisdev.taller.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private LogErrorService logErrorService;
    
    @Transactional
    public Pedido crearPedido(CrearPedidoRequest request) {
        try {
            // Obtener producto con lock pesimista para control de concurrencia
            Producto producto = productoRepository.findByIdWithLock(request.getProductId())
                    .orElseThrow(() -> new ProductoNoEncontradoException(request.getProductId()));
            
            // Verificar stock disponible
            if (producto.getStock() < request.getQuantity()) {
                throw new StockInsuficienteException(request.getProductId(), 
                                                   producto.getStock(), 
                                                   request.getQuantity());
            }
            
            // Obtener usuario con lock pesimista
            Usuario usuario = usuarioRepository.findByIdWithLock(request.getUserId())
                    .orElseThrow(() -> new UsuarioNoEncontradoException(request.getUserId()));
            
            // Calcular total
            BigDecimal total = producto.getPrecio().multiply(BigDecimal.valueOf(request.getQuantity()));
            
            // Verificar saldo suficiente
            if (usuario.getSaldo().compareTo(total) < 0) {
                throw new SaldoInsuficienteException(request.getUserId(), 
                                                   usuario.getSaldo(), 
                                                   total);
            }
            
            // Actualizar stock del producto
            producto.setStock(producto.getStock() - request.getQuantity());
            productoRepository.save(producto);
            
            // Actualizar saldo del usuario
            usuario.setSaldo(usuario.getSaldo().subtract(total));
            usuarioRepository.save(usuario);
            
            // Crear pedido
            Pedido pedido = new Pedido(usuario, total, "pagado");
            return pedidoRepository.save(pedido);
            
        } catch (Exception e) {
            logErrorService.logError("Error al crear pedido: " + e.getMessage(), 
                                   "UserId: " + request.getUserId() + 
                                   ", ProductId: " + request.getProductId() + 
                                   ", Quantity: " + request.getQuantity());
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }
    
    @Transactional(readOnly = true)
    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAllWithUsuario();
    }
} 