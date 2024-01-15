package io.js.sbexception.exception;

import io.js.sbexception.domain.CustomerNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@RestControllerAdvice
public class ExceptionHandling implements ProblemHandling {

    @ExceptionHandler
    public ResponseEntity<Problem> handleException(CustomerNotFoundException e, NativeWebRequest request) {
        ThrowableProblem problem = Problem.builder().withStatus(Status.NOT_FOUND)
                .withTitle("Customer not found")
                .withDetail(e.getMessage())
                .build();

        return create(problem, request);
    }
}
