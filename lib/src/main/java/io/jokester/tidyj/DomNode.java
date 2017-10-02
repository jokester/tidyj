package io.jokester.tidyj;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * immutable data type for a DOM node
 * <p>
 * All memory are allocated in java heap, and is safe to use after
 * the {@link TidyJ} instance they are from is freed.
 */
public final class DomNode {

    /* @NonNull */
    public final String textContent;

    /* @NonNull */
    public final NodeType type;

    /* @NonNull
    * for element (start / end / startend tag): tagName for element
    * otherwise: null
    */
    public final String tagName;

    /* @NonNull */
    public final Map<String, String> attributes;

    /* @NonNull */
    public final List<DomNode> children;

    /**
     * package only:
     * <p>
     * NOTE: not making copy of map/list inside, caller must not modify
     * map/list objects after created a DomNode with them.
     */
    DomNode(NodeType type,
            String tagName,
            String textContent,
            Map<String, String> attributes,
            List<DomNode> children) {

        this.type = type;

        switch (type) {
            case TidyNode_Start:
            case TidyNode_End:
            case TidyNode_StartEnd:
                this.tagName = tagName == null ? "" : tagName;
                break;
            default:
                this.tagName = "NONAME";
        }

        this.textContent = textContent == null ? "" : textContent;

        this.attributes = attributes == null ? EmptyAttributeSet : Collections.unmodifiableMap(attributes);

        this.children = children == null ? EmptyChildren : Collections.unmodifiableList(children);
    }

    private static final Map<String, String> EmptyAttributeSet = Collections.emptyMap();
    private static final List<DomNode> EmptyChildren = Collections.emptyList();

    /**
     * mirrors TidyNodeType in tidyenum.h
     */
    public static enum NodeType {
        TidyNode_Root,        /**< Root */
        TidyNode_DocType,     /**< DOCTYPE */
        TidyNode_Comment,     /**< Comment */
        TidyNode_ProcIns,     /**< Processing Instruction */
        TidyNode_Text,        /**< Text */
        TidyNode_Start,       /**< Start Tag */
        TidyNode_End,         /**< End Tag */
        TidyNode_StartEnd,    /**< Start/End (empty) Tag */
        TidyNode_CDATA,       /**< Unparsed Text */
        TidyNode_Section,     /**< XML Section */
        TidyNode_Asp,         /**< ASP Source */
        TidyNode_Jste,        /**< JSTE Source */
        TidyNode_Php,         /**< PHP Source */
        TidyNode_XmlDecl      /**< XML Declaration */
    }
}