package io.js.app.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TokenNotExistException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<ProblemDetail> handleTokenNotExist(TokenNotExistException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Associated user not found or token is invalid");
        problemDetail.setType(URI.create("https://api.bookmarks.com/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ProblemDetail> handleUserNotFoundException(UserNotFoundException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Associated user not found or bad credentials");
        problemDetail.setType(URI.create("https://api.bookmarks.com/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(BadCredentialsException.class)
    private ResponseEntity<ProblemDetail> handleBadCredentialsException(BadCredentialsException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Username and password must be provided");
        problemDetail.setType(URI.create("https://api.bookmarks.com/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(errors);
    }

}
