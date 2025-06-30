package com.luisdev.taller.exception;

public class ProductoNoEncontradoException extends RuntimeException {
    
    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
    public ProductoNoEncontradoException(Long id) {
        super("Producto con ID " + id + " no encontrado");
    }
} 