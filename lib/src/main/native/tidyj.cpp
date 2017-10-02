#include <cstring>
#include "io_jokester_tidyj_TidyJ.h"
#include "tidy.h"
#include "jni-util.hpp"
#include "tidyj-mem.h"
#include "tidyj-error.h"

/** a report filter to silence warnings */
extern "C"
Bool silenceReports( TidyDoc tdoc, TidyReportLevel lvl,
                                                uint line, uint col, ctmbstr code, va_list args ) {
    return yes;
}
/**
 * FIXME:
 * - learn how to cast pointer from/to jlong
 *   (feeling that explicit conversion may not be the right way)
 */

/**
 * native long nativeInit();
 *
 * TODO:
 * - custom memory allocator, using direct ByteBuffer
 */
JNIEXPORT jlong JNICALL Java_io_jokester_tidyj_TidyJ_nativeInit
(JNIEnv * env, jobject jthis, jobject nativeHeap) {
    TidyDoc tdoc = NULL;

    if (nativeHeap) {
        /** NOT supported */
        goto fail;
    }

    tdoc = tidyCreate();

    if (!tdoc) {
        goto fail;
    }

    // TODO: does cpp have a cast for this?
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
JNIEXPORT void JNICALL Java_io_jokester_tidyj_TidyJ_nativeFree
(JNIEnv *env, jobject jthis, jlong pTidyDoc) {
    TidyDoc tdoc = (TidyDoc) pTidyDoc;
    if (tdoc) {
        tidyRelease(tdoc);
    }
}
