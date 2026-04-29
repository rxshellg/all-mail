package com.rxshellg.allmail.repository;

import com.rxshellg.allmail.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByGoogleId(String googleId);
    
    Optional<AppUser> findByEmail(String email);
}