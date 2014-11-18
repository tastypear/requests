package net.dongliu.requests.exception;

import java.io.IOException;

/**
 * the runtime version (unckecked) of IOException
 *
 * @author Dong Liu dongliu@wandoujia.com
 */
public class RuntimeIOException extends RuntimeException {

    public static RuntimeIOException of(IOException e) {
        RuntimeIOException runtimeIOException = new RuntimeIOException(e.getMessage(),
                e.getCause());
        runtimeIOException.setStackTrace(e.getStackTrace());
        return runtimeIOException;
    }

    public RuntimeIOException() {
    }

    public RuntimeIOException(String message) {
        super(message);
    }

    public RuntimeIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeIOException(Throwable cause) {
        super(cause);
    }

    public RuntimeIOException(String message, Throwable cause, boolean enableSuppression,
                              boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
