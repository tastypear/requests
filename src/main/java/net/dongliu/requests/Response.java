package net.dongliu.requests;

import net.dongliu.requests.lang.Cookies;
import net.dongliu.requests.lang.Headers;

import java.util.ArrayList;
import java.util.List;

/**
 * http response, with statusCode, headers, and data
 *
 * @author Dong Liu
 */
public class Response<T> {
    private int statusCode;
    private Headers headers;
    private Cookies cookies;
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

    public Headers headers() {
        return headers;
    }

    void headers(Headers headers) {
        this.headers = headers;
    }

    public T body() {
        return body;
    }

    void body(T body) {
        this.body = body;
    }

    void cookies(Cookies cookies) {
        this.cookies = cookies;
    }

    /**
     * get cookies
     */
    public Cookies cookies() {
        return this.cookies;
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
