package io.jokester.tidyj;

import java.util.ArrayList;

/**
 * A set of options to be applied on a {@link io.jokester.tidyj.TidyJ} instance
 * <p>
 * See {@see http://api.html-tidy.org/tidy/quickref_5.4.0.html} for available options
 */
public final class TidyOptionSet {

    private final ArrayList<TidyOption> options = new ArrayList<>();

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

    private TidyOptionSet addOption(TidyOption o) {
        options.add(o);
        return this;
    }

    void apply(TidyJ doc) throws TidyJException.IllegalOption {
        for (TidyOption o : options) {
            o.apply(doc);
        }
    }

    abstract class TidyOption {
        abstract void apply(TidyJ doc) throws TidyJException.IllegalOption;
    }

    /**
     * a (tagName, value) pair of boolean option
     */
    class TidyBoolOption extends TidyOption {
        final String name;
        final boolean value;

        TidyBoolOption(String name, boolean value) {
            this.name = name;
            this.value = value;
        }

        @Override
        void apply(TidyJ doc) throws TidyJException.IllegalOption {
            if (!doc.setBoolOption(name, value)) {
                throw new TidyJException.IllegalOption(err(name, value));
            }
        }
    }

    class TidyStringOption extends TidyOption {
        final String name, value;

        TidyStringOption(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        void apply(TidyJ doc) throws TidyJException.IllegalOption {
            if (!doc.setStringOption(name, value)) {
                throw new TidyJException.IllegalOption(err(name, value));
            }
        }
    }

    class TidyIntegerOption extends TidyOption {
        final String name;
        final int value;

        TidyIntegerOption(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        void apply(TidyJ doc) throws TidyJException.IllegalOption {
            if (!doc.setIntOption(name, value)) {
                throw new TidyJException.IllegalOption(err(name, value));
            }
        }
    }

    class TidyAnyOption extends TidyOption {
        final String name;
        final String value;

        TidyAnyOption(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        void apply(TidyJ doc) throws TidyJException.IllegalOption {
            if (!doc.setAnyOption(name, value)) {
                throw new TidyJException.IllegalOption(err(name, value));
            }
        }
    }

    private static final String err(String name, Object value) {
        return String.format("error setting %s to %s", name, value);
    }
}
