package net.dongliu.requests.utils;

import net.dongliu.requests.exception.IllegalEncodingException;
import net.dongliu.requests.struct.Pair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * utils method for decode and encode
 *
 * @author Dong Liu dongliu@wandoujia.com
 */
public class Coder {

    public static <T extends Pair<String, String>> String encode(T data, String charset) {
        try {
            if (data.getName() == null && data.getValue() != null) {
                return URLEncoder.encode(data.getValue(), charset);
            } else if (data.getValue() == null && data.getName() != null) {
                return URLEncoder.encode(data.getName(), charset);
            } else if (data.getName() != null) {
                return URLEncoder.encode(data.getName(), charset) + "=" +
                        URLEncoder.encode(data.getValue(), charset);
            } else {
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            throw IllegalEncodingException.of(e);
        }
    }
}
