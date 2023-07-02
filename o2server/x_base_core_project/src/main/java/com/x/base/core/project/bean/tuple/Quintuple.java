package com.x.base.core.project.bean.tuple;

public abstract class Quintuple<A, B, C, D, E> {

    protected A first;

    protected B second;

    protected C third;

    protected D fourth;

    protected E fifth;

    public static <A, B, C, D, E> Quintuple<A, B, C, D, E> of(final A first, final B second, final C third,
            final D fourth, final E fifth) {
        return new ImmutableQuintuple<>(first, second, third, fourth, fifth);
    }

    public static <A, B, C, D, E> Quintuple<A, B, C, D, E> of(final Quadruple<A, B, C, D> quadruple, final E fifth) {
        return new ImmutableQuintuple<>(quadruple.first(), quadruple.second(), quadruple.third(), quadruple.fourth(),
                fifth);
    }

    public static <A, B, C, D, E> Quintuple<A, B, C, D, E> of(final Triple<A, B, C> triple, final D fourth,
            final E fifth) {
        return new ImmutableQuintuple<>(triple.first(), triple.second(), triple.third(), fourth,
                fifth);
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

    public static class ImmutableQuintuple<A, B, C, D, E> extends Quintuple<A, B, C, D, E> {

        public ImmutableQuintuple(final A first, final B second, final C third, final D fourth, E fifth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
            this.fifth = fifth;
        }
    }

}