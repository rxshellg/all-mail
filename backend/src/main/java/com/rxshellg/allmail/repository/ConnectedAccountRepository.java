package com.rxshellg.allmail.repository;

import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.model.ConnectedAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConnectedAccountRepository extends JpaRepository<ConnectedAccount, Long> {
    List<ConnectedAccount> findByAppUserAndActiveTrue(AppUser appUser);

    Optional<ConnectedAccount> findByAppUserAndProviderAndProviderAccountId(
            AppUser appUser,
            String provider,
            String providerAccountId
    );

    boolean existsByAppUserAndEmailAddress(AppUser appUser, String emailAddress);
}