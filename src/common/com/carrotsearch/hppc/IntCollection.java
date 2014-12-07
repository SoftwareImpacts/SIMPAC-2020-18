package com.carrotsearch.hppc;

import com.carrotsearch.hppc.predicates.IntPredicate;

/**
 * A collection allows basic, efficient operations on sets of elements 
 * (difference and intersection).
 */
 @javax.annotation.Generated(date = "2014-12-06T10:00:22+0100", value = "HPPC generated from: IntCollection.java") 
public interface IntCollection extends IntContainer
{
    /**
     * Removes all occurrences of <code>e</code> from this collection.
     * 
     * @param e Element to be removed from this collection, if present.
     * @return The number of removed elements as a result of this call.
     */
    public int removeAllOccurrences(int e);

    /**
     * Removes all elements in this collection that are present
     * in <code>c</code>. Runs in time proportional to the number
     * of elements in this collection. Equivalent of sets difference.
     * 
     * @return Returns the number of removed elements.
     */
    public int removeAll(IntLookupContainer c);

    /**
     * Removes all elements in this collection for which the
     * given predicate returns <code>true</code>.
     */
    public int removeAll(IntPredicate predicate);

    /**
     * Keeps all elements in this collection that are present
     * in <code>c</code>. Runs in time proportional to the number
     * of elements in this collection. Equivalent of sets intersection.
     * 
     * @return Returns the number of removed elements.
     */
    public int retainAll(IntLookupContainer c);

    /**
     * Keeps all elements in this collection for which the
     * given predicate returns <code>true</code>.
     */
    public int retainAll(IntPredicate predicate);

    /**
     * Removes all elements from this collection.
     */
    public void clear();
}