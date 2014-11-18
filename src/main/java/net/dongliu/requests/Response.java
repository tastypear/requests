package net.dongliu.requests;

import java.net.HttpCookie;
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
    private List<Header> headers = Collections.emptyList();
    private List<Cookie> cookies;
    private T body;

    private List<Response<byte[]>> historyResponses;
    private Request request;

    Response() {
    }

    public int statusCode() {
        return statusCode;
    }

    void statusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    List<Header> headers() {
        return headers;
    }

    public void headers(List<Header> headers) {
        this.headers = headers;
    }

    /**
     * get first match header value by header name
     */
    public String header(String name) {
        for (Header header : headers) {
            if (header.getName().equals(name)) {
                return header.getValue();
            }
        }
        return null;
    }

    public T body() {
        return body;
    }

    void body(T body) {
        this.body = body;
    }

    void cookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    /**
     * get cookies
     */
    public List<Cookie> cookies() {
        return this.cookies;
    }

    /**
     * get cookie value by name
     */
    public Cookie cookie(String name) {
        for (Cookie cookie : cookies()) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * redirect history responses.
     */
    public List<Response<byte[]>> history() {
        return historyResponses;
    }

    /**
     * return request which produce this response
     */
    public Request request() {
        return this.request;
    }

    void request(Request request) {
        this.request = request;
    }

    void addHistory(Response<byte[]> resp) {
        if (historyResponses == null) {
            historyResponses = new ArrayList<>();
        }
        historyResponses.add(resp);
    }
}
