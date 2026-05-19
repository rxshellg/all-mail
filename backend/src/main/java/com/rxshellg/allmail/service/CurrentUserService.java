package com.rxshellg.allmail.service;

import com.rxshellg.allmail.config.SessionKeys;
import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.repository.AppUserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final AppUserRepository appUserRepository;

    public Long requireCurrentUserId(HttpSession session) {
        Long appUserId = (Long) session.getAttribute(SessionKeys.ALLMAIL_USER_ID);
        if (appUserId == null) throw new RuntimeException("No AllMail user is stored in the current session.");
        return appUserId;
    }

    @Transactional(readOnly = true)
    public AppUser requireCurrentUser(HttpSession session) {
        return appUserRepository.findById(requireCurrentUserId(session))
                .orElseThrow(() -> new RuntimeException("Logged-in user was not found."));
    }
}
