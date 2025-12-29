package com.example.backend.domain.server.repository;

import com.example.backend.domain.server.entity.ServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends JpaRepository<ServerEntity, Long> {

}