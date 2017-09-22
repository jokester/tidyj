package io.jokester.tidyj;

import java.nio.ByteBuffer;

/**
 * TidyHTML5: a html tidy / parser
 * <p>
 * backened by tidy-html5 {@see https://github.com/htacg/tidy-html5}
 *
 * @author Wang Guan
 * <p>
 * TODO: v0.1 basic tidy and dom read-only access
 * TODO: implement memory restriction (custom allocator / manual free from java)
 */
public final class TidyHTML5 {

    public static TidyHTML5 parseString(String htmlString) {
        return parseString(htmlString, /* 8M */ 1 << 23);
    }

    private static TidyHTML5 parseString(String htmlString, int maxMem) {
        return new TidyHTML5(maxMem);
    }

    private final ByteBuffer nativeRegion;

    /**
     * @param maxMem maximum
     */
    TidyHTML5(int maxMem) {
        this.nativeRegion = ByteBuffer.allocateDirect(maxMem);
        boolean inited = this.initLibTidy();
        if (!inited)
            throw new Error("error initlaizeing ");
    }

    native boolean initLibTidy();

    native int nativeParseString(String htmlString);

    static {
        System.loadLibrary("tidyj");
    }
}
