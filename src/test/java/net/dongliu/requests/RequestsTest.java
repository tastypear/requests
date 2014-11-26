package net.dongliu.requests;

import net.dongliu.requests.struct.MultiPart;
import org.apache.commons.io.Charsets;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RequestsTest {

    @Test
    public void testGet() throws Exception {
        Response<String> response = Requests.text(Charsets.UTF_8).url("http://www.baidu.com")
                .get();
        assertEquals(200, response.getStatusCode());

        Response<String> resp = Requests.text().url("http://www.baidu.com").get();
        assertEquals(200, resp.getStatusCode());

        Map<String, String> map = new HashMap<>();
        map.put("wd", "test");
        resp = Requests.text().url("http://www.baidu.com/s").params(map).get();
        assertEquals(200, resp.getStatusCode());
    }

    @Test
    public void testCookie() {
        Response<String> response = Requests.text().url("http://www.baidu.com")
                .cookie("test", "value").get();
        //assertEquals("test=value", response.getRequest().getHeaders().getFirst("Cookie").getValue());
        assertTrue(response.getBody().contains("window"));
        assertNotNull(response.getCookies().getFirst("BAIDUID"));
    }

    @Test
    public void testBasicAuth() {
//        Response<String> response = Requests.string()
//                .url("http://xxx")
//                .auth("xx", "xx")
//                .get();
//        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testHttps() {
        Response<String> resp = Requests.text().url("https://kyfw.12306.cn/otn/")
                .verify(false).get();
        assertEquals(200, resp.getStatusCode());
    }

    @Test
    public void testRedirect() {
        Response<String> resp = Requests.text().url("http://www.dongliu.net/")
                .get();
        assertEquals(200, resp.getStatusCode());
        assertEquals(301, resp.getHistory().get(0).getStatusCode());
    }

    @Test
    public void testProxy() {
//        Response<String> resp = Requests.string().url("http://www.baidu.com/")
//                .proxy("http://127.0.0.1:8000/")
//                .get();
//        assertEquals(200, resp.getStatusCode());
    }

    @Test
    public void testMultiPart() {
        Response<String> response = Requests.text().url("http://10.0.11.48:5000/upload")
                .files(MultiPart.of("file",
                        "/Users/dongliu/code/java/requests/src/test/java/net/dongliu/requests/RequestsTest.java"))
                .post();
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
    }
}