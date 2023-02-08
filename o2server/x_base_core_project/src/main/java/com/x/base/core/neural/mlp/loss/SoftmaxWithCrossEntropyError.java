package com.x.base.core.neural.mlp.loss;

import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.neural.mlp.MultilayerPerceptronTools;
import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public class SoftmaxWithCrossEntropyError implements Loss {

	private transient MultilayerPerceptronMatrix y;

	private transient MultilayerPerceptronMatrix t;

	public float forward(MultilayerPerceptronMatrix x, MultilayerPerceptronMatrix t) {
		this.y = softmaxAsRow(x);
		this.t = t;
		return crossEntropyError(this.y, t);
	}

	public MultilayerPerceptronMatrix backward() {
		float batch = t.row();
		MultilayerPerceptronMatrix m = this.y.subtract(this.t);
		m.visit((r, c, v) -> m.set(r, c, v / batch));
		return m;
	}

	private float crossEntropyError(MultilayerPerceptronMatrix x, MultilayerPerceptronMatrix t) {
		float sum = 0f;
		for (int i = 0; i < x.row(); i++) {
			float[] xrow = x.row(i);
			float[] trow = t.row(i);
			for (int j = 0; j < xrow.length; j++) {
				sum += (trow[j] * Math.log(xrow[j] + MultilayerPerceptronTools.DELTA));
			}
		}
		return -(sum / (float) x.row());
	}

	public MultilayerPerceptronMatrix softmaxAsRow(MultilayerPerceptronMatrix x) {
		MultilayerPerceptronMatrix s = new MultilayerPerceptronMatrix(x.row(), x.column());
		for (int i = 0; i < x.row(); i++) {
			float[] rowData = x.row(i);
			float max = NumberUtils.max(rowData);
			for (int j = 0; j < rowData.length; j++) {
				rowData[j] = (float) Math.exp(rowData[j] - max);
			}
			float sum = 0f;
			for (float d : rowData) {
				sum += d;
			}
			sum += MultilayerPerceptronTools.DELTA;
			for (int j = 0; j < rowData.length; j++) {
				rowData[j] = rowData[j] / sum;
			}
			s.row(i, rowData);
		}
		return s;
	}
}