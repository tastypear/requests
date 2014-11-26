package net.dongliu.requests;

import net.dongliu.requests.exception.InvalidUrlException;
import net.dongliu.requests.struct.Method;

/**
 * construct and execute http requests
 *
 * @author Dong Liu
 */
public class Requests {

    /**
     * get method
     */
    public static RequestBuilder get(String url) throws InvalidUrlException {
        return newBuilder(url).method(Method.GET);
    }

    /**
     * head method
     */
    public static RequestBuilder head(String url) throws InvalidUrlException {
        return newBuilder(url).method(Method.HEAD);
    }

    /**
     * get url, and return content
     */
    public static RequestBuilder post(String url) throws InvalidUrlException {
        return newBuilder(url).method(Method.POST);
    }

    /**
     * put method
     */
    public static RequestBuilder put(String url) throws InvalidUrlException {
        return newBuilder(url).method(Method.PUT);
    }

    /**
     * delete method
     */
    public static RequestBuilder delete(String url) throws InvalidUrlException {
        return newBuilder(url).method(Method.DELETE);
    }

//    /**
//     * options method
//     */
//    public static RequestBuilder options(String url) throws InvalidUrlException {
//        return newBuilder(url).method(Method.OPTIONS);
//    }
//
//    /**
//     * patch method
//     */
//    public static RequestBuilder patch(String url) throws InvalidUrlException {
//        return newBuilder(url).method(Method.PATCH);
//    }
//
//    /**
//     * trace method
//     */
//    public static RequestBuilder trace(String url) throws InvalidUrlException {
//        return newBuilder(url).method(Method.TRACE);
//    }
//
//    /**
//     * connect
//     */
//    public static RequestBuilder connect(String url) throws InvalidUrlException {
//        return newBuilder(url).method(Method.CONNECT);
//    }

    /**
     * create request builder with url
     */
    private static RequestBuilder newBuilder(String url) throws InvalidUrlException {
        return new RequestBuilder().url(url);
    }


}