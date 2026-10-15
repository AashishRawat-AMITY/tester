package com.cybercup.cybercup_backend.service;



import com.cybercup.cybercup_backend.entity.Incident;
import com.cybercup.cybercup_backend.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Transactional
    public Incident createIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    @Transactional(readOnly = true)
    public List<Incident> getAllIncidents() {
        return incidentRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Incident> getIncidentsByUser(Long userId) {
        return incidentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Incident> getIncidentsByDecision(String decision) {
        return incidentRepository
                .findByDecisionOrderByCreatedAtDesc(decision.toUpperCase());
    }
}
