package com.skyapi.weatherforecast.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GeolocationException extends RuntimeException {
    public GeolocationException(String message) {
        super(message);
    }

    public GeolocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
