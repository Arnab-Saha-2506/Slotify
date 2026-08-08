package com.proj.slotify.exception;

import com.proj.slotify.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SlotAlreadyBookedException.class)
    public ResponseEntity<ErrorResponseDTO> handleSlotAlreadyBooked(SlotAlreadyBookedException ex, HttpServletRequest request) {
        logger.warn("[GlobalExceptionHandler] SlotAlreadyBookedException: path={}, message={}", request.getRequestURI(), ex.getMessage());
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        logger.warn("[GlobalExceptionHandler] UserNotFoundException: path={}, message={}", request.getRequestURI(), ex.getMessage());
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AvailabilityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleAvailabilityNotFound(AvailabilityNotFoundException ex, HttpServletRequest request) {
        logger.warn("[GlobalExceptionHandler] AvailabilityNotFoundException: path={}, message={}", request.getRequestURI(), ex.getMessage());
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleBookingNotFound(BookingNotFoundException ex, HttpServletRequest request) {
        logger.warn("[GlobalExceptionHandler] BookingNotFoundException: path={}, message={}", request.getRequestURI(), ex.getMessage());
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        logger.warn("[GlobalExceptionHandler] UserAlreadyExistsException: path={}, message={}", request.getRequestURI(), ex.getMessage());
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCreds(InvalidCredsException ex, HttpServletRequest request) {
        logger.warn("[GlobalExceptionHandler] InvalidCredentialsException: path={}, message={}", request.getRequestURI(), ex.getMessage());
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AvailabilityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleAvailabilityAlreadyExists(AvailabilityAlreadyExistsException ex, HttpServletRequest request) {
        logger.warn("[GlobalExceptionHandler] AvailabilityAlreadyExistsException: path={}, message={}", request.getRequestURI(), ex.getMessage());
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        logger.warn("[GlobalExceptionHandler] BadRequestException: path={}, message={}", request.getRequestURI(), ex.getMessage());
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        logger.error("[GlobalExceptionHandler] UnauthorizedException: path={}, message={}", request.getRequestURI(), ex.getMessage());
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");

        logger.warn("[GlobalExceptionHandler] Validation failed: path={}, message={}", request.getRequestURI(), message);
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("[GlobalExceptionHandler] Unhandled exception: path={}, message={}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Something went wrong: " + ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleHandlerMethodValidation(HandlerMethodValidationException ex, HttpServletRequest request) {
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
