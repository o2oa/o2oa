package com.x.base.core.neural.mlp.loss;

import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public class SigmoidWithMeanSquareError implements Loss {

	private transient MultilayerPerceptronMatrix y;

	private transient MultilayerPerceptronMatrix t;

	public SigmoidWithMeanSquareError() {
		// nothing
	}

	@Override
	public float forward(MultilayerPerceptronMatrix x, MultilayerPerceptronMatrix t) {
		this.t = t;
		this.y = x.sigmoid();
		return this.meanSquaredError(this.y, t);
	}

	@Override
	public MultilayerPerceptronMatrix backward() {
		float batch = t.row();
		MultilayerPerceptronMatrix m = this.y.subtract(this.t);
		m.visit((r, c, v) -> m.set(r, c, (v * (1 - v)) / batch));
		return m;
	}

	public float meanSquaredError(MultilayerPerceptronMatrix x, MultilayerPerceptronMatrix t) {
		float sum = 0f;
		for (int i = 0; i < x.row(); i++) {
			float[] xrow = x.row(i);
			float[] trow = t.row(i);
			for (int j = 0; j < xrow.length; j++) {
				sum += Math.pow(xrow[j] - trow[j], 2);
			}
		}
		return sum / (2f * x.row());
	}

}
