package com.convera.product.web;

import com.convera.common.template.CommonResponse;
import com.convera.common.template.response.error.ResponseError;
import com.convera.common.template.response.util.CommonResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler  //extends ResponseEntityExceptionHandler
{


    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse onConstraintValidationException(ConstraintViolationException exception,
                                                          ServletWebRequest webRequest) throws IOException {
        List<ResponseError> lst = new ArrayList<>();
        ResponseError error;


        for (ConstraintViolation violation : exception.getConstraintViolations()) {
            error = new ResponseError();
            error.setErrorMessage(violation.getPropertyPath().toString());
            error.setErrorDetails(violation.getMessage());
            lst.add(error);
        }
        return CommonResponseUtil.createResponse400(List.of(), webRequest.getRequest().getRequestURI() , webRequest.getRequest().getHeader("correlationId"), lst);
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public CommonResponse handleServiceCallException(MethodArgumentTypeMismatchException e, ServletWebRequest webRequest) {
        ResponseError error = new ResponseError();
        error.setErrorMessage(e.getMessage());
        error.setErrorDetails(e.getName());

        return
                CommonResponseUtil.createResponse400(List.of(), webRequest.getRequest().getRequestURI(),
                        webRequest.getRequest().getHeader("correlationId"),
                        List.of(error));
    }

   /* @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public CommonResponse handleServiceCallException(HttpMessageNotReadableException ex,
                                                     HttpHeaders headers,
                                                     HttpStatus status,
                                                     WebRequest request) {
        ResponseError error = new ResponseError();
        error.setErrorMessage(ex.getMessage());
        error.setErrorDetails(request.getDescription(false));

        return
                CommonResponseUtil.createResponse400(List.of(), request.getContextPath(),
                        request.getHeader("correlationId"),
                        List.of(error));
    }*/


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleConstraintViolationException(HttpMessageNotReadableException exception,
                                                   ServletWebRequest webRequest) throws IOException {
        List<ResponseError> lst = new ArrayList<>();
        ResponseError error = new ResponseError();
            error.setErrorMessage(exception.getLocalizedMessage());
            error.setErrorDetails(exception.getLocalizedMessage());
            lst.add(error);

        return CommonResponseUtil.createResponse400(List.of(), webRequest.getRequest().getRequestURI() , webRequest.getRequest().getHeader("correlationId"), lst);

    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleGlobalException(HttpMessageNotReadableException exception,
                                                   ServletWebRequest webRequest) throws IOException {
        webRequest.getResponse().sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

}
