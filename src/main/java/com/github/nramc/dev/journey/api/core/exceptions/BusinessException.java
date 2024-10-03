package com.github.nramc.dev.journey.api.core.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@Getter
public class BusinessException extends RuntimeException {
    private final transient ProblemDetail problemDetail;

    public BusinessException(String message, String error) {
        super(message);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problem.setTitle(error);
        this.problemDetail = problem;
    }

}
