package com.x.base.core.neural.cnn.layer;

import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;

public interface Layer {

	ConvolutionMatrix forward(ConvolutionMatrix x);

	ConvolutionMatrix backward(ConvolutionMatrix y);

	void update();

}
