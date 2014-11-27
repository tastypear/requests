package net.dongliu.requests;

import net.dongliu.requests.exception.RuntimeIOException;
import net.dongliu.requests.struct.*;
import org.apache.commons.io.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * execute request and get response result.
 *
 * @param <T> the response body type
 * @author Dong Liu dongliu@wandoujia.com
 */
class RequestExecutor<T> {
    private final Request request;
    private final ResponseProcessor<T> processor;

    RequestExecutor(Request request, ResponseProcessor<T> processor) {
        this.request = request;
        this.processor = processor;
    }

    /**
     * execute request, get http response, and convert response with processor
     */
    Response<T> execute() throws RuntimeIOException {
        CredentialsProvider provider = new BasicCredentialsProvider();
        HttpClientContext context = HttpClientContext.create();
        CookieStore cookieStore;
        if (request.getSession() != null) {
            cookieStore = request.getSession().getCookieStore();
        } else {
            cookieStore = new BasicCookieStore();
        }
        context.setCookieStore(cookieStore);
        HttpRequestBase httpRequest = buildRequest(provider, context);
        try (CloseableHttpClient client = buildHttpClient(provider, context)) {
            // do http request with http client
            try (CloseableHttpResponse httpResponse = client.execute(httpRequest, context)) {
                return wrapResponse(httpResponse, context);
            } catch (IOException e) {
                throw RuntimeIOException.of(e);
            }
        } catch (IOException e) {
            throw RuntimeIOException.of(e);
        }
    }

    /**
     * build http client
     */
    private CloseableHttpClient buildHttpClient(CredentialsProvider provider,
                                                HttpClientContext context) {
        HttpClientBuilder clientBuilder = HttpClients.custom().setUserAgent(request.getUserAgent());

        // basic auth
        if (request.getAuthInfo() != null) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                    request.getAuthInfo().getUserName(), request.getAuthInfo().getPassword());
            provider.setCredentials(
                    new AuthScope(request.getUrl().getHost(), request.getUrl().getPort()),
                    credentials);
        }

        clientBuilder.setDefaultCredentialsProvider(provider);

        // accept all https
        if (!request.isVerify()) {
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

        // disable gzip
        if (!request.isGzip()) {
            clientBuilder.disableContentCompression();
        }

        // get response
        if (!request.isAllowRedirects()) {
            clientBuilder.disableRedirectHandling();
        }

        return clientBuilder.build();
    }

    // build http request
    private HttpRequestBase buildRequest(CredentialsProvider provider, HttpClientContext context) {
        URI uri = buildFullUrl(request.getUrl(), request.getParameters());
        HttpRequestBase httpRequest;
        switch (request.getMethod()) {
            case POST:
                httpRequest = buildHttpPost(uri, request);
                break;
            case GET:
                httpRequest = new HttpGet(uri);
                break;
            case HEAD:
                httpRequest = new HttpHead(uri);
                break;
            case PUT:
                httpRequest = buildHttpPut(uri, request);
                break;
            case DELETE:
                httpRequest = new HttpDelete(uri);
                break;
            case OPTIONS:
                httpRequest = new HttpOptions(uri);
                break;
            case TRACE:
                httpRequest = new HttpTrace(uri);
                break;
            case PATCH:
                httpRequest = buildHttpPatch(uri, request);
                break;
            case CONNECT:
            default:
                throw new UnsupportedOperationException("Unsupported method:" + request.getMethod());
        }


        RequestConfig.Builder configBuilder = RequestConfig.custom()
                .setConnectTimeout(request.getConnectTimeout())
                .setSocketTimeout(request.getSocketTimeout())
                .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);
        //proxy
        if (request.getProxy() != null) {
            //TODO: socket proxy support
            Proxy proxy = request.getProxy();
            if (proxy.getAuthInfo() != null) {
                provider.setCredentials(new AuthScope(proxy.getHost(), proxy.getPort()),
                        new UsernamePasswordCredentials(proxy.getUserName(), proxy.getPassword()));
            }
            HttpHost httpHost = new HttpHost(proxy.getHost(), proxy.getPort(), proxy.getScheme());
            configBuilder.setProxy(httpHost);
        }
        httpRequest.setConfig(configBuilder.build());

        // set cookie
        CookieStore cookieStore = context.getCookieStore();
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                BasicClientCookie clientCookie = new BasicClientCookie(cookie.getName(),
                        cookie.getValue());
                clientCookie.setDomain(request.getUrl().getHost());
                clientCookie.setPath("/");
                cookieStore.addCookie(clientCookie);
            }
        }

        // set headers
        if (request.getHeaders() != null) {
            for (Header header : request.getHeaders()) {
                httpRequest.setHeader(header.getName(), header.getValue());
            }
        }

        return httpRequest;
    }

    private HttpRequestBase buildHttpPut(URI uri, Request request) {
        checkHttpBody(request);
        HttpPut httpPut = new HttpPut(uri);
        if (request.getBody() != null) {
            httpPut.setEntity(new ByteArrayEntity(request.getBody()));
        } else if (request.getIn() != null) {
            httpPut.setEntity(new InputStreamEntity(request.getIn()));
        } else if (request.getParamBody() != null) {
            // use www-form-urlencoded to send params
            List<BasicNameValuePair> paramList = new ArrayList<>(request.getParamBody().size());
            for (Parameter param : request.getParamBody()) {
                paramList.add(new BasicNameValuePair(param.getName(), param.getValue()));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, Charsets.UTF_8);
            httpPut.setEntity(entity);
        }
        return httpPut;
    }


    private HttpPost buildHttpPost(URI uri, Request request) {
        checkHttpBody(request);

        HttpPost httpPost = new HttpPost(uri);
        if (request.getMultiParts() != null) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            for (Parameter parameter : request.getParamBody()) {
                entityBuilder.addTextBody(parameter.getName(), parameter.getValue());
            }
            for (MultiPart f : request.getMultiParts()) {
                entityBuilder.addBinaryBody(f.getName(), f.getFile(),
                        ContentType.create(f.getMime()), f.getFileName());
            }
            httpPost.setEntity(entityBuilder.build());
        } else if (request.getBody() != null) {
            httpPost.setEntity(new ByteArrayEntity(request.getBody()));
        } else if (request.getIn() != null) {
            httpPost.setEntity(new InputStreamEntity(request.getIn()));
        } else if (request.getParamBody() != null) {
            // use www-form-urlencoded to send params
            List<BasicNameValuePair> paramList = new ArrayList<>(request.getParamBody().size());
            for (Parameter param : request.getParamBody()) {
                paramList.add(new BasicNameValuePair(param.getName(), param.getValue()));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, Charsets.UTF_8);
            httpPost.setEntity(entity);
        }
        return httpPost;
    }


    private HttpRequestBase buildHttpPatch(URI uri, Request request) {
        checkHttpBody(request);
        HttpPatch httpPatch = new HttpPatch(uri);
        if (request.getBody() != null) {
            httpPatch.setEntity(new ByteArrayEntity(request.getBody()));
        } else if (request.getIn() != null) {
            httpPatch.setEntity(new InputStreamEntity(request.getIn()));
        } else if (request.getParamBody() != null) {
            // use www-form-urlencoded to send params
            List<BasicNameValuePair> paramList = new ArrayList<>(request.getParamBody().size());
            for (Parameter param : request.getParamBody()) {
                paramList.add(new BasicNameValuePair(param.getName(), param.getValue()));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, Charsets.UTF_8);
            httpPatch.setEntity(entity);
        }
        return httpPatch;
    }

    /**
     * make sure only one http body was set
     */
    private void checkHttpBody(Request request) {
        int bodyCount = 0;
        if (request.getBody() != null) bodyCount++;
        if (request.getIn() != null) bodyCount++;
        if (request.getParamBody() != null) bodyCount++;
        if (request.getMultiParts() != null) bodyCount++;
        if (bodyCount > 1) {
            //can not set both
            throw new RuntimeException("More than one http request body have been set");
        }
    }

    // build full url with parameters
    private URI buildFullUrl(URI url, Parameters parameters) {
        try {
            if (parameters == null || parameters.isEmpty()) {
                return url;
            }
            URIBuilder urlBuilder = new URIBuilder(url);
            for (Parameter param : parameters) {
                urlBuilder.addParameter(param.getName(), param.getValue());
            }
            return urlBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * do http request with http client
     */
    private Response<T> wrapResponse(CloseableHttpResponse httpResponse,
                                     HttpClientContext context) throws IOException {
        Response<T> response = new Response<>();
        response.setRequest(request);
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        // get headers
        org.apache.http.Header[] respHeaders = httpResponse.getAllHeaders();
        Headers headers = new Headers();
        for (org.apache.http.Header header : respHeaders) {
            headers.add(new Header(header.getName(), header.getValue()));
        }
        response.setHeaders(headers);

        // get cookies
        Cookies cookies = new Cookies();
        for (org.apache.http.cookie.Cookie c : context.getCookieStore().getCookies()) {
            Cookie cookie = new Cookie(c.getName(), c.getValue());
            cookie.setPath(c.getPath());
            cookie.setDomain(c.getDomain());
            cookie.setPath(c.getPath());
            cookie.setExpiry(c.getExpiryDate());
            cookies.add(cookie);
        }
        response.setCookies(cookies);

        response.setHistory(context.getRedirectLocations());

        HttpEntity entity = httpResponse.getEntity();
        T result = processor.convert(response.getStatusCode(), headers, entity);
        response.setBody(result);
        return response;
    }

}
