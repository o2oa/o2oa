package com.x.base.core.neural.mlp.layer.activation;

import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public class Tanh implements Activation {

	private MultilayerPerceptronMatrix out;

	@Override
	public MultilayerPerceptronMatrix forward(MultilayerPerceptronMatrix x) {
		MultilayerPerceptronMatrix m = x.copy();
		m.visit((r, c, v) -> m.set(r, c, (float) ((Math.pow(Math.E, v) - Math.pow(Math.E, -v))
				/ (Math.pow(Math.E, v) + Math.pow(Math.E, -v)))));
		out = m.copy();
		return m;
	}

	@Override
	public MultilayerPerceptronMatrix backward(MultilayerPerceptronMatrix x) {
		MultilayerPerceptronMatrix m = x.copy();
		m.visit((r, c, v) -> {
			float o = out.get(r, c);
			m.set(r, c, v * (1f - o * o));
		});
		return m;
	}

	@Override
	public void update() {
		// nothing
	}
}
