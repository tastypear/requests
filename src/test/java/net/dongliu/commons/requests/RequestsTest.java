package net.dongliu.commons.requests;

import net.dongliu.commons.lang.Charsets;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestsTest {

    @Test
    public void testGet() throws Exception {
        Response<String> response = Requests.string(Charsets.UTF_8).url("http://www.baidu.com")
                .get();
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testHttpsGet() throws Exception {
        Response<String> response = Requests.string().url("https://www.google.com")
                .disableSslVerify()
                .get();
        assertEquals(200, response.getStatusCode());
    }
}