package com.x.base.core.neural.cnn.loss;

import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;

public interface Loss {

	ConvolutionMatrix backward();

	double forward(ConvolutionMatrix x, ConvolutionMatrix t);

}
