package com.example.userms.service;

import org.springframework.http.ResponseEntity;

public interface ICheckTokenService {
    ResponseEntity<String> checkAccessToken(String header);

    ResponseEntity<String> checkAdminToken(String adminToken);
}
