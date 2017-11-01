#include <cstring>
#include "io_jokester_tidyj_TidyDoc.h"
#include "tidy.h"
#include "jni-util.hpp"
#include "tidyj-io.h"
#include "tidyj-error.h"

#define TIDYJ_DEBUG 0

static inline void jniwrite(void *_writeParams, byte bt);
static inline int jnigetByte(void *sourceData);
static inline void jniungetByte(void *sourceData, byte bt);
static inline Bool jnieof(void *sourceData);

/**
 * native int nativeParseString(long pTidyDoc, String htmlString);
 *
 * Parse and clean a string
 * @return 2 for error, 1 for warning, 0 for clean
*/
JNIEXPORT jint JNICALL Java_io_jokester_tidyj_TidyDoc_nativeParseString
(JNIEnv * env, jobject jthis, jlong pTidyDoc, jstring docString) {
    assert(pTidyDoc);
    TidyDoc tdoc = (TidyDoc) pTidyDoc;
    ctmbstr chars = env->GetStringUTFChars(docString, NULL);

    /* in tidylib.c:
     * tidyParseString calls DocParseStream(), which in turn returns tidyDocStatus()
     */
    int parseResult = tidyParseString(tdoc, chars);
    env->ReleaseStringUTFChars(docString, chars);

    return parseResult;
}

/**
  private native int nativeParseStream(long pTidyDoc, PushbackInputStream htmlStream);
 * @return 3 for I/O error, 2 for parsing error, 1 for warning, 0 for clean
*/
JNIEXPORT jint JNICALL Java_io_jokester_tidyj_TidyDoc_nativeParseStream
(JNIEnv *env, jobject jthis, jlong pTidyDoc, jobject stream) {
    assert(pTidyDoc);
    TidyDoc tdoc = (TidyDoc) pTidyDoc;

    jclass streamClass = env->GetObjectClass(stream);
    jmethodID readByte = env->GetMethodID(streamClass,
            "read", "()I");
    if (!readByte) {
        javaThrow(env, jNoSuchMethodError, "int read();");
        return 3;
    }

    JniReadParams m = {
        env,
        stream,
        readByte,
        { 0 },
        /* finished */ 0,
        /* hadJavaError */ 0,
    };

    TidyInputSource src;
    if (!tidyInitSource(&src, &m,
                jnigetByte,
                jniungetByte,
                jnieof)) {
        return 4;
    }

    int parseResult = tidyParseSource(tdoc, &src);

    return parseResult;
}

/**
 * private native int nativeClean(long pTidyDoc);
 */
JNIEXPORT jint JNICALL Java_io_jokester_tidyj_TidyDoc_nativeClean
(JNIEnv *env, jobject jthis, jlong pTidyDoc) {
    assert(pTidyDoc);
    TidyDoc tdoc = (TidyDoc) pTidyDoc;
    int cleanResult = tidyCleanAndRepair(tdoc);
    return cleanResult;
}

/**
  native int nativeWriteStream(long pTidyDoc, OutputStream stream);
  */
JNIEXPORT jint JNICALL Java_io_jokester_tidyj_TidyDoc_nativeWriteStream
(JNIEnv *env, jobject jthis, jlong pTidyDoc, jobject stream) {
    assert(pTidyDoc && stream);
    TidyDoc tdoc = (TidyDoc) pTidyDoc;

    // FIXME: can we have a buffer that reduce jni calls?
    jclass streamClass = env->GetObjectClass(stream);
    jmethodID writeByte = env->GetMethodID(streamClass,
            "write", "(I)V");
    if (!writeByte) {
        javaThrow(env, jNoSuchMethodError, "void write(int);");
        return -1;
    }

    JniWriteParams m = {
        env,
        stream,
        writeByte,
        0,
    };

    TidyOutputSink sink;
    Bool inited = tidyInitSink(
            &sink,
            &m,
            jniwrite);

    if (!inited) {
        return -2;
    }

    /** tidySaveSink() returns tidyDocStatus() */
    if (tidySaveSink(tdoc, &sink) > 1) {
        return -3;
    }

    return m.bytesWritten;
}

static inline void jniwrite(void *_writeParams, byte bt) {
#if TIDYJ_IO_DEBUG
    fputs("jniwrite called\n", stderr);
#endif
    JniWriteParams *m = (JniWriteParams*) _writeParams;
    JNIEnv *env = m->env;
    env->CallVoidMethod(m->stream, m->writeMethod, bt);
    ++(m->bytesWritten);
}

static inline int jnigetByte(void *sourceData) {
#if TIDYJ_IO_DEBUG
    fputs("jnigetByte called\n", stderr);
#endif
    JniReadParams *m = (JniReadParams*) sourceData;
    if (m->hadJavaError) return EOF;
    JNIEnv *env = m->env;

    if (m->bufferedSize > 0) {
        return m->buffer[-- (m->bufferedSize)];
    }

    jint b = env->CallIntMethod(m->stream, m->readMethod);

    /* when Java error occured, return EOF to stop parsing (this allows libtidy to clean and return) */
    jthrowable maybeError = env->ExceptionOccurred();
    if(maybeError) {
        m->hadJavaError = true;
        return EOF;
    }
    return (b < 0) ? EOF : b;
}

static inline void jniungetByte(void *sourceData, byte bt) {
#if TIDYJ_IO_DEBUG
    fputs("jniungetByte called\n", stderr);
#endif
    JniReadParams *m = (JniReadParams*) sourceData;

    /**
     * should not see buffer overflow:
     * ungetByte is only called during detecting encode (as of libtidy 5.4)
     */
    if (m->bufferedSize >= InputBufferSize) exit(5);
    m->buffer[ (m->bufferedSize)++ ] = bt;
}

static inline Bool jnieof(void *sourceData) {
#if TIDYJ_IO_DEBUG
    fputs("jnieof called\n", stderr);
#endif
    JniReadParams *m = (JniReadParams*) sourceData;

    if (m->hadJavaError) return yes;
    if (m->bufferedSize > 0) return no;

    JNIEnv *env = m->env;
    // lookahead one byte
    jint bt = env->CallIntMethod(m->stream, m->readMethod);

    jthrowable maybeError = env->ExceptionOccurred();
    if(maybeError) {
        // java exception
        m->hadJavaError = yes;
        return yes;
    } else if (bt < 0) {
        // eof
        return yes;
    } else {
        // put into buffer
        // should not see buffer overflow
        if (m->bufferedSize >= InputBufferSize) exit(6);
        m->buffer[ (m->bufferedSize)++ ] = bt;

        return no;
    }
}
