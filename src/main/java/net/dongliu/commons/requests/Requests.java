package net.dongliu.commons.requests;

import net.dongliu.commons.lang.collection.Pair;
import net.dongliu.commons.requests.code.ResponseConverter;
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
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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

    protected Requests(HttpRequestBase request, RequestConfig config, ResponseConverter<T> transformer,
                       CredentialsProvider provider, boolean gzip, boolean checkSsl) {
        this.request = request;
        this.config = config;
        this.transformer = transformer;
        this.provider = provider;
        this.gzip = gzip;
        this.checkSsl = checkSsl;
    }

    /**
     * execute http requests, and get result
     *
     * @return
     */
    public Response<T> execute() throws IOException {
        request.setConfig(config);
        if (gzip) {
            request.addHeader(Header.Accept_Encoding, Header.Accept_Encoding_COMPRESS);
        }

        HttpClientBuilder clientBuilder = HttpClients.custom();
        if (provider != null) {
            clientBuilder.setDefaultCredentialsProvider(provider);
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

        try (CloseableHttpClient client = clientBuilder.build()) {
            try (CloseableHttpResponse httpResponse = client.execute(request)) {
                Response<T> response = new Response<>();
                response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
                org.apache.http.Header[] respHeaders = httpResponse.getAllHeaders();
                List<Pair<String, String>> headers = new ArrayList<>(respHeaders.length);
                for (org.apache.http.Header header : respHeaders) {
                    headers.add(Pair.of(header.getName(), header.getValue()));
                }
                response.setHeaders(headers);
                T result = transformer.convert(httpResponse.getEntity());
                response.setBody(result);
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
     * get one requests client for return string result.
     */
    public static RequestBuilder<String> stringClient() {
        return client(ResponseConverter.string);
    }

    /**
     * get one requests client for return byte array result.
     */
    public static RequestBuilder<byte[]> bytesClient() {
        return client(ResponseConverter.bytes);
    }


}