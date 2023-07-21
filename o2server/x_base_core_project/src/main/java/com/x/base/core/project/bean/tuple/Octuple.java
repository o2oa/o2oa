package com.x.base.core.project.bean.tuple;

public abstract class Octuple<A, B, C, D, E, F, G, H> {

    protected A first;

    protected B second;

    protected C third;

    protected D fourth;

    protected E fifth;

    protected F sixth;

    protected G seventh;

    protected H eighth;

    public static <A, B, C, D, E, F, G, H> Octuple<A, B, C, D, E, F, G, H> of(final A first, final B second,
            final C third, final D fourth, final E fifth, final F sixth, final G seventh, final H eighth) {
        return new ImmutableOctuple<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
    }

    public static <A, B, C, D, E, F, G, H> Octuple<A, B, C, D, E, F, G, H> of(
            final Septuple<A, B, C, D, E, F, G> septuple,
            final H eighth) {
        return new ImmutableOctuple<>(septuple.first(), septuple.second(), septuple.third(), septuple.fourth(),
                septuple.fifth(), septuple.sixth(), septuple.seventh(), eighth);
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

    public H eighth() {
        return this.eighth;
    }

    public static class ImmutableOctuple<A, B, C, D, E, F, G, H> extends Octuple<A, B, C, D, E, F, G, H> {

        public ImmutableOctuple(final A first, final B second, final C third, final D fourth, E fifth, F sixth,
                G seventh, H eighth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
            this.fifth = fifth;
            this.sixth = sixth;
            this.seventh = seventh;
            this.eighth = eighth;
        }
    }

}