package com.example.bnb.booking.api;

import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> notFound(NoSuchElementException e) {
    return Map.of("error", "NOT_FOUND", "message", e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> badRequest(IllegalArgumentException e) {
    return Map.of("error", "BAD_REQUEST", "message", e.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> conflict(IllegalStateException e) {
    return Map.of("error", "CONFLICT", "message", e.getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> dataConflict(DataIntegrityViolationException e) {
    return Map.of("error", "CONFLICT", "message", "Request violates a uniqueness or integrity constraint");
  }
}
