package io.jokester.tidyj;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("WeakerAccess")
public class TestOptions {

    // a perfect html5
    public static final String TestHTML1 = "<!DOCTYPE html> <html> <head> <title>test</title> </head> <body> <p>Body</p> </body> </html>";
    // a html5 without </p> tag
    public static final String TestHTML2 = "<!DOCTYPE html> <html> <head> <title>test</title> <body> Body</p> </html>";

    @Test
    public void createInstance1() throws Exception {
        try (TidyDoc t = new TidyDoc(0)) {
            boolean hadWarning = t.parse(TestHTML1);
            assertEquals(false, hadWarning);
        }
    }

    @Test
    public void createInstance2() throws Exception {
        try (TidyDoc t = new TidyDoc(0)) {

            boolean hadWarning = t.parse(TestHTML2);
            assertEquals(true, hadWarning);
        }
    }

    @Test(expected = TidyJException.AlreadyFreed.class)
    public void freeTwiceThrowsAlreadyFreed() throws Exception {
        TidyDoc t_;
        try (TidyDoc t = new TidyDoc(0)) {

            t_ = t;
            /* close() calls free() internally */
        }
        t_.saveString();
    }

    @Test
    public void outputBuffer() throws Exception {
        try (TidyDoc t = new TidyDoc(0)) {

            t.parse(TestHTML2);

            ByteArrayOutputStream o = new ByteArrayOutputStream();
            int numBytes = t.save(o);

            String s = o.toString();

            String expected =
                    "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "<title>test</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "Body\n" +
                            // dropped empty element "<p></p>\n" +
                            "</body>\n" +
                            "</html>\n";
            assertEquals(expected, s);
            assertEquals(expected.length(), numBytes);
        }
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setNonexistBoolOption() {
        TidyOptionSet o = new TidyOptionSet().addBoolOption("v", false);
        try (TidyDoc t = new TidyDoc(0)) {
            o.apply(t);
        }
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setNonBoolOption() {
        TidyOptionSet o = new TidyOptionSet().addBoolOption(/* a string option */ "alt-text", false);
        try (TidyDoc t = new TidyDoc(0)) {
            o.apply(t);
        }
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setReadonlyBoolOption() {
        TidyOptionSet o = new TidyOptionSet().addBoolOption(/* a string */ "unknown!", false);
        try (TidyDoc t = new TidyDoc(0)) {
            o.apply(t);
        }
    }

    @Test
    public void setOptions() {
        TidyOptionSet o = new TidyOptionSet()
                .addBoolOption("input-xml", false)
                .addBoolOption("quiet", true)
                .addStringOption("alt-text", "ALT")
                .addIntOption("show-errors", 5)
                .addAnyOption("indent", "yes")
                .addAnyOption("indent", "auto")
                .addAnyOption("indent", "0")
                .addAnyOption("new-empty-tags", "hey1");
        try (TidyDoc t = new TidyDoc(0)) {
            o.apply(t);
        }
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setNonexistStringOption() {
        TidyOptionSet o = new TidyOptionSet()
                .addStringOption("blt-text", "hey");
        try (TidyDoc t = new TidyDoc(0)) {
            o.apply(t);
        }
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setNonexistIntOption() {
        TidyOptionSet o = new TidyOptionSet()
                .addIntOption("show-errrrors", 5);
        try (TidyDoc t = new TidyDoc(0)) {
            o.apply(t);
        }

    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setNonIntOption() {
        TidyOptionSet o = new TidyOptionSet()
                .addIntOption("show-info", 5);
        try (TidyDoc t = new TidyDoc(0)) {
            o.apply(t);
        }
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setNonexistAnyOption() {
        TidyOptionSet o = new TidyOptionSet()
                .addAnyOption("show-errrrors", "5");
        try (TidyDoc t = new TidyDoc(0)) {
            o.apply(t);
        }
    }

    @Test(expected = TidyJException.IllegalOption.class)
    public void setIncorrectAnyOption() {
        TidyOptionSet o = new TidyOptionSet()
                .addAnyOption("new-empty-tags", "");
        try (TidyDoc t = new TidyDoc(0)) {
            o.apply(t);
        }

    }

}
