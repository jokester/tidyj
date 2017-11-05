#include <jni.h>
#include <cstring>

/**
 * class names for common java exception
 */
extern const char *jNoSuchMethodError, *jOutOfMemoryError, *jUnsupportedOperationException;

/**
 * throw a java exception to env
 * NOTE: *all* memory should be statically allocated
 */
extern void javaThrow(JNIEnv *env, const char* className, const char* message);
