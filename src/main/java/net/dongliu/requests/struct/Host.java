package net.dongliu.requests.struct;

/**
 * host, contains domain and port
 *
 * @author Dong Liu dongliu@wandoujia.com
 */
public class Host {
    private String domain;
    private int port;

    /**
     * create host with domain and port 80
     */
    public Host(String domain) {
        this(domain, 80);
    }

    /**
     * create host with domain and port
     */
    public Host(String domain, int port) {
        this.domain = domain;
        this.port = port;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Host host = (Host) o;

        if (port != host.port) return false;
        if (domain != null ? !domain.equals(host.domain) : host.domain != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = domain != null ? domain.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "Host " + domain + ":" + port;
    }
}
