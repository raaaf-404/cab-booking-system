package com.cabbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CabAlreadyExistException extends RuntimeException {

    public CabAlreadyExistException(String message) {
        super(message);
    }
}
