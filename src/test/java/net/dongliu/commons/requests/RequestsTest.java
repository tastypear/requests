package net.dongliu.commons.requests;

import net.dongliu.commons.lang.Charsets;
import net.dongliu.commons.lang.collection.Maps;
import net.dongliu.commons.lang.collection.Pair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestsTest {

    @Test
    public void testGet() throws Exception {
        Response<String> response = Requests.text(Charsets.UTF_8).url("http://www.baidu.com")
                .get();
        assertEquals(200, response.statusCode());

        Response<String> resp = Requests.get("http://www.baidu.com");
        assertEquals(200, resp.statusCode());

        resp = Requests.get("http://www.baidu.com/s", Maps.of(Pair.of("wd", "test")));
        assertEquals(200, resp.statusCode());
    }

    @Test
    public void testHttpsGet() throws Exception {
        Response<String> response = Requests.string().url("https://www.google.com")
                .verify(false)
                .gzip(true)
                .get();
        assertEquals(200, response.statusCode());

        response = Requests.string()
                .url("https://play.google.com/store/apps/details?id=aiMinesweeper.WhiteSnow")
                .verify(false)
                .gzip(true)
                .get();

        assertEquals(404, response.statusCode());
    }
}