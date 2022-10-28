package com.x.base.core.project.bean.tuple;

public abstract class Sextuple<A, B, C, D, E, F> {

    protected A first;

    protected B second;

    protected C third;

    protected D fourth;

    protected E fifth;

    protected F sixth;

    public static <A, B, C, D, E, F> Sextuple<A, B, C, D, E, F> of(final A first, final B second, final C third,
            final D fourth, final E fifth, final F sixth) {
        return new ImmutableSextuple<>(first, second, third, fourth, fifth, sixth);
    }

    public static <A, B, C, D, E, F> Sextuple<A, B, C, D, E, F> of(final Quintuple<A, B, C, D, E> quintuple,
            final F sixth) {
        return new ImmutableSextuple<>(quintuple.first(), quintuple.second(), quintuple.third(), quintuple.fourth(),
                quintuple.fifth(), sixth);
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

    public D fourth() {
        return this.fourth;
    }

    public E fifth() {
        return this.fifth;
    }

    public F sixth() {
        return this.sixth;
    }

    public static class ImmutableSextuple<A, B, C, D, E, F> extends Sextuple<A, B, C, D, E, F> {

        public ImmutableSextuple(final A first, final B second, final C third, final D fourth, E fifth, F sixth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
            this.fifth = fifth;
            this.sixth = sixth;
        }
    }

}