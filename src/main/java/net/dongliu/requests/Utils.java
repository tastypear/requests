package net.dongliu.requests;

import net.dongliu.requests.struct.Proxy;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author Dong Liu dongliu@wandoujia.com
 */
public class Utils {
    static Registry<ConnectionSocketFactory> getConnectionSocketFactoryRegistry(
            Proxy proxy, boolean verify) {
        SSLContext sslContext;

        // trust all http certificate
        if (!verify) {
            try {
                sslContext = SSLContexts.custom().useTLS().build();
                sslContext.init(new KeyManager[0], new TrustManager[]{new AllTrustManager()},
                        new SecureRandom());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException(e);
            }
        } else {
            sslContext = SSLContexts.createSystemDefault();
        }

        SSLConnectionSocketFactory sslsf = new CustomSSLConnectionSocketFactory(sslContext,
                proxy, verify);
        PlainConnectionSocketFactory psf = new CustomConnectionSocketFactory(proxy);
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", psf)
                .register("https", sslsf)
                .build();
    }
}
