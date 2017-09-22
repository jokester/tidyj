#include <jni.h>
#include <cstring>
#include "io_jokester_tidyj_TidyHTML5.h"
#include "tidy.h"

/**
 *
 */
JNIEXPORT jboolean JNICALL Java_io_jokester_tidyj_TidyHTML5_initLibTidy
(JNIEnv * env, jobject jthis) {
    TidyDoc tdoc = NULL;
    int intStatus = 0;
    Bool boolStatus = no;

    tdoc = tidyCreate();
    if (!tdoc) goto fail;

    boolStatus = tidyOptSetBool(tdoc, TidyDropEmptyElems, no);
    if (!boolStatus) goto fail;







    return JNI_TRUE;
fail:
    return JNI_FALSE;
}

JNIEXPORT jint JNICALL Java_io_jokester_tidyj_TidyHTML5_nativeParseString
(JNIEnv *env, jobject jthis, jstring jstr) {
    /* a simplest c++ example to compute strlen(jstr) */
    const char *str = env->GetStringUTFChars(jstr, 0);
    const int l = strlen(str);
    return l;
}

