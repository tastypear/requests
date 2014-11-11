package net.dongliu.commons.requests;

import net.dongliu.commons.lang.collection.Pair;
import net.dongliu.commons.requests.code.ResponseConverter;
import net.dongliu.commons.requests.code.StringResponseConverter;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * construct and execute http requests
 *
 * @param <T> the response Type
 * @author Dong Liu
 */
public class Requests<T> {
    private final HttpRequestBase request;
    // the http response processor
    private final ResponseConverter<T> transformer;
    private final RequestConfig config;
    private final CredentialsProvider provider;
    // if enable gzip response
    private final boolean gzip;
    // if verify certificate of https site
    private final boolean checkSsl;
    private final CookieStore cookieStore;
    private final boolean allowRedirects;

    Requests(HttpRequestBase request, RequestConfig config, ResponseConverter<T> transformer,
             CredentialsProvider provider, boolean gzip, boolean checkSsl, CookieStore cookieStore,
             boolean allowRedirects) {
        this.request = request;
        this.config = config;
        this.transformer = transformer;
        this.provider = provider;
        this.gzip = gzip;
        this.checkSsl = checkSsl;
        this.cookieStore = cookieStore;
        this.allowRedirects = allowRedirects;
    }

    /**
     * execute http requests, and get result
     *
     * @return
     */
    Response<T> execute() throws IOException {
        request.setConfig(config);
        if (gzip) {
            request.addHeader(Header.Accept_Encoding, Header.Accept_Encoding_COMPRESS);
        }

        HttpClientBuilder clientBuilder = HttpClients.custom();
        if (provider != null) {
            clientBuilder.setDefaultCredentialsProvider(provider);
        }
        if (cookieStore != null) {
            clientBuilder.setDefaultCookieStore(cookieStore);
        }
        if (!checkSsl) {
            SSLConnectionSocketFactory sslsf;
            try {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                sslsf = new SSLConnectionSocketFactory(builder.build());
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                throw new RuntimeException(e);
            }
            clientBuilder.setSSLSocketFactory(sslsf);
        }

        Response<T> response = new Response<>();
        if (allowRedirects) {
            clientBuilder.setRedirectStrategy(new CustomRedirectStrategy(response));
        } else {
            clientBuilder.disableRedirectHandling();
        }

        try (CloseableHttpClient client = clientBuilder.build()) {
            try (CloseableHttpResponse httpResponse = client.execute(request)) {
                response.statusCode(httpResponse.getStatusLine().getStatusCode());
                org.apache.http.Header[] respHeaders = httpResponse.getAllHeaders();
                List<Pair<String, String>> headers = new ArrayList<>(respHeaders.length);
                for (org.apache.http.Header header : respHeaders) {
                    headers.add(Pair.of(header.getName(), header.getValue()));
                }
                response.headers(headers);
                T result = transformer.convert(httpResponse.getEntity());
                response.body(result);
                return response;
            }
        }
    }

    /**
     * get one requests client for return result with Type T.
     */
    public static <T> RequestBuilder<T> client(ResponseConverter<T> transformer) {
        return new RequestBuilder<T>().transformer(transformer);
    }

    /**
     * get one requests client for return text result, use default encoding.
     */
    public static RequestBuilder<String> string() {
        return client(ResponseConverter.string);
    }

    /**
     * get one requests client for return text result.
     *
     * @param charSet the encoding to use if not found in response header
     */
    public static RequestBuilder<String> text(Charset charSet) {
        return client(new StringResponseConverter(charSet));
    }

    /**
     * get one requests client for return text result.
     *
     * @param charSet the encoding to use if not found in response header
     */
    public static RequestBuilder<String> text(String charSet) {
        return text(Charset.forName(charSet));
    }

    /**
     * get one requests client for return byte array result.
     */
    public static RequestBuilder<byte[]> bytes() {
        return client(ResponseConverter.bytes);
    }

    /**
     * get url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> get(String url) throws IOException {
        return client(ResponseConverter.string).url(url).get();
    }

    /**
     * get url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> get(String url, Map<String, String> params) throws IOException {
        return client(ResponseConverter.string).url(url).params(params).get();
    }

    /**
     * get url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> get(String url, Charset charset) throws IOException {
        return text(charset).url(url).get();
    }

    /**
     * get url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> get(String url, Map<String, String> params, Charset charset) throws IOException {
        return text(charset).url(url).params(params).get();
    }

    /**
     * get url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> get(String url, String charset) throws IOException {
        return text(Charset.forName(charset)).url(url).get();
    }

    /**
     * get url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> get(String url, Map<String, String> params, String charset) throws IOException {
        return text(Charset.forName(charset)).url(url).params(params).get();
    }

    /**
     * get url, and return response body as binary
     *
     * @throws IOException
     */
    public static Response<byte[]> getBinary(String url) throws IOException {
        return bytes().url(url).get();
    }

    /**
     * get url, and return response body as binary
     *
     * @throws IOException
     */
    public static Response<byte[]> getBinary(String url, Map<String, String> params) throws IOException {
        return bytes().url(url).params(params).get();
    }

    /**
     * post url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> post(String url) throws IOException {
        return client(ResponseConverter.string).url(url).post();
    }

    /**
     * post url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> post(String url, Map<String, String> params) throws IOException {
        return client(ResponseConverter.string).url(url).params(params).post();
    }

    /**
     * post url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> post(String url, byte[] data) throws IOException {
        return client(ResponseConverter.string).url(url).data(data).post();
    }

    /**
     * post url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> post(String url, Charset charset) throws IOException {
        return text(charset).url(url).post();
    }

    /**
     * post url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> post(String url, Map<String, String> params, Charset charset) throws IOException {
        return text(charset).url(url).params(params).post();
    }

    /**
     * post url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> post(String url, byte[] data, Charset charset) throws IOException {
        return text(charset).url(url).data(data).post();
    }

    /**
     * post url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> post(String url, String charset) throws IOException {
        return text(Charset.forName(charset)).url(url).post();
    }

    /**
     * post url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> post(String url, Map<String, String> params, String charset) throws IOException {
        return text(Charset.forName(charset)).url(url).params(params).post();
    }

    /**
     * post url, and return response body as string
     *
     * @throws IOException
     */
    public static Response<String> post(String url, byte[] data, String charset) throws IOException {
        return text(Charset.forName(charset)).url(url).data(data).post();
    }

    /**
     * post url, and return response body as binary
     *
     * @throws IOException
     */
    public static Response<byte[]> postBinary(String url) throws IOException {
        return bytes().url(url).post();
    }

    /**
     * post url, and return response body as binary
     *
     * @throws IOException
     */
    public static Response<byte[]> postBinary(String url, Map<String, String> params) throws IOException {
        return bytes().params(params).url(url).post();
    }

    /**
     * post url, and return response body as binary
     *
     * @throws IOException
     */
    public static Response<byte[]> postBinary(String url, byte[] data) throws IOException {
        return bytes().url(url).data(data).post();
    }

    /**
     * return a session used to do http request and keep cookie and params
     * TODO: to be implemented
     *
     * @return
     */
    public static Session session() {
        throw new UnsupportedOperationException();
    }

}