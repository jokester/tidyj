package io.jokester.tidyj;

import java.util.ArrayList;

/**
 * A set of options that can be applied to {@link io.jokester.tidyj.TidyJ} instance
 * before parse starts.
 * See {@see http://api.html-tidy.org/tidy/quickref_5.4.0.html} for available options
 * <p>
 * Options that are added later have higher precedence.
 * <p>
 * Internally, a "option set" is just a list of stateless name-value pairs,
 * it is thus fine to use one option set for multiple documents.
 * <p>
 * <p>
 * Types of options:
 * - bool
 * - string
 * - integer
 * - any: A string value that will be interpreted by libtidy.
 * </p>
 * <p>
 * <p>
 * If a option is not successfully set (may due to bad name / type / value),
 * a {@link io.jokester.tidyj.TidyJException.IllegalOption} will be thrown.
 * </p>
 */
public final class TidyOptionSet {

    private final ArrayList<TidyOption> options = new ArrayList<>();

    private static String err(String name, Object value) {
        return String.format("error setting %s to %s", name, value);
    }

    public TidyOptionSet addBoolOption(String name, boolean value) {
        return this.addOption(new TidyBoolOption(name, value));
    }

    public TidyOptionSet addStringOption(String name, String value) {
        return this.addOption(new TidyStringOption(name, value));
    }

    public TidyOptionSet addIntOption(String name, int value) {
        return this.addOption(new TidyIntegerOption(name, value));
    }

    public TidyOptionSet addAnyOption(String name, String value) {
        return this.addOption(new TidyAnyOption(name, value));
    }

    void apply(TidyDoc doc) {
        for (TidyOption o : options) {
            o.apply(doc);
        }
    }

    private TidyOptionSet addOption(TidyOption o) {
        options.add(o);
        return this;
    }

    /**
     * a (name, value) pair of boolean option
     */
    abstract class TidyOption {
        abstract void apply(TidyDoc doc) throws TidyJException.IllegalOption;
    }

    class TidyBoolOption extends TidyOption {
        final String name;
        final boolean value;

        TidyBoolOption(String name, boolean value) {
            this.name = name;
            this.value = value;
        }

        @Override
        void apply(TidyDoc doc) throws TidyJException.IllegalOption {
            if (!doc.setBoolOption(name, value)) {
                throw new TidyJException.IllegalOption(err(name, value));
            }
        }
    }

    final class TidyStringOption extends TidyOption {
        final String name, value;

        TidyStringOption(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        void apply(TidyDoc doc) throws TidyJException.IllegalOption {
            if (!doc.setStringOption(name, value)) {
                throw new TidyJException.IllegalOption(err(name, value));
            }
        }
    }

    final class TidyIntegerOption extends TidyOption {
        final String name;
        final int value;

        TidyIntegerOption(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        void apply(TidyDoc doc) throws TidyJException.IllegalOption {
            if (!doc.setIntOption(name, value)) {
                throw new TidyJException.IllegalOption(err(name, value));
            }
        }
    }

    final class TidyAnyOption extends TidyOption {
        final String name;
        final String value;

        TidyAnyOption(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        void apply(TidyDoc doc) throws TidyJException.IllegalOption {
            if (!doc.setAnyOption(name, value)) {
                throw new TidyJException.IllegalOption(err(name, value));
            }
        }
    }
}
