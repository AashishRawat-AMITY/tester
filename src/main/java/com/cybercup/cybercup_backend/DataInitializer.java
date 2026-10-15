package com.cybercup.cybercup_backend;

import com.cybercup.cybercup_backend.entity.Role;
import com.cybercup.cybercup_backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeRoles(RoleRepository roleRepository) {
        return args -> {
            createRoleIfMissing(roleRepository, "ADMIN");
            createRoleIfMissing(roleRepository, "SECURITY_ANALYST");
            createRoleIfMissing(roleRepository, "EMPLOYEE");
        };
    }

    private void createRoleIfMissing(
            RoleRepository roleRepository,
            String roleName) {

        if (roleRepository.findByName(roleName).isEmpty()) {
            roleRepository.save(new Role(roleName));
            System.out.println("Created role: " + roleName);
        }
    }
}
