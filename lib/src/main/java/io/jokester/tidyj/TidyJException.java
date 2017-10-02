package io.jokester.tidyj;

class TidyJException extends Exception {

    TidyJException(String message) {
        super(message);
    }

    static class InitError extends TidyJException {
        InitError(String message) {
            super(message);
        }
    }

    static class ParseError extends TidyJException {
        ParseError(String message) {
            super(message);
        }
    }

    static class IllegalOption extends TidyJException {
        IllegalOption(String message) {
            super(message);
        }
    }

    static class AlreadyFreed extends Error {
        AlreadyFreed(String message) {
            super(message);
        }
    }
}
