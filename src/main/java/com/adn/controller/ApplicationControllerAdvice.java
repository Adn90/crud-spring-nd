package com.adn.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.adn.exception.RecordNotFoundException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice // advises on exceptions in all classes with @RestController
public class ApplicationControllerAdvice {

    @ExceptionHandler(RecordNotFoundException.class) // what kind of exception is being handled
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(RecordNotFoundException ex) {
        return ex.getMessage();
        // not using stacktrace for security reason: not show the backend tech
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
            .map(error -> String.format("%s %s", error.getField(),error.getDefaultMessage()))
            .reduce("", (acc, error) -> acc + error + "\n");
        // not using stacktrace for security reason: not show the backend tech
    }

    // when pass negative id for put operation
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
            .map(error -> String.format("%s %s", error.getPropertyPath(), error.getMessage()))
            .reduce("", (acc, error) -> acc + error + "\n");
        // not using stacktrace for security reason: not show the backend tech
    }

    // when pass other types in id url
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        if (ex != null && ex.getRequiredType() != null) {
            // not expose backend language
            String type = ex.getRequiredType().getName(); // return java.lang.long
            String[] typeParts = type.split("\\.");
            String typeName = typeParts[typeParts.length - 1];
            return String.format("%s deve ser do tipo: %s", ex.getName(), typeName);
        }            
        return "Tipo do argumento não é válido!";        
    }
}

// https://youtu.be/Lr3d6QhZGlU?list=PLGxZ4Rq3BOBpwaVgAPxTxhdX_TfSVlTcY