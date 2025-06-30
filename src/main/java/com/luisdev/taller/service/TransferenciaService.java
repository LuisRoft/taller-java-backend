package com.luisdev.taller.service;

import com.luisdev.taller.dto.TransferirRequest;
import com.luisdev.taller.entity.Cuenta;
import com.luisdev.taller.exception.SaldoInsuficienteException;
import com.luisdev.taller.exception.UsuarioNoEncontradoException;
import com.luisdev.taller.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferenciaService {
    
    @Autowired
    private CuentaRepository cuentaRepository;
    
    @Autowired
    private LogErrorService logErrorService;
    
    @Transactional
    public String transferir(TransferirRequest request) {
        try {
            // Validar que no sea la misma cuenta
            if (request.getFromUserId().equals(request.getToUserId())) {
                throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
            }
            
            // Obtener cuenta origen con lock pesimista (orden por ID para evitar deadlocks)
            Long minId = Math.min(request.getFromUserId(), request.getToUserId());
            Long maxId = Math.max(request.getFromUserId(), request.getToUserId());
            
            Cuenta cuentaOrigen, cuentaDestino;
            
            if (request.getFromUserId().equals(minId)) {
                cuentaOrigen = cuentaRepository.findByUsuarioIdWithLock(minId)
                        .orElseThrow(() -> new UsuarioNoEncontradoException("Cuenta de usuario " + minId + " no encontrada"));
                cuentaDestino = cuentaRepository.findByUsuarioIdWithLock(maxId)
                        .orElseThrow(() -> new UsuarioNoEncontradoException("Cuenta de usuario " + maxId + " no encontrada"));
            } else {
                cuentaDestino = cuentaRepository.findByUsuarioIdWithLock(minId)
                        .orElseThrow(() -> new UsuarioNoEncontradoException("Cuenta de usuario " + minId + " no encontrada"));
                cuentaOrigen = cuentaRepository.findByUsuarioIdWithLock(maxId)
                        .orElseThrow(() -> new UsuarioNoEncontradoException("Cuenta de usuario " + maxId + " no encontrada"));
            }
            
            // Verificar saldo suficiente
            if (cuentaOrigen.getSaldo().compareTo(request.getAmount()) < 0) {
                throw new SaldoInsuficienteException(request.getFromUserId(), 
                                                   cuentaOrigen.getSaldo(), 
                                                   request.getAmount());
            }
            
            // Realizar transferencia
            cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(request.getAmount()));
            cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(request.getAmount()));
            
            // Guardar cambios
            cuentaRepository.save(cuentaOrigen);
            cuentaRepository.save(cuentaDestino);
            
            return "Transferencia exitosa de " + request.getAmount() + 
                   " desde usuario " + request.getFromUserId() + 
                   " hacia usuario " + request.getToUserId();
                   
        } catch (Exception e) {
            logErrorService.logError("Error en transferencia: " + e.getMessage(), 
                                   "From: " + request.getFromUserId() + 
                                   ", To: " + request.getToUserId() + 
                                   ", Amount: " + request.getAmount());
            throw e;
        }
    }
} 