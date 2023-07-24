package com.x.base.core.neural.mlp.optimizer;

import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public class StochasticGradientDescent implements Optimizer {

	private float learningRate = 0.001f;

	public StochasticGradientDescent() {

	}

	public StochasticGradientDescent(float learningRate) {
		this.learningRate = learningRate;
	}

	@Override
	public void update(MultilayerPerceptronMatrix x, MultilayerPerceptronMatrix dx) {
		x.visit((r, c, v) -> x.set(r, c, v - (learningRate * dx.get(r, c))));

	}

}
