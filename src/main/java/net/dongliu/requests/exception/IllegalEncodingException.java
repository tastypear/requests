package net.dongliu.requests.exception;

import java.io.UnsupportedEncodingException;

/**
 * @author Dong Liu dongliu@wandoujia.com
 */
public class IllegalEncodingException extends RuntimeException {

    public static IllegalEncodingException of(UnsupportedEncodingException e) {
        IllegalEncodingException runtimeIOException = new IllegalEncodingException(e.getMessage(),
                e.getCause());
        runtimeIOException.setStackTrace(e.getStackTrace());
        return runtimeIOException;
    }

    public IllegalEncodingException() {
    }

    public IllegalEncodingException(String message) {
        super(message);
    }

    public IllegalEncodingException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalEncodingException(Throwable cause) {
        super(cause);
    }

    public IllegalEncodingException(String message, Throwable cause, boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
