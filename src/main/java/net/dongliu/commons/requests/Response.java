package net.dongliu.commons.requests;

import net.dongliu.commons.lang.collection.Pair;

import java.util.Collections;
import java.util.List;

/**
 * http response, with statusCode, headers, and body
 *
 * @author Dong Liu
 */
public class Response<T> {
    private int statusCode;
    private List<Pair<String, String>> headers = Collections.emptyList();

    private T body;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<Pair<String, String>> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Pair<String, String>> headers) {
        this.headers = headers;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
