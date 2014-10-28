package net.dongliu.commons.requests.code;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * http handler convert http response body to bytes
 *
 * @author Dong Liu
 */
public class BytesResponseConverter implements ResponseConverter<byte[]> {

    @Override
    public byte[] convert(HttpEntity httpEntity) {
        try {
            return (EntityUtils.toByteArray(httpEntity));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
