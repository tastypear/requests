package net.dongliu.requests;

import net.dongliu.requests.struct.MultiPart;
import org.junit.Test;

public class MultiPartTest {

    @Test
    public void testOf() throws Exception {
        MultiPart multiPart = MultiPart.of("file", "MultiPartTest.java");
    }
}