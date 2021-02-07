package com.plee.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserExistedException extends RuntimeException{
    public UserExistedException(String message) {
        super(message);
    }
}
