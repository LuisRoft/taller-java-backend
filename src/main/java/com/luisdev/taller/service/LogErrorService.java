package com.luisdev.taller.service;

import com.luisdev.taller.entity.LogError;
import com.luisdev.taller.repository.LogErrorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogErrorService {
    
    @Autowired
    private LogErrorRepository logErrorRepository;
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logError(String mensaje, String detalles) {
        try {
            LogError logError = new LogError(mensaje, detalles);
            logErrorRepository.save(logError);
        } catch (Exception e) {
            // Si falla el logging, no queremos que afecte la transacción principal
            System.err.println("Error al guardar log: " + e.getMessage());
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logError(Exception exception) {
        String mensaje = exception.getMessage();
        String detalles = exception.getClass().getSimpleName() + ": " + 
                         (exception.getStackTrace().length > 0 ? 
                          exception.getStackTrace()[0].toString() : "Sin stack trace");
        logError(mensaje, detalles);
    }
} 