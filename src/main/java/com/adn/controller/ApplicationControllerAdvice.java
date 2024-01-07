package com.adn.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.adn.exception.RecordNotFoundException;

@RestControllerAdvice // advises on exceptions in all classes with @RestController
public class ApplicationControllerAdvice {

    @ExceptionHandler(RecordNotFoundException.class) // what kind of exception is being handled
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(RecordNotFoundException ex) {
        return ex.getMessage();
        // not using stacktrace for security reason: not show the backend tech
    }
}
