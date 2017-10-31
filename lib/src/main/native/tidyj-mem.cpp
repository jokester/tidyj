/**
 * memory related: init / free
 */
#include <cstring>
#include "io_jokester_tidyj_TidyDoc.h"
#include "tidy.h"
#include "jni-util.hpp"
#include "tidyj-mem.hpp"
#include "tidyj-error.h"

/**
 * native long nativeInit();
 *
 * TODO:
 * - custom memory allocator, using direct ByteBuffer
 */
JNIEXPORT jlong JNICALL Java_io_jokester_tidyj_TidyDoc_nativeInit
(JNIEnv * env, jobject jthis, jobject nativeBuffer) {
    TidyDoc tdoc = NULL;

    if (nativeBuffer) {
        /** NOT supported */
        goto fail;
    }

    tdoc = tidyCreate();

    if (!tdoc) {
        goto fail;
    }

    // TODO: does cpp have a better cast for this?
    return (jlong) tdoc;

fail:
    if (tdoc) {
        tidyRelease(tdoc);
    }
    return 0;
}

/**
 * native void nativeFree(long pTidyDoc);
 */
JNIEXPORT void JNICALL Java_io_jokester_tidyj_TidyDoc_nativeFree
(JNIEnv *env, jobject jthis, jlong pTidyDoc) {
    TidyDoc tdoc = (TidyDoc) pTidyDoc;
    if (tdoc) {
        tidyRelease(tdoc);
    }
}
