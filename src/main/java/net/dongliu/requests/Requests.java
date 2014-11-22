package net.dongliu.requests;

import net.dongliu.requests.converter.ResponseConverter;
import net.dongliu.requests.converter.StringResponseConverter;
import net.dongliu.requests.exception.RuntimeIOException;
import net.dongliu.requests.lang.Cookie;
import net.dongliu.requests.lang.Cookies;
import net.dongliu.requests.lang.Header;
import net.dongliu.requests.lang.Headers;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.HttpCookie;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

/**
 * construct and execute http requests
 *
 * @param <T> the response Type
 * @author Dong Liu
 */
public class Requests<T> {

    // the http response processor
    private final ResponseConverter<T> transformer;
    private final Request request;

    Requests(Request request, ResponseConverter<T> transformer) {
        this.request = request;
        this.transformer = transformer;
    }

    /**
     * execute http requests, and get result
     *
     * @return
     */
    Response<T> execute() throws RuntimeIOException {
        HttpClientBuilder clientBuilder = HttpClients.custom();
        if (request.provider() != null) {
            clientBuilder.setDefaultCredentialsProvider(request.provider());
        }
        if (!request.verify()) {
            SSLContext sslContext;
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(new KeyManager[0], new TrustManager[]{new AllTrustManager()},
                        new SecureRandom());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException(e);
            }
            SSLContext.setDefault(sslContext);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            clientBuilder.setSSLSocketFactory(sslsf);
        }

        Response<T> response = new Response<>();
        if (request.allowRedirects()) {
            clientBuilder.setRedirectStrategy(new CustomRedirectStrategy(response));
        } else {
            clientBuilder.disableRedirectHandling();
        }

        //disable auto gzip handles
        clientBuilder.disableContentCompression();

        try (CloseableHttpClient client = clientBuilder.build()) {
            try (CloseableHttpResponse httpResponse = client.execute(request.request())) {
                response.statusCode(httpResponse.getStatusLine().getStatusCode());
                org.apache.http.Header[] respHeaders = httpResponse.getAllHeaders();
                Headers headers = new Headers();
                Cookies cookies = new Cookies();
                for (org.apache.http.Header header : respHeaders) {
                    headers.add(Header.of(header.getName(), header.getValue()));
                    if (header.getName().equalsIgnoreCase("Set-Cookie")) {
                        List<HttpCookie> httpCookies = HttpCookie.parse(
                                header.getName() + ": " + header.getValue());
                        for (HttpCookie httpCookie : httpCookies) {
                            Cookie cookie = new Cookie();
                            cookie.setDomain(httpCookie.getDomain());
                            cookie.setName(httpCookie.getName());
                            cookie.setPath(httpCookie.getPath());
                            cookie.setValue(httpCookie.getValue());
                            cookie.setExpiry(httpCookie.getMaxAge());
                            cookies.add(cookie);
                        }
                    }
                }
                response.headers(headers);
                response.cookies(cookies);
                HttpEntity entity;
                switch (useCompress(headers)) {
                    case 1:
                        entity = new GzipDecompressingEntity(httpResponse.getEntity());
                        break;
                    case 2:
                        entity = new DeflateDecompressingEntity(httpResponse.getEntity());
                        break;
                    case 0:
                    default:
                        entity = httpResponse.getEntity();
                }

                T result = transformer.convert(entity);
                response.body(result);
                response.request(request);
                return response;
            }
        } catch (IOException e) {
            throw RuntimeIOException.of(e);
        }
    }

    private int useCompress(Headers headers) {
        Header header = headers.getFirst("Content-Encoding");
        if (header == null) {
            return 0;
        }
        String ec = header.getValue().toLowerCase();
        if (ec.contains("gzip")) {
            return 1;
        } else if (ec.contains("deflate")) {
            return 2;
        } else {
            return 0;
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

}