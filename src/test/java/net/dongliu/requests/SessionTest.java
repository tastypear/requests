package net.dongliu.requests;

import org.junit.Test;

import static org.junit.Assert.*;

public class SessionTest {

    @Test
    public void testSession() {
        Session session = Requests.session();
        Response<String> response = session.get("http://www.baidu.com").text();
        assertEquals(200, response.getStatusCode());
    }
}