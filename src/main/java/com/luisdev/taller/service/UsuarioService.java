package com.luisdev.taller.service;

import com.luisdev.taller.dto.CrearUsuarioRequest;
import com.luisdev.taller.entity.Usuario;
import com.luisdev.taller.exception.EmailYaExisteException;
import com.luisdev.taller.exception.UsuarioNoEncontradoException;
import com.luisdev.taller.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private LogErrorService logErrorService;
    
    @Transactional
    public Usuario crearUsuario(CrearUsuarioRequest request) {
        try {
            // Verificar si el email ya existe
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new EmailYaExisteException(request.getEmail());
            }
            
            Usuario usuario = new Usuario(request.getNombre(), request.getEmail());
            return usuarioRepository.save(usuario);
            
        } catch (Exception e) {
            logErrorService.logError("Error al crear usuario: " + e.getMessage(), 
                                   "Email: " + request.getEmail());
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario con email " + email + " no encontrado"));
    }
} 