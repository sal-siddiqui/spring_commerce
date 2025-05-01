package com.spring_commerce.exceptions;

public class APIException extends RuntimeException {
    // private static final long serialVersionUUID = 1L;

    public APIException() {

    }

    public APIException(String message) {
        super(message);
    }
}
