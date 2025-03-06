package com.geonlee.spring.user.infra;

import com.geonlee.spring.user.domain.aggregate.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
