package com.example.commonexception.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public enum ExceptionsEnum {

    PLANE_NOT_FOUND("Plane not found" , HttpStatus.NOT_FOUND),
    TICKET_NOT_FOUND("Ticket not found" , HttpStatus.NOT_FOUND),
    FLIGHT_NOT_FOUND("Flight not found" , HttpStatus.NOT_FOUND),
    AIRLINE_NOT_FOUND("Airline not found" , HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND("Role not found" , HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("User not found" , HttpStatus.NOT_FOUND),
    USERNAME_NOT_FOUND("Username not found" , HttpStatus.NOT_FOUND),
    Token_NOT_FOUND("Token not found" , HttpStatus.NOT_FOUND),
    SEAT_NUMBER_NOT_FOUND("There is no such seat number" , HttpStatus.BAD_REQUEST),
    SEAT_NUMBER_NOT_AVAILABLE("This seat already taken" , HttpStatus.BAD_REQUEST),
    AUTH_HEADER_NOT_TRUE("Authorization header not true!" , HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED_REQUEST("Unauthenticated request!" , HttpStatus.UNAUTHORIZED),
    PDF_CONTENT_IS_NULL("Pdf content is null!" , HttpStatus.BAD_REQUEST),
    EMAIL_NOT_REGISTERED("This email not registered!" , HttpStatus.BAD_REQUEST),
    TOKEN_IS_WRONG("Token is wrong!" , HttpStatus.BAD_REQUEST),
    USER_ID_IS_WRONG("UserId is wrong!" , HttpStatus.BAD_REQUEST),
    USERNAME_IS_NULL("Token Username is null!!" , HttpStatus.BAD_REQUEST),
    PASSWORDS_IS_NOT_EQUALS("Passwords not equals" , HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;
    private final LocalDateTime localDateTime;

    ExceptionsEnum(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.localDateTime = LocalDateTime.now();
    }

}