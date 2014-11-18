package net.dongliu.requests.code;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * http handler convert http response data to bytes
 *
 * @author Dong Liu
 */
public class BytesResponseConverter implements ResponseConverter<byte[]> {

    @Override
    public byte[] convert(HttpEntity httpEntity) throws IOException {
        return (EntityUtils.toByteArray(httpEntity));
    }
}
