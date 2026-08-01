package com.adam.docvault.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.adam.docvault.common.dto.ErrorResponseDTO;
import com.adam.docvault.user.exception.EmailAlreadyExistsException;
import com.adam.docvault.user.exception.InvalidCredentialsException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailExists(
            EmailAlreadyExistsException exception,
            HttpServletRequest request
    ){

        ErrorResponseDTO error = new ErrorResponseDTO(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }


    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(
            InvalidCredentialsException exception,
            HttpServletRequest request
    ){

        ErrorResponseDTO error = new ErrorResponseDTO(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

}