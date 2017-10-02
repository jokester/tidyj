package io.jokester.tidyj;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;

/**
 * TidyHTML5: a html tidy / parser
 * <p>
 * backened by tidy-html5 {@see https://github.com/htacg/tidy-html5}
 *
 * @author Wang Guan
 *         <p>
 *         TODO: v0.1 basic tidy and dom read-only access
 *         TODO: implement memory restriction (custom allocator / manual free from java)
 */
public final class TidyJ implements Closeable {

    /**
     * factory method to parse string
     *
     * @return TidyJ instance if the parsing succeeded
     */
    public static TidyJ parseString(String htmlString) throws TidyJException {
        return parseString(htmlString, /* 8M */ 1 << 23, null);
    }

    private static TidyJ parseString(String htmlString, int maxMem, TidyOptionSet options)
            throws TidyJException {
        return new TidyJ(maxMem, options);
    }

    private final ByteBuffer nativeRegion;
    private final long pTidyDoc;
    private boolean freed = false;

    private static final TidyOptionSet defaultOptions = new TidyOptionSet()
            .addIntOption("show-errors", 0)
            .addBoolOption("show-info", false)
            .addBoolOption("drop-empty-elements", false)
            .addBoolOption("show-warnings", false);

    /**
     * TidyJ: a parsed document
     *
     * @param maxMem  maximum memory to use
     * @param options options
     */
    TidyJ(int maxMem, TidyOptionSet options) throws TidyJException {
        nativeRegion = null; // ByteBuffer.allocateDirect(maxMem);
        pTidyDoc = this.nativeInit(nativeRegion);
        if (pTidyDoc == 0)
            throw new TidyJException.InitError("error initializing ");
        defaultOptions.apply(this);
        if (options != null)
            options.apply(this);
    }

    /**
     * @return had warning(s)
     */
    boolean parseHTML(String htmlString) throws TidyJException.ParseError {
        int parseRet = nativeParse(pTidyDoc, htmlString);
        if (parseRet >= 2) {
            throw new TidyJException.ParseError("ParseError");
        }

        return parseRet != 0;
    }

    /**
     * Set value of bool option
     * @param optName tagName of option
     * @param optValue value of option
     * @return succeeded
     */
    boolean setBoolOption(String optName, boolean optValue) {
        return nativeSetBoolOption(pTidyDoc, optName, optValue);
    }

    /**
     * Set value of string option
     * @param optName tagName of option
     * @param optValue value of option
     * @return succeeded
     */
    boolean setStringOption(String optName, String optValue) {
        return nativeSetStringOption(pTidyDoc, optName, optValue);
    }

    /**
     * Set value of int option
     * @param optName tagName of option
     * @param optValue value of option
     * @return succeeded
     */
    boolean setIntOption(String optName, int optValue) {
        return nativeSetIntOption(pTidyDoc, optName, optValue);
    }

    /**
     * Set value of any libtidy option (it will be interpreted by underlying libtidy)
     * @param optName tagName of option
     * @param uninterpretedValue value of option
     * @return succeeded
     */
    boolean setAnyOption(String optName, String uninterpretedValue) {
        return nativeSetAnyOption(pTidyDoc, optName, uninterpretedValue);
    }

    /**
     * Write to stream and close the stream
     * @param stream output stream
     * @return bytes written
     */
    public int save(OutputStream stream) throws IOException {
        return save(stream, true);
    }

    /**
     * Write to stream
     * @param stream output stream
     * @param closeAfterFinish whether to close the stream
     * @return num of bytes written
     */
    synchronized
    public int save(OutputStream stream, boolean closeAfterFinish) throws IOException {
        ensureNotFreed();

        try {
            int bytesWritten = nativeWriteStream(pTidyDoc, stream);
            if (bytesWritten < 0) {
                throw new IOException(String.format("nativeWriteStream returned %d", bytesWritten));
            }
            stream.flush();
            return bytesWritten;

        } finally {
            if (closeAfterFinish)
                stream.close();

        }
    }

    synchronized
    public void close() {
        if (!freed)
            free();
    }

    synchronized
    public void free() {
        ensureNotFreed();
        nativeFree(pTidyDoc);
        freed = true;
    }

    synchronized
    private void ensureNotFreed() {
        if (freed)
            throw new TidyJException.AlreadyFreed("already freed");
    }

    /**
     *
     * @param q a DomQuery instance
     * @return opaque handles to matched nodes (internally, a TidyNode pointer)
     */
    synchronized long[] queryDom(DomQuery q) {
        ensureNotFreed();
        return null;
        // TODO
    }

    /**
     * Copy pnodes from
     * @param pNodes opaque handlers returned from {@see queryDom}
     * @return DomNode objects that completely resides in java heap
     */
    synchronized DomNode[] pullDom(long[] pNodes) {
        ensureNotFreed();
        return null;
        // TODO: how to write this in native?
    }

    /**
     * initialize TidyDoc object
     * TODO: support memory allocator
     *
     * @return 0 if failed to create object
     * otherwise: a opaque handle (internally, a pointer to native heap)
     */
    private native long nativeInit(ByteBuffer nativeMemory);

    private native void nativeFree(long pTidyDoc);

    /**
     * parse document
     *
     * @return 2 on error, 1 on warning, 0 on clean
     */
    private native int nativeParse(long pTidyDoc, String htmlString);

    /**
     * TODO: support stream input
     * libtidy internally have a TidyInputSource for streamed input:
     * - get a byte
     * - put back a byte
     * - see if eof is reached
     * <p>
     * Thus the PushbackInputStream class is used here
     */
    private native int nativeParse(PushbackInputStream htmlStream);

    private native boolean nativeSetBoolOption(long pTidyDoc, String optName, boolean optValue);
    private native boolean nativeSetStringOption(long pTidyDoc, String optName, String optValue);
    private native boolean nativeSetIntOption(long pTidyDoc, String optName, int optValue);
    private native boolean nativeSetAnyOption(long pTidyDoc, String optName, String optValue);

    /**
     * write document to stream
     * @param pTidyDoc
     * @param stream
     * @return num of bytes written
     */
    private native int nativeWriteStream(long pTidyDoc, OutputStream stream);

    static {
        System.loadLibrary("tidyj");
    }

}
