package com.convera.product.exception;
public class ServiceExternalException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ServiceExternalException(String msg) {
        super(msg);
    }

    public ServiceExternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
