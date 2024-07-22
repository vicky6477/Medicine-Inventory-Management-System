package com.panda.medicineinventorymanagementsystem.aop;

import com.panda.medicineinventorymanagementsystem.exception.MedicineAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //still have the problem to get all validation error at once, need to modified, can get null error together, but not with email exits
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        log.error("Validation error at {}: {}", webRequest.getContextPath(), errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex, WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Validation error at {}: {}", webRequest.getContextPath(), errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        addCommonErrorDetails(errors, webRequest);
        errors.put("method", ex.getMethod());
        errors.put("exception", ex.getMessage());
        log.error("Unsupported HTTP method at {}: {}", webRequest.getContextPath(), ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        addCommonErrorDetails(errors, webRequest);
        errors.put("error", ex.getMessage());
        log.error("Entity not found: {}", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex, WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        addCommonErrorDetails(errors, webRequest);
        errors.put("error", ex.getMessage());
        log.error("Illegal state: {}", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        addCommonErrorDetails(errors, webRequest);
        errors.put("error", ex.getMessage());
        log.error("Illegal argument: {}", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MedicineAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleMedicineAlreadyExistsException(MedicineAlreadyExistsException ex, WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        addCommonErrorDetails(errors, webRequest);
        errors.put("error", ex.getMessage());
        log.error("Medicine already exists: {}", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }

    private void addCommonErrorDetails(Map<String, String> errors, WebRequest webRequest) {
        errors.put("path", webRequest.getContextPath());
        errors.put("web request desc", webRequest.getDescription(false));
    }
}
