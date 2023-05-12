package com.solvd.noteservice.domain.exception;

public class IllegalOperationException extends RuntimeException {

    public IllegalOperationException(final String message) {
        super(message);
    }

}
