/**
 */
#include "io_jokester_tidyj_TidyDoc.h"
#include "tidy.h"

/**
 * adapter: allow libtidy to write to Java OutputStream
 * TODO: can we add a jmpbuf to jump back on jni error?
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
 * adapter: allow libtidy to read from Java InputStream
 * TODO: can we add a jmpbuf to jump back on jni error?
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
    int hadJavaError;
} JniReadParams;

