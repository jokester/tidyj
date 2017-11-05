#include <stdio.h>

#include "tidyj-error.h"

#define TIDYJ_DEBUG 0

void TIDYJ_error(const char* message, int code) {
#if TIDYJ_DEBUG
    fprintf(stderr, message, code);
#endif
}


