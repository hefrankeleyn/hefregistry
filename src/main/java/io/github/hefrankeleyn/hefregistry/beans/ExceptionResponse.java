package io.github.hefrankeleyn.hefregistry.beans;

import org.springframework.http.HttpStatus;

/**
 * @Date 2024/5/21
 * @Author lifei
 */
public class ExceptionResponse {

    private HttpStatus httpStatus;
    private String message;

    public ExceptionResponse() {
    }

    public ExceptionResponse(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
