package io.jokester.tidyj;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("WeakerAccess")
public class TestTidyJ {

    // a perfect html5
    public static final String TestHTML1 = "<!DOCTYPE html> <html> <head> <title>test</title> </head> <body> <p>Body</p> </body> </html>";
    // a html5 without </p> tag
    public static final String TestHTML2 = "<!DOCTYPE html> <html> <head> <title>test</title> <body> Body</p> </html>";

    @Test
    public void test1() {
        assertEquals(2, 1 + 1);
    }

    @Test
    public void createInstance1() throws Exception {
        TidyJ t = new TidyJ(0, null);
        boolean hadWarning = t.parseHTML(TestHTML1);
        assertEquals(false, hadWarning);
        t.free();
    }

    @Test
    public void createInstance2() throws Exception {
        TidyJ t = new TidyJ(0, null);
        boolean hadWarning = t.parseHTML(TestHTML2);
        assertEquals(true, hadWarning);
        t.free();
    }

    @Test(expected = TidyJException.AlreadyFreed.class)
    public void freeTwice() throws Exception {
        TidyJ t = new TidyJ(0, null);
        t.free();
        t.free();
    }

    @Test
    public void outputBuffer() throws Exception {
        TidyJ t = new TidyJ(0, null);
        boolean hadWarning = t.parseHTML(TestHTML2);


        ByteArrayOutputStream o = new ByteArrayOutputStream();
        t.save(o);

        String s = o.toString();

        assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>test</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "Body\n" +
                "<p></p>\n" +
                "</body>\n" +
                "</html>\n", s);
        t.free();
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setNonexistBoolOption()
            throws Throwable {
        TidyOptionSet o = new TidyOptionSet().addBoolOption("v", false);
        TidyJ t = new TidyJ(0, o);
        t.free();
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setNonBoolOption()
            throws Throwable {
        TidyOptionSet o = new TidyOptionSet().addBoolOption(/* a string */ "alt-text", false);
        TidyJ t = new TidyJ(0, o);
        t.free();
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setReadonlyBoolOption()
            throws Throwable {
        TidyOptionSet o = new TidyOptionSet().addBoolOption(/* a string */ "unknown!", false);
        TidyJ t = new TidyJ(0, o);
        t.free();
    }

    @Test
    public void setOptions()
            throws Throwable {
        TidyOptionSet o = new TidyOptionSet()
                .addBoolOption("input-xml", false)
                .addBoolOption("quiet", true)
                .addStringOption("alt-text", "ALT")
                .addIntOption("show-errors", 5)
                .addAnyOption("indent", "yes")
                .addAnyOption("indent", "auto")
                .addAnyOption("indent", "0")
                .addAnyOption("new-empty-tags", "hey1");

        TidyJ t = new TidyJ(0, o);
        t.free();
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setNonexistStringOption() throws TidyJException {
        TidyOptionSet o = new TidyOptionSet()
                .addStringOption("blt-text", "hey");
        TidyJ t = new TidyJ(0, o);
        t.free();
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setNonexistIntOption() throws TidyJException {
        TidyOptionSet o = new TidyOptionSet()
                .addIntOption("show-errrrors", 5);
        TidyJ t = new TidyJ(0, o);
        t.free();
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setNonIntOption() throws TidyJException {
        TidyOptionSet o = new TidyOptionSet()
                .addIntOption("show-info", 5);
        TidyJ t = new TidyJ(0, o);
        t.free();
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setNonexistAnyOption() throws TidyJException {
        TidyOptionSet o = new TidyOptionSet()
                .addAnyOption("show-errrrors", "5");
        TidyJ t = new TidyJ(0, o);
        t.free();
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setIncorrectAnyOption() throws TidyJException {
        TidyOptionSet o = new TidyOptionSet()
                .addAnyOption("new-empty-tags", "");
        TidyJ t = new TidyJ(0, o);
        t.free();
    }

}
