package com.ballastlane.pokemon.presentation.exception;

public class PokeApiException extends RuntimeException {
    public PokeApiException(String message) {
        super(message);
    }

    public PokeApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
