package com.example.userms.service;

import com.example.userms.model.dto.request.AuthenticationRequest;
import com.example.userms.model.dto.request.PasswordRequestDto;
import com.example.userms.model.dto.request.EmailRequestDto;
import com.example.userms.model.dto.request.UserRequestDto;
import com.example.userms.model.dto.response.AuthenticationResponse;
import com.example.userms.model.entity.User;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    ResponseEntity<String> saveUser(UserRequestDto userRequestDto);
    ResponseEntity<String> sendConfirmationLinkToUser(EmailRequestDto emailRequestDto);
    ResponseEntity<AuthenticationResponse> authenticateUser(AuthenticationRequest request);
    String passwordSetPage(String token);
    ResponseEntity<String> sendOTP(String token);
    ResponseEntity<String> checkOtp(String username,String otp);
    ResponseEntity<String> checkEmailInDatabase(String email);
    ResponseEntity<String> resetsPassword(String token, PasswordRequestDto passwordRequestDto);
    ResponseEntity<String> checkResetToken(String token);
    AuthenticationResponse refreshToken(String token);
    void sendConfirmationLink(String token, User user,String topicName);
    ResponseEntity<String> confirmAccount(String token);
}

