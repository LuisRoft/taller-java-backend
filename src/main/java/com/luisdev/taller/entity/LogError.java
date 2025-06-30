package com.luisdev.taller.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "log_errores")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LogError {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "timestamp_error", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestampError;
    
    @NotNull
    @Column(name = "mensaje_error", nullable = false, length = 1000)
    private String mensajeError;
    
    @Column(columnDefinition = "TEXT")
    private String detalles;
    
    // Constructores
    public LogError() {
        this.timestampError = LocalDateTime.now();
    }
    
    public LogError(String mensajeError, String detalles) {
        this.timestampError = LocalDateTime.now();
        // Truncar mensaje si es muy largo para evitar errores SQL
        this.mensajeError = mensajeError != null && mensajeError.length() > 1000 
            ? mensajeError.substring(0, 997) + "..." 
            : mensajeError;
        this.detalles = detalles;
    }
    
    @PrePersist
    protected void onCreate() {
        if (timestampError == null) {
            timestampError = LocalDateTime.now();
        }
        // Asegurar que el mensaje no sea muy largo
        if (mensajeError != null && mensajeError.length() > 1000) {
            mensajeError = mensajeError.substring(0, 997) + "...";
        }
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getTimestampError() {
        return timestampError;
    }
    
    public void setTimestampError(LocalDateTime timestampError) {
        this.timestampError = timestampError;
    }
    
    public String getMensajeError() {
        return mensajeError;
    }
    
    public void setMensajeError(String mensajeError) {
        // Truncar mensaje si es muy largo
        this.mensajeError = mensajeError != null && mensajeError.length() > 1000 
            ? mensajeError.substring(0, 997) + "..." 
            : mensajeError;
    }
    
    public String getDetalles() {
        return detalles;
    }
    
    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }
} 