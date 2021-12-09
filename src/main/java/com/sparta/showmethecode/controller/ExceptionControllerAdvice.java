package com.sparta.showmethecode.controller;

import com.sparta.showmethecode.dto.response.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ExceptionResponse illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("[ExceptionHandler-IllegalArgumentException: {}", e);
        return new ExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadCredentialsException.class)
    public ExceptionResponse badCredentialsExceptionHandler(BadCredentialsException e) {
        log.error("[ExceptionHandler-BadCredentialsException: {}", e);
        return new ExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
