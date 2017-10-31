/**
 * code about tidy options
 */

#include <cstring>
#include "io_jokester_tidyj_TidyDoc.h"
#include "tidy.h"
#include "jni-util.hpp"

/**
  native boolean nativeSetBoolOption(long pTidyDoc, String optName, boolean optValue);
  */
JNIEXPORT jboolean JNICALL Java_io_jokester_tidyj_TidyDoc_nativeSetBoolOption
(JNIEnv *env, jobject jthis, jlong pTidyDoc, jstring _optName, jboolean optValue) {
    assert(pTidyDoc && _optName);

    jboolean succeeded = false;
    TidyDoc tdoc = (TidyDoc) pTidyDoc;
    ctmbstr optName = env->GetStringUTFChars(_optName, NULL);
    // tidyGetOptionByName returns NULL on fail
    // TidyOption objects are in static memory, no need to free
    TidyOption option = tidyGetOptionByName(tdoc, optName);
    env->ReleaseStringUTFChars(_optName, optName);

    if (
            !option
            || tidyOptIsReadOnly(option)
            || (tidyOptGetType(option) != TidyBoolean)
       ) return no;

    TidyOptionId optId = tidyOptGetId(option);

    succeeded = tidyOptSetBool(tdoc, optId, optValue ? yes: no);
    return succeeded;
}

/*
   native boolean nativeSetStringOption(long pTidyDoc, String optName, String optValue);
   */
JNIEXPORT jboolean JNICALL Java_io_jokester_tidyj_TidyDoc_nativeSetStringOption
(JNIEnv *env, jobject jthis, jlong pTidyDoc, jstring _optName, jstring _optValue) {
    assert(pTidyDoc && _optName && _optValue);

    jboolean succeeded = false;
    TidyDoc tdoc = (TidyDoc) pTidyDoc;
    ctmbstr optName = env->GetStringUTFChars(_optName, NULL);
    ctmbstr optValue = env->GetStringUTFChars(_optValue, NULL);
    TidyOptionId optId;
    TidyOption option = tidyGetOptionByName(tdoc, optName);

    if (
            !option
            || tidyOptIsReadOnly(option)
            || (tidyOptGetType(option) != TidyString)
       ) goto fail;

    optId = tidyOptGetId(option);

    succeeded = tidyOptSetValue(tdoc, optId, optValue);

fail:
    if (optName) {
        env->ReleaseStringUTFChars(_optName, optName);
    }
    if (optValue) {
        env->ReleaseStringUTFChars(_optValue, optValue);
    }
    return succeeded;
}


/**
 native boolean nativeSetIntOption(long pTidyDoc, String optName, int optValue);
 */
JNIEXPORT jboolean JNICALL Java_io_jokester_tidyj_TidyDoc_nativeSetIntOption
(JNIEnv *env, jobject jthis, jlong pTidyDoc, jstring _optName, jint optValue) {
    assert(pTidyDoc && _optName);

    jboolean succeeded = false;
    TidyDoc tdoc = (TidyDoc) pTidyDoc;
    ctmbstr optName = env->GetStringUTFChars(_optName, NULL);
    TidyOption option = tidyGetOptionByName(tdoc, optName);
    env->ReleaseStringUTFChars(_optName, optName);

    if (
            !option
            || tidyOptIsReadOnly(option)
            || (tidyOptGetType(option) != TidyInteger)
       ) return false;
    TidyOptionId optId = tidyOptGetId(option);
    succeeded = tidyOptSetInt(tdoc, optId, optValue);
    return succeeded;
}

/**
  private native boolean nativeSetAnyOption(long pTidyDoc, String optName, String optValue);
 */
JNIEXPORT jboolean JNICALL Java_io_jokester_tidyj_TidyDoc_nativeSetAnyOption
(JNIEnv *env, jobject jthis, jlong pTidyDoc, jstring _optName, jstring _optValue) {
    assert(pTidyDoc && _optName && _optValue);

    jboolean succeeded = false;
    TidyDoc tdoc = (TidyDoc) pTidyDoc;
    ctmbstr optName = env->GetStringUTFChars(_optName, NULL);
    ctmbstr optValue = env->GetStringUTFChars(_optValue, NULL);
    TidyOptionId optId;
    TidyOption option = tidyGetOptionByName(tdoc, optName);

    if (
            !option
            || tidyOptIsReadOnly(option)
       ) return false;

    optId = tidyOptGetId(option);

    succeeded = tidyOptSetValue(tdoc, optId, optValue);

fail:
    if (optName) {
        env->ReleaseStringUTFChars(_optName, optName);
    }
    if (optValue) {
        env->ReleaseStringUTFChars(_optValue, optValue);
    }
    return succeeded;
}
