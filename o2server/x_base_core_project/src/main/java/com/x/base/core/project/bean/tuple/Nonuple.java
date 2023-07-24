package com.x.base.core.project.bean.tuple;

public abstract class Nonuple<A, B, C, D, E, F, G, H, I> {

    protected A first;

    protected B second;

    protected C third;

    protected D fourth;

    protected E fifth;

    protected F sixth;

    protected G seventh;

    protected H eighth;

    protected I ninth;

    public static <A, B, C, D, E, F, G, H, I> Nonuple<A, B, C, D, E, F, G, H, I> of(final A first, final B second,
            final C third, final D fourth, final E fifth, final F sixth, final G seventh, final H eighth,
            final I ninth) {
        return new ImmutableNonuple<>(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth);
    }

    public static <A, B, C, D, E, F, G, H, I> Nonuple<A, B, C, D, E, F, G, H, I> of(
            final Octuple<A, B, C, D, E, F, G, H> octuple,
            final I ninth) {
        return new ImmutableNonuple<>(octuple.first(), octuple.second(), octuple.third(), octuple.fourth(),
                octuple.fifth(), octuple.sixth(), octuple.seventh(), octuple.eighth(), ninth);
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

    public I ninth() {
        return this.ninth;
    }

    public static class ImmutableNonuple<A, B, C, D, E, F, G, H, I> extends Nonuple<A, B, C, D, E, F, G, H, I> {

        public ImmutableNonuple(final A first, final B second, final C third, final D fourth, E fifth, F sixth,
                G seventh, H eighth, I ninth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
            this.fifth = fifth;
            this.sixth = sixth;
            this.seventh = seventh;
            this.eighth = eighth;
            this.ninth = ninth;
        }
    }

}