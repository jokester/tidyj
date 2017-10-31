package io.jokester.tidyj;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * TidyDoc: a immutable parsed and cleaned html/xml document
 * <p>
 * (When parse / clean fails, the constructor will throw)
 * <p>
 * Note on methods: most methods are package-local, except the output / DOM APIs
 * they are only meant to be called from inside the package
 * <p>
 * Corresponds to `TidyDoc` type of native tidy-html5 {@see https://github.com/htacg/tidy-html5}
 */
public final class TidyDoc implements Closeable {

    static {
        System.loadLibrary("tidyj");
    }

    /**
     * pointer to memory of TidyDoc
     */
    private final long pTidyDoc;
    /**
     * whether the memory is
     */
    private boolean freed = false;

    /**
     * TidyJ: a parsed document
     *
     * @param maxMem maximum memory to use
     */
    TidyDoc(int maxMem) throws TidyJException.InitError {
        pTidyDoc = this.nativeInit(null);
        if (pTidyDoc == 0)
            throw new TidyJException.InitError("error initializing TidyDoc");
    }

    /**
     * @return had warning(s)
     */
    boolean parse(String docString) throws TidyJException.ParseError {
        if (docString == null)
            throw new NullPointerException("docString cannot be null");
        int parseRet = nativeParseString(pTidyDoc, docString);
        if (parseRet >= 2) {
            throw new TidyJException.ParseError("error parsing string");
        }

        return parseRet != 0;
    }

    /**
     * @return had warning(s)
     */
    boolean parse(InputStream docStream) throws TidyJException.ParseError {
        if (docStream == null)
            throw new NullPointerException("docStream cannot be null");
        int parseRet = nativeParseStream(pTidyDoc, docStream);
        if (parseRet >= 2) {
            throw new TidyJException.ParseError("error parsing stream");
        }

        return parseRet != 0;
    }

    /**
     * @return had warning(s)
     */
    boolean clean() throws TidyJException.ParseError {
        int cleanRet = nativeClean(pTidyDoc);
        if (cleanRet >= 2) {
            throw new TidyJException.ParseError("error cleaning");
        }
        return cleanRet != 0;
    }

    /**
     * Set value of bool option
     *
     * @param optName  tagName of option
     * @param optValue value of option
     * @return succeeded
     */
    boolean setBoolOption(String optName, boolean optValue) {
        return nativeSetBoolOption(pTidyDoc, optName, optValue);
    }

    /**
     * Set value of string option
     *
     * @param optName  tagName of option
     * @param optValue value of option
     * @return succeeded
     */
    boolean setStringOption(String optName, String optValue) {
        return nativeSetStringOption(pTidyDoc, optName, optValue);
    }

    /**
     * Set value of int option
     *
     * @param optName  tagName of option
     * @param optValue value of option
     * @return succeeded
     */
    boolean setIntOption(String optName, int optValue) {
        return nativeSetIntOption(pTidyDoc, optName, optValue);
    }

    /**
     * Set value of any libtidy option (it will be interpreted by underlying libtidy)
     *
     * @param optName            tagName of option
     * @param uninterpretedValue value of option
     * @return succeeded
     */
    boolean setAnyOption(String optName, String uninterpretedValue) {
        return nativeSetAnyOption(pTidyDoc, optName, uninterpretedValue);
    }

    /**
     * Write to and close the stream
     *
     * @param stream output stream
     * @return num of bytes written
     */
    public int save(OutputStream stream) throws IOException {
        return save(stream, true);
    }

    public String saveString() throws IOException {
        return saveString("utf-8");
    }

    synchronized
    public String saveString(String charsetName) throws IOException {

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        int numBytes = save(o, true);

        return o.toString(charsetName);
    }

    /**
     * Write to stream
     *
     * @param stream           output stream
     * @param closeAfterFinish whether to close the stream
     * @return num of bytes written
     */
    synchronized
    public int save(OutputStream stream, boolean closeAfterFinish) throws IOException {
        assertNotFreed();
        if (stream == null)
            throw new NullPointerException("OutputStream cannot be null");
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

    /**
     * Destroy underlying libtidy `TidyDoc` object, and return memory to native heap.
     * <p>
     * After a call to close() or free(), most API of this instance would
     * throw {@link TidyJException.AlreadyFreed}
     * <p>
     * close() is idempotent: calling it more than once have no effect
     */
    synchronized
    public void close() {
        if (!freed)
            free();
    }

    /**
     * Destroy underlying libtidy `TidyDoc` object, and return memory to native heap
     * <p>
     * After a call to close() or free(), most API of this instance would
     * throw {@link TidyJException.AlreadyFreed}
     * <p>
     * calling free() when TidyDoc is already freed would throw {@link TidyJException.AlreadyFreed}
     */
    synchronized
    public void free() {
        assertNotFreed();
        nativeFree(pTidyDoc);
        freed = true;
    }

    synchronized
    private void assertNotFreed() {
        if (freed)
            throw new TidyJException.AlreadyFreed("already freed");
    }

    /**
     * @param q a DomQuery instance
     * @return opaque handles to matched nodes (internally, a TidyNode pointer)
     */
    synchronized long[] queryDom(DomQuery q) {
        assertNotFreed();
        return null;
        // TODO
    }

    /**
     * Copy pnodes from
     *
     * @param pNodes opaque handlers returned from {@see queryDom}
     * @return DomNode objects that completely resides in java heap
     */
    synchronized DomNode[] pullDom(long[] pNodes) {
        assertNotFreed();
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
    private native int nativeParseString(long pTidyDoc, String htmlString);

    /**
     * parse document
     *
     * @return 2 on error, 1 on warning, 0 on clean
     */
    private native int nativeParseStream(long pTidyDoc, InputStream htmlStream);

    private native int nativeClean(long pTidyDoc);

    private native boolean nativeSetBoolOption(long pTidyDoc, String optName, boolean optValue);

    private native boolean nativeSetStringOption(long pTidyDoc, String optName, String optValue);

    private native boolean nativeSetIntOption(long pTidyDoc, String optName, int optValue);

    private native boolean nativeSetAnyOption(long pTidyDoc, String optName, String optValue);

    /**
     * write document to stream
     *
     * @param pTidyDoc
     * @param stream
     * @return num of bytes written
     */
    private native int nativeWriteStream(long pTidyDoc, OutputStream stream);
}
