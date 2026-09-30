package com.an.pathtrip.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {

    private final int status;

    public AppException(
            String message,
            int status
    ) {
        super(message);
        this.status = status;
    }
}