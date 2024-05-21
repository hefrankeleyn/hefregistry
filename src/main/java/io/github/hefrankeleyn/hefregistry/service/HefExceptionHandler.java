package io.github.hefrankeleyn.hefregistry.service;

import io.github.hefrankeleyn.hefregistry.beans.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Date 2024/5/21
 * @Author lifei
 */
@RestControllerAdvice
public class HefExceptionHandler {

    @ExceptionHandler(value = {IllegalStateException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleException(Exception exception) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
