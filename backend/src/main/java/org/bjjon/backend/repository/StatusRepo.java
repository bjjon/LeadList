package org.bjjon.backend.repository;

import org.bjjon.backend.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepo extends JpaRepository<Status, String> {
    Status findStatusByValue(String value);
}
