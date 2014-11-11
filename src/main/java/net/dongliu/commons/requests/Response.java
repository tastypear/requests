package net.dongliu.commons.requests;

import net.dongliu.commons.lang.collection.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * http response, with statusCode, headers, and data
 *
 * @author Dong Liu
 */
public class Response<T> {
    private int statusCode;
    private List<Pair<String, String>> headers = Collections.emptyList();

    private T body;

    private List<Response<byte[]>> historyResponses;

    public int statusCode() {
        return statusCode;
    }

    public void statusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<Pair<String, String>> headers() {
        return headers;
    }

    public void headers(List<Pair<String, String>> headers) {
        this.headers = headers;
    }

    /**
     * get first match header value by header name
     */
    public String header(String name) {
        for (Pair<String, String> header : headers) {
            if (header.getKey().equals(name)) {
                return header.getValue();
            }
        }
        return null;
    }

    public T body() {
        return body;
    }

    public void body(T body) {
        this.body = body;
    }

    /**
     * get cookies
     * TODO: to be implemented
     */
    public List<Pair<String, String>> cookies() {
        throw new UnsupportedOperationException();
    }

    /**
     * get cookie value by name
     * TODO: to be implemented
     */
    public String cookie(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * redirect history responses.
     */
    public List<Response<byte[]>> history() {
        return historyResponses;
    }

    /**
     * return request which produce this response
     * TODO: to be implemented
     */
    public Request request() {
        throw new UnsupportedOperationException();
    }

    void addHistory(Response<byte[]> resp) {
        if (historyResponses == null) {
            historyResponses = new ArrayList<>();
        }
        historyResponses.add(resp);
    }
}
