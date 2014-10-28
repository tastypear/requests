package net.dongliu.commons.requests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestsTest {

    @Test
    public void testGet() throws Exception {
        Response<String> response = Requests.stringClient().url("http://www.baidu.com").get();
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testHttpsGet() throws Exception {
        Response<String> response = Requests.stringClient().url("https://www.google.com")
                .disableSslVerify()
                .get();
        assertEquals(200, response.getStatusCode());
    }
}