package com.example.userms.service.impl.security;

import com.example.userms.security.SecurityHelper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final SecurityHelper securityHelper;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        String authHeader = request.getHeader("Authorization");
        if (securityHelper.authHeaderIsValid(authHeader)) {
            String jwt = authHeader.substring(7);

            SecurityContextHolder.clearContext();
        }else {
            throw new RuntimeException("Authorization header is invalid");
        }
    }
}

