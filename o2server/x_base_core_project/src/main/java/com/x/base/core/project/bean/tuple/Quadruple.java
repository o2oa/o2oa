package com.x.base.core.project.bean.tuple;

public abstract class Quadruple<A, B, C, D> {

    protected A first;

    protected B second;

    protected C third;

    protected D fourth;

    public static <A, B, C, D> Quadruple<A, B, C, D> of(final A first, final B second, final C third, final D fourth) {
        return new ImmutableQuadruple<>(first, second, third, fourth);
    }

    public static <A, B, C, D> Quadruple<A, B, C, D> of(final Triple<A, B, C> triple, final D fourth) {
        return new ImmutableQuadruple<>(triple.first(), triple.second(), triple.third(), fourth);
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

    public static class ImmutableQuadruple<A, B, C, D> extends Quadruple<A, B, C, D> {

        public ImmutableQuadruple(final A first, final B second, final C third, final D fourth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
        }
    }

}