package com.solvd.noteservice.domain.exception;

public class ResourceDoesNotExistException extends RuntimeException{

    public ResourceDoesNotExistException(String message) {
        super(message);
    }

}
