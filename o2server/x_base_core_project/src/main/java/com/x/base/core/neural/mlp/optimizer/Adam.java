package com.x.base.core.neural.mlp.optimizer;

import com.x.base.core.neural.mlp.MultilayerPerceptronTools;
import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public class Adam implements Optimizer {

	private float learningRate = 0.001f;
	private float beta1 = 0.9f;
	private float beta2 = 0.999f;

	private int iter = 0;

	private MultilayerPerceptronMatrix m;
	private MultilayerPerceptronMatrix v;

	public Adam() {

	}

	public Adam(float learningRate, float beta1, float beta2) {
		this.learningRate = learningRate;
		this.beta1 = beta1;
		this.beta2 = beta2;
	}

	public int iter() {
		return this.iter;
	}

	public Adam iter(int iter) {
		this.iter = iter;
		return this;
	}

	public double learningRate() {
		return this.learningRate;
	}

	public Adam learningRate(float learningRate) {
		this.learningRate = learningRate;
		return this;
	}

	public void update(MultilayerPerceptronMatrix x, MultilayerPerceptronMatrix dx) {
		if (m == null) {
			m = new MultilayerPerceptronMatrix(x.row(), x.column());
			v = new MultilayerPerceptronMatrix(x.row(), x.column());
		}

		iter++;

		float temp = (float) (learningRate * Math.sqrt(1f - Math.pow(beta2, iter)) / (1f - Math.pow(beta1, iter)));

		x.visit((r, c, value) -> {
			float d = dx.get(r, c);
			float mv = m.get(r, c);
			float vv = v.get(r, c);
			mv = mv + (1f - beta1) * (d - mv);
			vv = vv + (float) ((1f - beta2) * (Math.pow(d, 2) - vv));
			m.set(r, c, mv);
			v.set(r, c, vv);
			x.set(r, c, (value - (float) ((temp * mv) / (Math.sqrt(vv) + MultilayerPerceptronTools.DELTA))));
		});
	}

	public static class Param {

		private float learningRate = 0.001f;
		private float beta1 = 0.9f;
		private float beta2 = 0.999f;

		public float getLearningRate() {
			return learningRate;
		}

		public void setLearningRate(float learningRate) {
			this.learningRate = learningRate;
		}

		public float getBeta1() {
			return beta1;
		}

		public void setBeta1(float beta1) {
			this.beta1 = beta1;
		}

		public float getBeta2() {
			return beta2;
		}

		public void setBeta2(float beta2) {
			this.beta2 = beta2;
		}

	}
}