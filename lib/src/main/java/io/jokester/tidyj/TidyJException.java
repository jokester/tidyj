package io.jokester.tidyj;

class TidyJException extends Exception {

    TidyJException(String message) {
        super(message);
    }

    /** A TidyJ object cannot be initialized */
    static class InitError extends Error {
        InitError(String message) {
            super(message);
        }
    }

    /** Error applying option to TidyJ */
    static class IllegalOption extends Error {
        IllegalOption(String message) {
            super(message);
        }
    }

    /** A TidyJ instance is free()-d more than once */
    static class AlreadyFreed extends Error {
        AlreadyFreed(String message) {
            super(message);
        }
    }

    /* == ^ supposedly code error    ^ == */
    /* == v supposedly runtime error ^ == */

    /** Error when parsing document */
    static class ParseError extends TidyJException {
        ParseError(String message) {
            super(message);
        }
    }
}
