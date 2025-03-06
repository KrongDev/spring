package com.geonlee.spring.security.exception;

import com.geonlee.spring.shared.enums.ErrorCode;
import com.geonlee.spring.shared.exception.BaseException;
import lombok.Getter;

@Getter
public class CustomJwtException extends BaseException {

    public CustomJwtException(ErrorCode errorCode) {
        super(errorCode);
    }
}