package com.example.userms.controller;

import com.example.userms.model.dto.response.AuthenticationResponse;
import com.example.userms.service.ICheckTokenService;
import com.example.userms.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CheckTokenController {
    private final ICheckTokenService checkTokenService;
    private final IUserService userService;

    @PostMapping("/auth/checkAccessToken")
    public ResponseEntity<String> checkAccessToken(@RequestHeader(name = "Authorization") String accessToken){
        return checkTokenService.checkAccessToken(accessToken);
    }

    @GetMapping("/auth/checkRefreshToken")
    public ResponseEntity<AuthenticationResponse> checkRefreshToken(@RequestHeader(name = "Authorization") String refreshToken){
        return ResponseEntity.ok().body(userService.refreshToken(refreshToken));
    }

    @PostMapping("/auth/checkAdminToken")
    public ResponseEntity<String> checkAdminToken(@RequestHeader(name = "Authorization") String adminToken){
        return checkTokenService.checkAdminToken(adminToken);
    }

}
