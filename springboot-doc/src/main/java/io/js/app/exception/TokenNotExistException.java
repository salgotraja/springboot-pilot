package io.js.app.exception;

public class TokenNotExistException extends RuntimeException{

    public TokenNotExistException(String message) {
        super(message);
        System.out.println("HERE ONLY HERE");
    }
}
