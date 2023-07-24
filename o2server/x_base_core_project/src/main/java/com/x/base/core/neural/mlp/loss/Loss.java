package com.x.base.core.neural.mlp.loss;

import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public interface Loss {

	MultilayerPerceptronMatrix backward();

	float forward(MultilayerPerceptronMatrix x, MultilayerPerceptronMatrix t);

}
