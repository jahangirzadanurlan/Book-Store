package com.example.userms.service.impl.security;

import com.example.userms.model.dto.response.AuthenticationResponse;
import com.example.userms.security.SecurityHelper;
import com.example.userms.service.ICheckTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckTokenService implements ICheckTokenService {
    private final SecurityHelper securityHelper;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<String> checkAccessToken(String header){
        String token = header.substring(7);

        if (securityHelper.authHeaderIsValid(header) && !jwtService.isTokenExpired(token)){
            return ResponseEntity.ok("Token is true!");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Token is not true!");
        }
    }

    @Override
    public ResponseEntity<String> checkAdminToken(String header) {
        String token = header.substring(7);

        if (securityHelper.authHeaderIsValid(header) && !jwtService.isTokenExpired(token)){
            List<String> roles = jwtService.extractRoles(token);
            System.out.println(roles);
            if (roles.contains("ROLE_ADMIN")){
                return ResponseEntity.ok("Token is true!");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Token is not true!");
        }
    }
}
