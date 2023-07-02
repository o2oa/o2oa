package com.x.base.core.neural.mlp.layer.activation;

import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public class Relu implements Activation {

	private transient MultilayerPerceptronMatrix mask;

	public MultilayerPerceptronMatrix forward(MultilayerPerceptronMatrix x) {

		mask = new MultilayerPerceptronMatrix(x.row(), x.column());

		x.visit((int r, int c, float v) -> {
			if (v > 0d) {
				mask.set(r, c, v);
			} else {
				mask.set(r, c, 0);
			}

		});

		return mask.copy();
	}

	public MultilayerPerceptronMatrix backward(MultilayerPerceptronMatrix dy) {
		MultilayerPerceptronMatrix dx = new MultilayerPerceptronMatrix(dy.row(), dy.column());
		dy.visit((r, c, v) -> {
			if (mask.get(r, c) > 0) {
				dx.set(r, c, v);
			} else {
				dx.set(r, c, 0f);
			}
		});
		return dx;
	}

	@Override
	public void update() {
		// nothing
	}
}
