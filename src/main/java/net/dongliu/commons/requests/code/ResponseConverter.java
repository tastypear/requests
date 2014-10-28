package net.dongliu.commons.requests.code;

import net.dongliu.commons.lang.Charsets;
import org.apache.http.HttpEntity;

import java.io.IOException;

/**
 * interface to trans body to result
 *
 * @author Dong Liu
 */
public interface ResponseConverter<T> {

    static ResponseConverter<String> string = new StringResponseConverter(Charsets.UTF_8);

    static ResponseConverter<byte[]> bytes = new BytesResponseConverter();

    /**
     * from http Body to result with type T
     */
    T convert(HttpEntity httpEntity) throws IOException;
}
