package io.js.app.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ErrorResponse {
    private HttpStatus status;
    private String message;
    private LocalDateTime timestamp;
}