package org.bjjon.backend.repository;

import org.bjjon.backend.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LeadRepo extends JpaRepository<Lead, UUID> {
    boolean existsByEmail(String email);
}
