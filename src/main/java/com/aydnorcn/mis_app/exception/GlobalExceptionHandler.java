package com.aydnorcn.mis_app.exception;

import com.aydnorcn.mis_app.dto.APIResponse;
import com.aydnorcn.mis_app.utils.MessageConstants;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<ErrorMessage>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validationErrorList = exception.getBindingResult().getAllErrors();

        validationErrorList.forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String validationMsg = error.getDefaultMessage();
            validationErrors.put(fieldName, validationMsg);
        });

        return createResponseEntity(validationErrors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse<ErrorMessage>> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return createResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<APIResponse<ErrorMessage>> handleAlreadyExistsException(AlreadyExistsException exception) {
        return createResponseEntity(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({NoAuthorityException.class, AuthorizationDeniedException.class})
    public ResponseEntity<APIResponse<ErrorMessage>> handleNoAuthorityException() {
        return createResponseEntity(HttpStatus.FORBIDDEN, MessageConstants.UNAUTHORIZED_ACTION);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse<ErrorMessage>> handleAPIException(APIException exception) {
        return createResponseEntity(exception.getStatus(), exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse<ErrorMessage>> handleMissingRequestBody() {
        return createResponseEntity(HttpStatus.BAD_REQUEST, MessageConstants.REQUEST_BODY_MISSING);
    }

    @ExceptionHandler(RateLimitExceedException.class)
    public ResponseEntity<APIResponse<ErrorMessage>> handleRateLimitExceedException(RateLimitExceedException exception) {
        return createResponseEntity(HttpStatus.TOO_MANY_REQUESTS, exception.getMessage());
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<APIResponse<ErrorMessage>> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException exception) {
        return createResponseEntity(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<APIResponse<ErrorMessage>> handleBadCredentialsException(BadCredentialsException exception) {
        return createResponseEntity(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<APIResponse<ErrorMessage>> handleAuthenticationException(AuthenticationException exception) {
        return createResponseEntity(HttpStatus.UNAUTHORIZED, MessageConstants.AUTHENTICATION_REQUIRED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<ErrorMessage>> handleException(Exception exception) {
        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private ResponseEntity<APIResponse<ErrorMessage>> createResponseEntity(HttpStatus status, String message) {
        ErrorMessage error = new ErrorMessage(new Date(), message);
        return new ResponseEntity<>(new APIResponse<>(false, message, error), status);
    }

    private ResponseEntity<APIResponse<ErrorMessage>> createResponseEntity(Map<String, String> message) {
        ErrorMessage error = new ErrorMessage(new Date(), message);
        return new ResponseEntity<>(new APIResponse<>(false, "Validation error occurred", error), HttpStatus.BAD_REQUEST);
    }
}