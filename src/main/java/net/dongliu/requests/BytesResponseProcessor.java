package net.dongliu.requests;

import net.dongliu.requests.ResponseProcessor;
import net.dongliu.requests.struct.Headers;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * http handler convert http response data to bytes
 *
 * @author Dong Liu
 */
final class BytesResponseProcessor implements ResponseProcessor<byte[]> {

    @Override
    public byte[] convert(int statusCode, Headers headers, HttpEntity httpEntity)
            throws IOException {
        return (EntityUtils.toByteArray(httpEntity));
    }
}
