package org.bjjon.backend.repository;

import org.bjjon.backend.entity.CallLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CallLogRepo extends JpaRepository<CallLog, UUID> {
    List<CallLog> findByLeadId(UUID leadId);
}
