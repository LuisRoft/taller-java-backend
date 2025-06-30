package com.luisdev.taller.repository;

import com.luisdev.taller.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    @Query("SELECT p FROM Pedido p JOIN FETCH p.usuario WHERE p.usuario.id = :usuarioId")
    List<Pedido> findByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT p FROM Pedido p JOIN FETCH p.usuario WHERE DATE(p.creadoEn) = :fecha")
    List<Pedido> findByFecha(@Param("fecha") LocalDate fecha);
    
    @Query("SELECT p FROM Pedido p JOIN FETCH p.usuario")
    List<Pedido> findAllWithUsuario();
} 