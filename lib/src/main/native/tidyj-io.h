/**
 */
#include "io_jokester_tidyj_TidyDoc.h"
#include "tidy.h"

/**
 * adapter struct to retrieve output from libtidy
 * -
 */
typedef struct {
    JNIEnv *env;
    /** OutputStream object */
    jobject stream;
    /** void write(int);" */
    jmethodID writeMethod;
    int bytesWritten;
} JniWriteParams;

static const size_t InputBufferSize = 16;

/**
 * adapter struct to provide bytes to libtidy
 */
typedef struct {
    JNIEnv *env;
    /** InputStream */
    jobject stream;
    /** int read() */
    jmethodID readMethod;
    uint buffer[InputBufferSize];
    /** how many bytes are unget()ed */
    size_t bufferedSize;
    int finished;
} JniReadParams;

#ifdef __cplusplus
extern "C" {
#endif

inline void jniwrite(void *_writeParams, byte bt) {
    JniWriteParams *m = (JniWriteParams*) _writeParams;
    JNIEnv *env = m->env;
    env->CallVoidMethod(m->stream, m->writeMethod, bt);
    ++(m->bytesWritten);
}

inline int jnigetByte(void *sourceData) {
    JniReadParams *m = (JniReadParams*) sourceData;
    JNIEnv *env = m->env;

    if (m->bufferedSize > 0) {
        return m->buffer[-- (m->bufferedSize)];
    }

    jint b = env->CallIntMethod(m->stream, m->readMethod);
    return (b < 0) ? EOF : b;
}

inline void jniungetByte(void *sourceData, byte bt) {
    JniReadParams *m = (JniReadParams*) sourceData;

    // should not see buffer overflow
    if (m->bufferedSize >= InputBufferSize) exit(5);
    m->buffer[ (m->bufferedSize)++ ] = bt;
}

inline Bool jnieof(void *sourceData) {
    JniReadParams *m = (JniReadParams*) sourceData;

    if (m->bufferedSize > 0) {
        return no;
    }

    JNIEnv *env = m->env;
    jint bt = env->CallIntMethod(m->stream, m->readMethod);
    if (bt < 0) {
        return yes;
    } else {
        // put into buffer
        // should not see buffer overflow
        if (m->bufferedSize >= InputBufferSize) exit(6);
        m->buffer[ (m->bufferedSize)++ ] = bt;

        return no;
    }
}

#ifdef __cplusplus
}
#endif
