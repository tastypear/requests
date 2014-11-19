package net.dongliu.requests;

import com.sun.deploy.util.StringUtils;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dong Liu dongliu@live.cn
 */
public class Request {

    private final HttpRequestBase request;
    private final RequestConfig config;
    private final CredentialsProvider provider;
    // if enable gzip response
    private final boolean gzip;
    // if verify certificate of https site
    private final boolean verify;
    private final List<Header> headers;
    private final List<Cookie> cookies;
    private final boolean allowRedirects;

    Request(HttpRequestBase request, CredentialsProvider provider, List<Header> headers,
            boolean gzip, boolean verify, RequestConfig config,
            List<Cookie> cookies, boolean allowRedirects) {
        this.request = request;
        this.headers = headers;
        this.config = config;
        this.provider = provider;
        this.gzip = gzip;
        this.verify = verify;
        this.cookies = cookies;
        this.allowRedirects = allowRedirects;

        // set cookie header
        if (!cookies.isEmpty()) {
            List<String> strs = new ArrayList<>(cookies.size());
            for (Cookie cookie : cookies) {
                strs.add(cookie.string());
            }
            headers.add(Header.of("Cookie", StringUtils.join(strs, ";")));
        }
        // set gzip header
        if (gzip) {
            headers.add(Header.of(Header.Accept_Encoding, Header.Accept_Encoding_COMPRESS));
        }

        for (Header header : headers) {
            request.addHeader(header.getName(), header.valueAsString());
        }
        request.setConfig(config);
    }

    HttpRequestBase request() {
        return request;
    }

    RequestConfig config() {
        return config;
    }

    CredentialsProvider provider() {
        return provider;
    }

    public boolean gzip() {
        return gzip;
    }

    public boolean verify() {
        return verify;
    }

    public List<Cookie> cookies() {
        return cookies;
    }

    public boolean allowRedirects() {
        return allowRedirects;
    }

    /**
     * return headers this request send
     *
     * @return
     */
    public List<Header> headers() {
        return headers;
    }

    /**
     * return header with name this req send
     *
     * @param name
     * @return
     */
    public String header(String name) {
        for (Header header : headers) {
            if (header.getName().equals(name)) {
                return header.getValue();
            }
        }
        return null;
    }

    /**
     * return url of this request
     *
     * @return
     */
    public URI url() {
        return request.getURI();
    }
}
