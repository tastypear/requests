package net.dongliu.requests;

import net.dongliu.requests.struct.Headers;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;

/**
 * interface to handle response data. for user to custom response handler.
 *
 * @author Dong Liu
 */
public interface ResponseHandler<T> {

    /**
     * handle response body
     *
     * @param statusCode the response status code
     * @param headers    the response header
     * @param in         the response body inputStream
     * @return you want to return wrapped in Response
     * @throws IOException
     */
    T handle(int statusCode, Headers headers, InputStream in) throws IOException;
}
