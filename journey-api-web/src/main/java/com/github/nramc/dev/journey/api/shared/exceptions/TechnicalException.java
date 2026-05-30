package com.github.nramc.dev.journey.api.shared.exceptions;

public class TechnicalException extends RuntimeException {

    public TechnicalException(String message, Throwable ex) {
        super(message, ex);
    }
}
