package com.tomi.jwtsecurity.exception;

import lombok.Data;

@Data
public class ExceptionResponse {
    private int status;
    private String message;
    private long timestamp;
}
