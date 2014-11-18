package net.dongliu.requests.exception;

import java.net.URISyntaxException;

/**
 * @author Dong Liu dongliu@wandoujia.com
 */
public class InvalidUrlException extends RuntimeException {

    public static InvalidUrlException of(URISyntaxException e) {
        InvalidUrlException ex = new InvalidUrlException(e.getMessage(), e.getCause());
        ex.setStackTrace(e.getStackTrace());
        return ex;
    }

    public InvalidUrlException() {
    }

    public InvalidUrlException(String message) {
        super(message);
    }

    public InvalidUrlException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUrlException(Throwable cause) {
        super(cause);
    }

    public InvalidUrlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
