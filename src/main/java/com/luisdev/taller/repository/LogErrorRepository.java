package com.luisdev.taller.repository;

import com.luisdev.taller.entity.LogError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface LogErrorRepository extends JpaRepository<LogError, Long> {
} 