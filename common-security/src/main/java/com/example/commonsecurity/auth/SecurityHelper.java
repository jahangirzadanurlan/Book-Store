package com.example.commonsecurity.auth;

import lombok.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class SecurityHelper {
    public boolean servletPathIsAuth(@NonNull HttpServletRequest httpServletRequest) {
        return httpServletRequest.getServletPath().contains("/auth");
    }

    public boolean authHeaderIsValid(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }

    public boolean isJwtUsedFirst(String username) {
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }
}