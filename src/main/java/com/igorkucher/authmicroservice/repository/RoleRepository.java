package com.igorkucher.authmicroservice.repository;

import com.igorkucher.authmicroservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
