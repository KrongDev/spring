package com.geonlee.spring.security.exception;

import com.geonlee.spring.shared.enums.ErrorCode;
import com.geonlee.spring.shared.exception.BaseException;

public class AuthenticationException extends BaseException {

    public AuthenticationException() {
        super(ErrorCode.INVALID_AUTHENTICATION);
    }

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException(String message) {
        super(message, ErrorCode.INVALID_AUTHENTICATION);
    }

    public AuthenticationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}