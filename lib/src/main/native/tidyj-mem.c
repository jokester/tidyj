#if 0
#include "tidy.h"
#include "tidyj-mem.h"
#include "tidyj-error.h"

ctmbstr fallbackMessage = "MyAllocator_panic";

static void MyAllocator_panic(TidyAllocator *base, /* MUST BE static */ ctmbstr _message) {
    assert(base);
    ctmbstr message = _message ? _message : fallbackMessage;

    MyAllocator *self = (MyAllocator*)base;
    if (self->panicJump != NULL) {
        self->panicMessage = message;
        longjmp(self->panicJump, AllocatorPanic);
    } else {
        fputs(message, stderr);
        exit(AllocatorPanic);
    }
}

static void* MyAllocator_alloc(TidyAllocator *base, size_t nBytes) {
    MyAllocator *self = (MyAllocator*)base;

    if (self->size - self->consumed > nBytes) {
        void *p = self->start + self->consumed;
        self->consumed += nBytes;
        return p;
    }

    MyAllocator_panic(base, "memory exhausted");
    /* should not got there */
    return NULL;
}

static void* MyAllocator_realloc(TidyAllocator *base, void *block, size_t nBytes) {
    MyAllocator *self = (MyAllocator*)base;

    void* n = MyAllocator_alloc(base, nBytes);
    memcpy(n, block, nBytes);
    return n;
}

static void MyAllocator_free(TidyAllocator *base, void *block) {
    assert(base);
}

static const TidyAllocatorVtbl MyAllocatorVtbl = {
    MyAllocator_alloc,
    MyAllocator_realloc,
    MyAllocator_free,
    MyAllocator_panic
};

int initAllocator(MyAllocator *allocator, void *start, size_t size) {
    if (!allocator)
        return -1;
    if (!start)
        return -2;
    if (size <= 0)
        return -3;

    memset(allocator, 0x0, sizeof(MyAllocator));

    allocator->base.vtbl = &MyAllocatorVtbl;
    allocator->start = start;
    allocator->size = size;
    allocator->consumed = 0;
    allocator->panicJump = NULL;

    return 0;
}

int setPanicJump(MyAllocator *allocator, jmp_buf *jBuf) {
    allocator->panicJump = jBuf;
}
#endif
