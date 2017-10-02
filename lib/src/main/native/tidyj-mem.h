#if 0
/**
 * tidyj-mem: custom memory allocator
 * FIXME: not working
 */

#include <setjmp.h>
#include "tidy.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct _MyAllocator {
    TidyAllocator base;
    void *start;
    size_t size;
    size_t consumed;
    /** a NON-OPTIONAL jup_buf to use on panic and set to NULL if nothing wrong (this makes the allocator single-threaded) */
    jmp_buf *panicJump;
    ctmbstr panicMessage;
} MyAllocator;

/**
 * initialize a TidyAllocator
 * @return 0 on succeed
 */
int initMyAllocator(MyAllocator *allocator, void *start, size_t size);
int setPanicJump(MyAllocator *allocator, jmp_buf *jBuf);

#ifdef __cplusplus
}
#endif
#endif
