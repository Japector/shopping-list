package org.japector.progtech.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}

