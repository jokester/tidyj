#include "tidyj-error.h"

#define TIDYJ_debug

#ifdef TIDYJ_debug
#include <stdio.h>
#endif



void TIDYJ_error(const char* message, int code) {
#ifdef TIDYJ_debug
    fprintf(stderr, message, code);
#endif
}


