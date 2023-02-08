package com.x.base.core.neural.cnn.layer.activation;

import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;

public class Relu implements Activation {

	private ConvolutionMatrix mask;

	@Override
	public ConvolutionMatrix forward(ConvolutionMatrix x) {
		this.mask = new ConvolutionMatrix(x.number(), x.channel(), x.height(), x.width());
		x.visit((n, c, h, w, v) -> {
			if (v > 0d) {
				mask.set(n, c, h, w, v);
			} else {
				mask.set(n, c, h, w, 0);
			}
		});
		return mask.copy();
	}

	@Override
	public ConvolutionMatrix backward(ConvolutionMatrix y) {
		ConvolutionMatrix dx = new ConvolutionMatrix(y.number(), y.channel(), y.height(), y.width());
		y.visit((n, c, h, w, v) -> {
			if (mask.get(n, c, h, w) > 0) {
				dx.set(n, c, h, w, v);
			} else {
				dx.set(n, c, h, w, 0d);
			}
		});
		return dx;
	}

	// @Override
	public void update() {
		// nothing
	}
}
