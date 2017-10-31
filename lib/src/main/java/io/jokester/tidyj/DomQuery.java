package io.jokester.tidyj;

/**
 * A query that can be used to obtain
 */
public abstract class DomQuery {

    public final DomNode[] query(TidyDoc doc) {
        synchronized (doc) {
            long[] nodePointers = doc.queryDom(this);

            DomNode[] nodes = doc.pullDom(nodePointers);
            return nodes;
        }
    }

    // TODO: how?
    public abstract DomNode[] query(DomNode start);
}
