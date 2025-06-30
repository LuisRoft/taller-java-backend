package com.luisdev.taller.exception;

public class EmailYaExisteException extends RuntimeException {
 
    public EmailYaExisteException(String email) {
        super("El email " + email + " ya está registrado");
    }
} 