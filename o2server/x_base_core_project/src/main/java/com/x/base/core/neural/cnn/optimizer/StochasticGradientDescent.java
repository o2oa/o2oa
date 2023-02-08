package com.x.base.core.neural.cnn.optimizer;

import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;

public class StochasticGradientDescent implements Optimizer {

	private double learningRate = 0.01d;

	public StochasticGradientDescent() {

	}

	public StochasticGradientDescent(double learningRate) {
		this.learningRate = learningRate;
	}

	@Override
	public void update(ConvolutionMatrix x, ConvolutionMatrix dx) {
		x.visit((n, c, h, w, v) -> x.set(n, c, h, w, v - (learningRate * dx.get(n, c, h, w))));

	}

}
