package com.x.base.core.neural.mlp;

public interface Data {

	public float[] input();

	public float[] label();

	public int inputSize();

	public int labelSize();

}
