/**
 * code about tidy parse/clean
 */

#include <cstring>
#include "io_jokester_tidyj_TidyJ.h"
#include "tidy.h"
#include "jni-util.hpp"

/**
  native int nativeParse(long pTidyDoc, String htmlString);
 @return 2 for error, 1 for warning, 0 for clean
*/
JNIEXPORT jint JNICALL Java_io_jokester_tidyj_TidyJ_nativeParse__JLjava_lang_String_2
(JNIEnv * env, jobject jthis, jlong pTidyDoc, jstring htmlString) {
    assert(pTidyDoc);
    TidyDoc tdoc = (TidyDoc) pTidyDoc;
    ctmbstr chars =  env->GetStringUTFChars(htmlString, NULL);

    int parseResult = tidyParseString(tdoc, chars);

    env->ReleaseStringUTFChars(htmlString, chars);
    return parseResult;
}

/**
  native int nativeParse(PushbackInputStream htmlStream);
TODO: implement
*/
JNIEXPORT jint JNICALL Java_io_jokester_tidyj_TidyJ_nativeParse__Ljava_io_PushbackInputStream_2
(JNIEnv * env, jobject jthis, jstring xmlString) {
    javaThrow(env, jUnsupportedOperationException, "native int nativeParse(PushbackInputStream htmlStream);");
    return 1;
}

/**
 * bound params to a jni "write" call
 */
typedef struct {
    JNIEnv *env;
    jmethodID method;
    jobject obj;
    int *numWritten;
} JniWriteParams;

extern "C"
void jniwrite(void* sinkData, byte bt) {
    JniWriteParams *m = static_cast<JniWriteParams*>(sinkData);
    JNIEnv *env = m->env;
    env->CallVoidMethod(m->obj, m->method, bt);
    ++(*m->numWritten);
}

extern "C"
void writeNop(void *sinkData, byte bt) {
}

/**
  native int nativeWriteStream(long pTidyDoc, OutputStream stream);
*/
JNIEXPORT jint JNICALL Java_io_jokester_tidyj_TidyJ_nativeWriteStream
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

    int numWritten = 0;

    TidyOutputSink sink;
    JniWriteParams m;
    m.env = env;
    m.method = writeByte;
    m.obj= stream;
    m.numWritten = &numWritten;

    Bool inited = tidyInitSink(
            &sink,
            &m,
            jniwrite);

    if (!inited) {
        return -2;
    }

    if (!tidySaveSink(tdoc, &sink)) {
        return -3;
    }

    return numWritten;
}
