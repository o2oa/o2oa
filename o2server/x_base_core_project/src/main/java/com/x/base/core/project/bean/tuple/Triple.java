package com.x.base.core.project.bean.tuple;

import java.util.Objects;

import org.apache.commons.lang3.builder.CompareToBuilder;

public abstract class Triple<A, B, C> implements Comparable<Triple<A, B, C>> {

    protected A first;

    protected B second;

    protected C third;

    public static <A, B, C> Triple<A, B, C> of(final A first, final B second, final C third) {
        return new ImmutableTriple<>(first, second, third);
    }

    public static <A, B, C> Triple<A, B, C> of(final Pair<A, B> pair, final C third) {
        return new ImmutableTriple<>(pair.first(), pair.second(), third);
    }

    public A first() {
        return this.first;
    }

    public B second() {
        return this.second;
    }

    public C third() {
        return this.third;
    }

    @Override
    public int compareTo(final Triple<A, B, C> other) {
        return new CompareToBuilder().append(first(), other.first()).append(second(), other.second())
                .append(third(), other.third()).toComparison();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj == this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(first()) ^ Objects.hashCode(second());
    }

    public static class ImmutableTriple<A, B, C> extends Triple<A, B, C> {

        public ImmutableTriple(final A first, final B second, final C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }

}