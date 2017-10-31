#include <cstring>
#include "io_jokester_tidyj_TidyDoc.h"
#include "tidy.h"
#include "jni-util.hpp"
#include "tidyj-io.h"
#include "tidyj-error.h"

/**
 * FIXME:
 * - learn how to cast pointer from/to jlong
 *   (feeling that explicit conversion may not be the right way)
 */

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
        0,
        0,
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

    // FIXME: can we have a buffer that reduce jni call?
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
