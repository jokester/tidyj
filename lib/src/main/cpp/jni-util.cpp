#include <jni.h>
#include "jni-util.hpp"

const char *jNoSuchMethodError = "java/lang/NoSuchMethodError";
const char *jOutOfMemoryError = "java/lang/OutOfMemoryError";
const char *jUnsupportedOperationException = "java/lang/UnsupportedOperationException";

static void throwNoClassDefError(JNIEnv *env, const char* className) {
    const char* jNoClassDefFoundError = "java/lang/NoClassDefFoundError";
    jclass exClass = env->FindClass(jNoClassDefFoundError);
    // skip null check and make it more severe, in case NoClassDefFoundError doesnt exist
    env->ThrowNew(exClass, className);
}

void javaThrow(JNIEnv *env, const char* className, const char* message) {
    jclass exClass = env->FindClass(className);

    if (exClass == NULL) {
        throwNoClassDefError(env, className);
        return;
    };
    env->ThrowNew(exClass, message);
}
