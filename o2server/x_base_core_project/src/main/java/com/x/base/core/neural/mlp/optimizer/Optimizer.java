package com.x.base.core.neural.mlp.optimizer;

import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public interface Optimizer {

	public void update(MultilayerPerceptronMatrix x, MultilayerPerceptronMatrix dx);
}
