package com.x.base.core.project.tools;

import java.util.Collection;

import org.apache.commons.collections4.IteratorUtils;

/**
 * @author Ray
 */
public class CollectionTools {

    private CollectionTools() {
        // nothing
    }

    public static <T> T[] toArray(Collection<T> col, Class<T> cls) {
        return IteratorUtils.toArray(col.iterator(), cls);
    }

}