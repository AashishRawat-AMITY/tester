package com.cybercup.cybercup_backend.repository;


import com.cybercup.cybercup_backend.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Incident> findAllByOrderByCreatedAtDesc();

    List<Incident> findByDecisionOrderByCreatedAtDesc(String decision);
}
