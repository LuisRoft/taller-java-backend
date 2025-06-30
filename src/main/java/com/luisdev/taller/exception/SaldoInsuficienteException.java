package com.luisdev.taller.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends RuntimeException {
    
    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
    
    public SaldoInsuficienteException(Long usuarioId, BigDecimal disponible, BigDecimal requerido) {
        super("Saldo insuficiente para el usuario ID " + usuarioId + 
              ". Disponible: " + disponible + ", Requerido: " + requerido);
    }
} 