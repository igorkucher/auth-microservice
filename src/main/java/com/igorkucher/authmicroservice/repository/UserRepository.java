package com.igorkucher.authmicroservice.repository;

import com.igorkucher.authmicroservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
