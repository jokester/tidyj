enum TidyJError {
    AllocatorPanic = 100,
};

extern void TIDYJ_error(const char* message, int code);
