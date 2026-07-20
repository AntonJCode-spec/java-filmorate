package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notFoundHandler(NotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.toString(), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> validationExceptionHandler(ValidationException e) {
        return new ResponseEntity<>(new ErrorResponse("VALIDATION_ERROR", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> variableRangeHandler(VariableRangeException e) {
        return new ResponseEntity<>(new ErrorResponse("VARIABLE_RANGE_ERROR", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        return new ResponseEntity<>(new ErrorResponse("INTERNAL_SERVER", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
