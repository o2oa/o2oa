package com.x.base.core.neural.mlp.layer;

import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public interface Layer {

	MultilayerPerceptronMatrix forward(MultilayerPerceptronMatrix x);

	MultilayerPerceptronMatrix backward(MultilayerPerceptronMatrix y);

	public default String name() {
		return this.getClass().getSimpleName();
	}

	void update();
}
