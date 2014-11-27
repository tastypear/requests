package net.dongliu.requests;

import org.apache.http.HttpClientConnection;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * used to share connection manager across http client.
 * http client contains too many properties, such as proxy, compress and other setting.
 * we want to reuse the same connection, but with different http client settings
 *
 * @author Dong Liu dongliu@wandoujia.com
 */
class ConnectionManagerWrapper implements HttpClientConnectionManager {

    private final HttpClientConnectionManager manager;

    public ConnectionManagerWrapper(HttpClientConnectionManager manager) {
        this.manager = manager;
    }

    @Override
    public ConnectionRequest requestConnection(HttpRoute route, Object state) {
        return manager.requestConnection(route, state);
    }

    @Override
    public void releaseConnection(HttpClientConnection conn, Object newState, long validDuration,
                                  TimeUnit timeUnit) {
        manager.releaseConnection(conn, newState, validDuration, timeUnit);
    }

    @Override
    public void connect(HttpClientConnection conn, HttpRoute route, int connectTimeout,
                        HttpContext context) throws IOException {
        manager.connect(conn, route, connectTimeout, context);
    }

    @Override
    public void upgrade(HttpClientConnection conn, HttpRoute route, HttpContext context)
            throws IOException {
        manager.upgrade(conn, route, context);
    }

    @Override
    public void routeComplete(HttpClientConnection conn, HttpRoute route, HttpContext context)
            throws IOException {
        manager.routeComplete(conn, route, context);
    }

    @Override
    public void closeIdleConnections(long idletime, TimeUnit tunit) {
        manager.closeIdleConnections(idletime, tunit);
    }

    @Override
    public void closeExpiredConnections() {
        manager.closeExpiredConnections();
    }

    @Override
    public void shutdown() {
        // do nothings
    }
}
