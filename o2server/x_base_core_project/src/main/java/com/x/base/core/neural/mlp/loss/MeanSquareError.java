package com.x.base.core.neural.mlp.loss;

import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public class MeanSquareError implements Loss {

	private transient MultilayerPerceptronMatrix x;

	private transient MultilayerPerceptronMatrix t;

	public MeanSquareError() {
		// nothing
	}

//    self.x = x
//    self.y = y
//    return np.sum(np.square(x - y)) / x.size
	@Override
	public float forward(MultilayerPerceptronMatrix x, MultilayerPerceptronMatrix t) {
		this.x = x;
		this.t = t;
		float s = 0f;
		for (int r = 0; r < x.row(); r++) {
			for (int c = 0; c < x.column(); c++) {
				s += Math.pow(x.get(r, c) - t.get(r, c), 2);
			}
		}
		return s / ((float) x.row());
	}

	@Override
	public MultilayerPerceptronMatrix backward() {
		MultilayerPerceptronMatrix m = x.subtract(t);
		m.visit((r, c, v) -> m.set(r, c, 2f * (v / (float) x.row())));
		return m;
	}

}
