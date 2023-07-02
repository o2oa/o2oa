package com.x.base.core.project.bean.tuple;

import java.util.Objects;

import org.apache.commons.lang3.builder.CompareToBuilder;

public abstract class Pair<A, B> implements Comparable<Pair<A, B>> {

	protected A first;

	protected B second;

	public static <A, B> Pair<A, B> of(final A first, final B second) {
		return new ImmutablePair<>(first, second);
	}

	public A first() {
		return this.first;
	}

	public B second() {
		return this.second;
	}

	@Override
	public int compareTo(final Pair<A, B> other) {
		return new CompareToBuilder().append(first(), other.first()).append(second(), other.second()).toComparison();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(first()) ^ Objects.hashCode(second());
	}

	public static class ImmutablePair<A, B> extends Pair<A, B> {

		public ImmutablePair(final A first, final B second) {
			this.first = first;
			this.second = second;
		}
	}

}