package com.cybercup.cybercup_backend.repository;




import com.cybercup.cybercup_backend.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    List<Policy> findByEnabled(boolean enabled);

    boolean existsByNameIgnoreCase(String name);
}
