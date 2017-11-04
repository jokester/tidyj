package io.jokester.tidyj;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class TestParsing {

    /**
     * default options taken from
     * tidy-html5-tests/cases/testbase/config_default.conf
     */
    public static final TidyOptionSet defaultOptions = new TidyOptionSet()
            .addAnyOption("indent", "auto")
            .addAnyOption("char-encoding", "latin1")
            .addBoolOption("tidy-mark", false)
            .addBoolOption("clean", true)
            .addBoolOption("logical-emphasis", true)
            .addBoolOption("indent-attributes", true);

    /**
     * 5 warnings / 1 error
     */
    public static String TestCase_HTML_1002509 = "/cases/testbase/case-1002509.html";

    /**
     * 0 warning / 0 error
     */
    public static String TestCase_HTML_1003361 = "/cases/testbase/case-1003361.html";
    public static String Expected_HTML_1003361 = "/cases/testbase-expects/case-1003361.html";

    /**
     * 1 warning / 0 error
     */
    public static String TestCase_HTML_427830 = "/cases/testbase/case-427830.html";
    public static String Expected_HTML_427830 = "/cases/testbase-expects/case-427830.html";

    @Test
    public void testParseString() throws TidyJException, IOException {
        assertCleanResult(
                TestUtil.getTestResString(Expected_HTML_427830),
                TestUtil.getTestResString(TestCase_HTML_427830),
                defaultOptions);

        assertCleanResult(
                TestUtil.getTestResString(Expected_HTML_1003361),
                TestUtil.getTestResString(TestCase_HTML_1003361),
                defaultOptions);
    }

    @Test
    public void testParseStream() throws IOException, TidyJException {
        assertCleanResult(
                TestUtil.getTestResString(Expected_HTML_427830),
                TestUtil.getTestResStream(TestCase_HTML_427830),
                defaultOptions);

        assertCleanResult(
                TestUtil.getTestResString(Expected_HTML_1003361),
                TestUtil.getTestResStream(TestCase_HTML_1003361),
                defaultOptions);
    }

    @Test(expected = IOError.class)
    public void badOutputStreamThrowsToJava() throws TidyJException, IOException {
        String s = TestUtil.getTestResString(TestCase_HTML_427830);

        OutputStream badStream = new ByteArrayOutputStream() {
            private int written = 0;
            @Override
            public synchronized void write(int b) {
                if (++written > 10)
                    throw new IOError(new Error("test error"));
                super.write(b);
            }
        };

        try (TidyDoc doc = TidyJ.parseString(s)) {
            doc.save(badStream);
        }
    }

    @Test(expected = IOException.class, timeout = 1000L)
    public void badInputStreamThrowsToJava() throws TidyJException, IOException {
        final String s = TestUtil.getTestResString(TestCase_HTML_427830);

        InputStream badStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException();
            }
        };

        try (TidyDoc doc = TidyJ.parseStream(badStream)) {
        }
    }

    private void assertCleanResult(String expected, String input, TidyOptionSet options) throws TidyJException, IOException {
        try (TidyDoc doc = TidyJ.parseString(input, options)) {
            assertEquals(
                    expected,
                    doc.saveString());
        }
    }

    private void assertCleanResult(String expected, InputStream input, TidyOptionSet options) throws TidyJException, IOException {
        try (TidyDoc doc = TidyJ.parseStream(input, options)) {
            assertEquals(
                    expected,
                    doc.saveString());
        }
    }
}
