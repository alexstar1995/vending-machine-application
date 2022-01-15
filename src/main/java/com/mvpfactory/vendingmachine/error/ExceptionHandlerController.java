package com.mvpfactory.vendingmachine.error;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.mvpfactory.vendingmachine.error.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleException(UserNotFoundException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("User Not found. Error id is {} ", errorId, ex);
        return new ApiError(errorId, ex.getMessage());
    }

    @ExceptionHandler({UnrecognizedPropertyException.class, })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleException(UnrecognizedPropertyException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Unrecognized property in JSON. Error id is {} ", errorId, ex);
        return new ApiError(errorId, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleException(DataIntegrityViolationException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Error while inserting/updating user {} ", errorId, ex);
        return new ApiError(errorId, ex.getMessage());
    }

    @ExceptionHandler(UserDetailsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleException(UserDetailsException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Wrong user details for username {} ", errorId, ex);
        return new ApiError(errorId, ex.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleException(RuntimeException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Malformed user details {}", errorId, ex);
        return new ApiError(errorId, ex.getMessage());
    }

    @ExceptionHandler({DepositException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleException(DepositException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("User tried to deposit wrong coin {}", errorId, ex);
        return new ApiError(errorId, ex.getMessage());
    }

    @ExceptionHandler({OperationNotAllowedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleException(OperationNotAllowedException ex) {
        UUID errorId = UUID.randomUUID();
        log.error("User does not have the correct role to perform this operation {}", errorId, ex);
        return new ApiError(errorId, ex.getMessage());
    }
}