package com.mvpfactory.vendingmachine.error;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.mvpfactory.vendingmachine.error.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.UUID;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleException(UserNotFoundException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("User Not found. Error id is {} ", errorId, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(errorId, ex.getMessage()));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleException(ProductNotFoundException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Product Not found. Error id is {} ", errorId, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(errorId, ex.getMessage()));
    }

    @ExceptionHandler({UnrecognizedPropertyException.class, })
    public ResponseEntity<ApiError> handleException(UnrecognizedPropertyException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Unrecognized property in JSON. Error id is {} ", errorId, ex);
        return ResponseEntity.badRequest().body(new ApiError(errorId, ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleException(DataIntegrityViolationException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Error while inserting/updating entity {} ", errorId, ex);
        return ResponseEntity.badRequest().body(new ApiError(errorId, ex.getMessage()));
    }

    @ExceptionHandler(UserDetailsException.class)
    public ResponseEntity<ApiError> handleException(UserDetailsException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Wrong user details for username {} ", errorId, ex);
        return ResponseEntity.badRequest().body(new ApiError(errorId, ex.getMessage()));
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError> handleException(RuntimeException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Malformed user details {}", errorId, ex);
        return ResponseEntity.badRequest().body(new ApiError(errorId, ex.getMessage()));
    }

    @ExceptionHandler({DepositException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleException(DepositException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("User tried to deposit wrong coin {}", errorId, ex);
        return ResponseEntity.badRequest().body(new ApiError(errorId, ex.getMessage()));
    }

    @ExceptionHandler({UserAlreadyExistsException.class})
    public ResponseEntity<ApiError> handleException(UserAlreadyExistsException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Cannot create user because it already exists {}", errorId, ex);
        return ResponseEntity.badRequest().body(new ApiError(errorId, ex.getMessage()));
    }

    @ExceptionHandler({ProductAlreadyExistsException.class})
    public ResponseEntity<ApiError> handleException(ProductAlreadyExistsException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Cannot create product because it already exists {}", errorId, ex);
        return ResponseEntity.badRequest().body(new ApiError(errorId, ex.getMessage()));
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<ApiError> handleException(UsernameNotFoundException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("User Not found. Error id is {} ", errorId, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(errorId, ex.getMessage()));
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ApiError> handleGenericException(RuntimeException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Internal server error {}", errorId, ex);
        return ResponseEntity.internalServerError().body(new ApiError(errorId, ex.getMessage()));
    }

    @ExceptionHandler({InvalidProductCostException.class})
    public ResponseEntity<ApiError> handleGenericException(InvalidProductCostException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Invalid product cost {}", errorId, ex);
        return ResponseEntity.badRequest().body(new ApiError(errorId, ex.getMessage()));
    }

    @ExceptionHandler({OperationNotAllowedException.class})
    public ResponseEntity<ApiError> handleGenericException(OperationNotAllowedException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Forbidden action on product {}", errorId, ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(errorId, ex.getMessage()));
    }
}