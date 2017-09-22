package io.jokester.tidyj;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("WeakerAccess")
public class TestTidyJ {

    @Test
    public void test1() {
        assertEquals(2, 1 + 1);
    }

    @Test
    public void createInstance() {
        TidyHTML5 t = TidyHTML5.parseString("");
        assertEquals(1, 1);
        int c = t.nativeParseString("heeeo");
        assertEquals(5, c);
        System.out.println("line");
    }
}
