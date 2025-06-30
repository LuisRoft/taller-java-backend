package com.luisdev.taller.exception;

public class StockInsuficienteException extends RuntimeException {
    
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
    
    public StockInsuficienteException(Long productoId, Integer disponible, Integer requerido) {
        super("Stock insuficiente para el producto ID " + productoId + 
              ". Disponible: " + disponible + ", Requerido: " + requerido);
    }
} 