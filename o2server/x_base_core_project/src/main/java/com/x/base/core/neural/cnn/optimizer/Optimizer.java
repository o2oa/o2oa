package com.x.base.core.neural.cnn.optimizer;

import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;

public interface Optimizer {

	public void update(ConvolutionMatrix x, ConvolutionMatrix dx);
}
