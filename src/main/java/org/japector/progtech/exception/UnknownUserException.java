package org.japector.progtech.exception;

public class UnknownUserException extends RuntimeException {
    public UnknownUserException(String message) {
        super(message);
    }
}