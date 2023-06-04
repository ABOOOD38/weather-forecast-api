package com.skyapi.weatherforecast.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
@Slf4j
@PropertySource("classpath:messages.properties")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${exc.GenericExceptionMsg}")
    private String GENERIC_EXCEPTION_MSG;

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {

        log.error(ex.getMessage(), ex);

        var apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList(),
                ((ServletWebRequest) request).getRequest().getServletPath()
        );

        return new ResponseEntity<>(apiError, headers, status);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ApiError> handleConstraintViolation(
            @NonNull ConstraintViolationException ex,
            @NonNull HttpServletRequest request
    ) {

        log.error(ex.getMessage(), ex);
        var apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getConstraintViolations()
                        .stream()
                        .map(ConstraintViolation::getMessage)
                        .toList(),
                request.getServletPath()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {

        log.error(ex.getMessage(), ex);

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                List.of(ex.getMessage() == null ? "" : ex.getMessage()),
                request.getServletPath()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleDuplicateResource(
            DuplicateResourceException ex,
            HttpServletRequest request
    ) {

        log.error(ex.getMessage(), ex);

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                List.of(ex.getMessage() == null ? "" : ex.getMessage()),
                request.getServletPath()
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoDataAvailableException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleNoDataAvailable(
            NoDataAvailableException ex,
            HttpServletRequest request
    ) {

        log.error(ex.getMessage(), ex);

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NO_CONTENT.value(),
                HttpStatus.NO_CONTENT.getReasonPhrase(),
                List.of(ex.getMessage() == null ? "" : ex.getMessage()),
                request.getServletPath()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(InvalidResourceProvidedException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleInvalidResourceProvided(
            InvalidResourceProvidedException ex,
            HttpServletRequest request
    ) {

        log.error(ex.getMessage(), ex);

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                List.of(ex.getMessage() == null ? "" : ex.getMessage()),
                request.getServletPath()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(XCurrentHourHeaderException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleXCurrentHourHeader(
            XCurrentHourHeaderException ex,
            HttpServletRequest request
    ) {

        log.error(ex.getMessage(), ex);

        final ApiError apiError = getAPIErrorForBadRequest(ex, request);

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GeolocationException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleGeolocationException(
            GeolocationException ex,
            HttpServletRequest request
    ) {

        log.error(ex.getMessage(), ex);

        final ApiError apiError = getAPIErrorForBadRequest(ex, request);

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error(ex.getMessage(), ex);

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                List.of(GENERIC_EXCEPTION_MSG),
                request.getServletPath()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ApiError getAPIErrorForBadRequest(RuntimeException ex, HttpServletRequest request) {
        return new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                List.of(ex.getMessage() == null ? "" : ex.getMessage()),
                request.getServletPath()
        );
    }
}
