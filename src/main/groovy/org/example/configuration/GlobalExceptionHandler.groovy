package org.example.configuration

import org.example.exception.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException)
    ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest(ex.message ?: "Something gone wrong")
    }

    @ExceptionHandler(Exception)
    ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.internalServerError("An error occurred: ${ex.message}")
    }

    @ExceptionHandler(ResourceNotFoundException)
    ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.notFound(ex.message ?: "Not found")
    }
}
