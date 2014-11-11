package net.dongliu.commons.requests.code;

import org.apache.http.HttpEntity;

import java.io.IOException;

/**
 * interface to trans data to result
 *
 * @author Dong Liu
 */
public interface ResponseConverter<T> {

    static ResponseConverter<String> string = new StringResponseConverter(null);

    static ResponseConverter<byte[]> bytes = new BytesResponseConverter();

    /**
     * from http Body to result with type T
     */
    T convert(HttpEntity httpEntity) throws IOException;
}
