package com.luisdev.taller.exception;

public class UsuarioNoEncontradoException extends RuntimeException {
    
    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
    public UsuarioNoEncontradoException(Long id) {
        super("Usuario con ID " + id + " no encontrado");
    }
} 