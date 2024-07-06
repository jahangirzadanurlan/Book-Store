package com.example.userms.handler;

import com.example.commonexception.enums.ExceptionsEnum;
import com.example.commonexception.exceptions.GeneralException;
import com.example.commonexception.response.ExceptionResponse;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.constraints.NotNull;

@RestControllerAdvice
public class GlobalHandler extends DefaultErrorAttributes {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ExceptionResponse> handleException(GeneralException generalPlaneException) {
        ExceptionsEnum exceptions = generalPlaneException.getExceptionsEnum();
        ExceptionResponse response = ExceptionResponse.of(exceptions.getMessage(), exceptions.getHttpStatus());

        return new ResponseEntity<>(response,response.getHttpStatus());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handlerValidationException(@NotNull BindException exception) {
        FieldError fieldError = exception.getFieldError();

        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error occurred!";

        return ExceptionResponse.of(errorMessage,HttpStatus.BAD_REQUEST);
    }
}
