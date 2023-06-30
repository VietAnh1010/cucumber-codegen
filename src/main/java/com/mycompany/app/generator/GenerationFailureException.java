package com.mycompany.app.generator;

public class GenerationFailureException extends RuntimeException {

    public GenerationFailureException(String message) {
        super(message);
    }

    public GenerationFailureException(Throwable cause) {
        super(cause);
    }

    public GenerationFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
