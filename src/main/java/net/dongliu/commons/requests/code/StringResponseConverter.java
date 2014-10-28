package net.dongliu.commons.requests.code;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * convert http response to String
 *
 * @author Dong Liu
 */
public class StringResponseConverter implements ResponseConverter<String> {
    private final Charset charset;

    public StringResponseConverter(Charset charset) {
        this.charset = charset;
    }

    @Override
    public String convert(HttpEntity httpEntity) {
        try {
            return EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
