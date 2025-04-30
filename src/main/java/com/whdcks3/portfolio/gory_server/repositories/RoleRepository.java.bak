package com.whdcks3.portfolio.gory_server.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whdcks3.portfolio.gory_server.data.models.Role;
import com.whdcks3.portfolio.gory_server.enums.ERole;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

}
