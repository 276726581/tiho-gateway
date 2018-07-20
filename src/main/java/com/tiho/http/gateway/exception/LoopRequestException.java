package com.tiho.http.gateway.exception;

public class LoopRequestException extends RuntimeException {

    public LoopRequestException() {
    }

    public LoopRequestException(String message) {
        super(message);
    }

    public LoopRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoopRequestException(Throwable cause) {
        super(cause);
    }
}
