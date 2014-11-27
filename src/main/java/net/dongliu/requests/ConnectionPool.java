package net.dongliu.requests;

import net.dongliu.requests.struct.Host;
import net.dongliu.requests.struct.Pair;
import org.apache.http.HttpHost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * http connection pool. use this to reuse http connection across http requests.
 * This class is thread-safe, can service connection requests from multiple execution threads.
 *
 * @author Dong Liu dongliu@wandoujia.com
 */
public class ConnectionPool implements Closeable {

    private final PoolingHttpClientConnectionManager manager;

    public ConnectionPool(PoolingHttpClientConnectionManager manager) {
        this.manager = manager;
    }

    public ConnectionPoolBuilder custom() {
        return new ConnectionPoolBuilder();
    }

    @Override
    public void close() throws IOException {
        manager.close();
    }

    HttpClientConnectionManager getConnectionManager() {
        return manager;
    }

    public class ConnectionPoolBuilder {
        // how long http connection keep, in milliseconds. default -1, get from server response
        private long timeToLive = -1;
        // the max total http connection count
        private int maxTotal = 20;
        // the max connection count for each host
        private int maxPerRoute = 2;
        // set max count for specified host
        private List<Pair<Host, Integer>> perRouteCount;

        ConnectionPoolBuilder() {
        }

        public ConnectionPool build() {
            PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(
                    timeToLive, TimeUnit.MILLISECONDS);
            manager.setMaxTotal(maxTotal);
            manager.setDefaultMaxPerRoute(maxPerRoute);
            if (perRouteCount != null) {
                for (Pair<Host, Integer> pair : perRouteCount) {
                    Host host = pair.getName();
                    manager.setMaxPerRoute(
                            new HttpRoute(new HttpHost(host.getDomain(), host.getPort())),
                            pair.getValue());
                }
            }

            return new ConnectionPool(manager);
        }

        /**
         * how long http connection keep, in milliseconds. default -1, get from server response
         */
        public ConnectionPoolBuilder timeToLive(long timeToLive) {
            this.timeToLive = timeToLive;
            return this;
        }

        /**
         * the max total http connection count. default 20
         */
        public ConnectionPoolBuilder maxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
            return this;
        }

        /**
         * set default max connection count for each host, default 2
         */
        public ConnectionPoolBuilder maxPerRoute(int maxPerRoute) {
            this.maxPerRoute = maxPerRoute;
            return this;
        }

        /**
         * set specified max connection count for the host, default 2
         */
        public ConnectionPoolBuilder maxPerRoute(Host host, int maxPerRoute) {
            ensurePerRouteCount();
            this.perRouteCount.add(new Pair<>(host, maxPerRoute));
            return this;
        }

        private void ensurePerRouteCount() {
            if (this.perRouteCount == null) {
                this.perRouteCount = new ArrayList<>();
            }
        }
    }
}
