package com.evelateresume.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Username doesn't exist.")
public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException() {
    }
}
