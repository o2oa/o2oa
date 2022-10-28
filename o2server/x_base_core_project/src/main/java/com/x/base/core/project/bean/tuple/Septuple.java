package com.x.base.core.project.bean.tuple;

public abstract class Septuple<A, B, C, D, E, F, G> {

    protected A first;

    protected B second;

    protected C third;

    protected D fourth;

    protected E fifth;

    protected F sixth;

    protected G seventh;

    public static <A, B, C, D, E, F, G> Septuple<A, B, C, D, E, F, G> of(final A first, final B second, final C third,
            final D fourth, final E fifth, final F sixth, final G seventh) {
        return new ImmutableSeptuple<>(first, second, third, fourth, fifth, sixth, seventh);
    }

    public static <A, B, C, D, E, F, G> Septuple<A, B, C, D, E, F, G> of(final Sextuple<A, B, C, D, E, F> sextuple,
            final G seventh) {
        return new ImmutableSeptuple<>(sextuple.first(), sextuple.second(), sextuple.third(), sextuple.fourth(),
                sextuple.fifth(), sextuple.sixth(), seventh);
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

    public G seventh() {
        return this.seventh;
    }

    public static class ImmutableSeptuple<A, B, C, D, E, F, G> extends Septuple<A, B, C, D, E, F, G> {

        public ImmutableSeptuple(final A first, final B second, final C third, final D fourth, E fifth, F sixth,
                G seventh) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
            this.fifth = fifth;
            this.sixth = sixth;
            this.seventh = seventh;
        }
    }

}