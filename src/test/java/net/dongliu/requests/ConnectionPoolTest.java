package net.dongliu.requests;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ConnectionPoolTest {

    @Test
    public void testHttps() throws IOException {
        ConnectionPool connectionPoll = ConnectionPool.custom().verify(false).build();
        Response<String> response = Requests.get("https://kyfw.12306.cn/otn/")
                .connectionPool(connectionPoll)
                .text();
        assertEquals(200, response.getStatusCode());
        connectionPoll.close();
    }


    @Test
    public void testMultiThread() throws IOException {
        ConnectionPool connectionPoll = ConnectionPool.custom().build();
        for (int i = 0; i < 100; i++) {
            Response<String> response = Requests.get("http://www.baidu.com/")
                    .connectionPool(connectionPoll)
                    .text();
            assertEquals(200, response.getStatusCode());
        }
        connectionPoll.close();
    }
}