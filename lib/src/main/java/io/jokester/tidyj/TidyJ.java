package io.jokester.tidyj;

import java.io.InputStream;

/**
 * TidyJ: static factory methods to do parse + clean with libtidy
 *
 * Before memory management is implemented,
 * it is advised to use try-with to ensure TidyJ instance get closed.
 */
public final class TidyJ {

    /**
     * default options
     */
    private static final TidyOptionSet defaultOptions = new TidyOptionSet()
            .addIntOption("show-errors", 0)
            .addBoolOption("show-info", false)
            .addBoolOption("show-warnings", false)
            .addBoolOption("drop-empty-elements", false)
            .addBoolOption("drop-empty-paras", false)
            .addBoolOption("quiet", true);

    private TidyJ() {
    }

    /**
     * factory methods for parsing and tidying String
     * <p>
     * when factory method returns, the (there may be warnings)
     *
     * @return TidyJ instance if the parsing succeeded
     * @throws TidyJException if error raised in parsing / cleaning
     */
    public static TidyDoc parseString(String docString) throws TidyJException {
        return parseString(docString, /* options*/ null);
    }

    public static TidyDoc parseStream(InputStream docStream) throws TidyJException {
        return parseStream(docStream, /* options*/ null);
    }

    public static TidyDoc parseString(String docString, TidyOptionSet options) throws TidyJException.ParseError {
        int maxMem = -1;
        TidyDoc doc = new TidyDoc(maxMem);

        try {
            defaultOptions.apply(doc);
            if (options != null) options.apply(doc);
            doc.parse(docString);
            doc.clean();
            return doc;
        } catch (Throwable e) {
            // when error occurs, ensure doc gets closed
            doc.close();
            throw e;
        }
    }

    public static TidyDoc parseStream(InputStream docStream, TidyOptionSet options)
            throws TidyJException {
        int maxMem = -1;
        TidyDoc doc = new TidyDoc(maxMem);
        try {
            defaultOptions.apply(doc);
            if (options != null) options.apply(doc);
            doc.parse(docStream);
            doc.clean();
            return doc;
        } catch (Throwable e) {
            // when error occurs, ensure doc gets closed
            doc.close();
            throw e;
        }
    }
}
