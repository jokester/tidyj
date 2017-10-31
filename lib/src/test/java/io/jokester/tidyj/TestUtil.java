package io.jokester.tidyj;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class TestUtil {

    public static InputStream getTestResStream(String path) {
        return TestUtil.class.getResourceAsStream(path);
    }

    public static String getTestResString(String path) {
        InputStream i = getTestResStream(path);
        Scanner scanner = new Scanner(i);
        String contents = scanner.useDelimiter("\\A").next();
        scanner.close();
        return contents;
    }

    public static File testResFile(String path) {
        return new File(testResUrl((path)).getFile());
    }

    public static URL testResUrl(String path) {
        return TestUtil.class.getResource(path);
    }

    @Test
    public void canReadResStream() throws IOException {
        InputStream i = TestUtil.getTestResStream("/cases/testbase/case-426885.html");
        while (i.read() != -1) {
        }
    }

    @Test
    public void canReadResString() {
        assertEquals(393, TestUtil.getTestResString("/cases/testbase/case-426885.html").length());
    }
}
