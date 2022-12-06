package com.x.base.core.neural.cnn.optimizer;

import com.x.base.core.neural.cnn.ConvolutionTools;
import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;

public class Adam implements Optimizer {

	private double learningRate = 0.1d;
	private double beta1 = 0.9d;
	private double beta2 = 0.999d;

	private int iter = 0;

	private ConvolutionMatrix m;
	private ConvolutionMatrix v;

	public Adam() {

	}

	public Adam(double learningRate, double beta1, double beta2) {
		this.learningRate = learningRate;
		this.beta1 = beta1;
		this.beta2 = beta2;
	}

	public void update(ConvolutionMatrix x, ConvolutionMatrix dx) {
		if (m == null) {
			m = new ConvolutionMatrix(x.number(), x.channel(), x.height(), x.width());
			v = new ConvolutionMatrix(x.number(), x.channel(), x.height(), x.width());
		}

		iter++;
		double temp = (learningRate * Math.sqrt(1.0 - Math.pow(beta2, iter)) / (1d - Math.pow(beta1, iter)));

		x.visit((n, c, h, w, value) -> {
			double d = dx.get(n, c, h, w);
			double mv = m.get(n, c, h, w);
			double vv = v.get(n, c, h, w);
			mv = mv + (1d - beta1) * (d - mv);
			vv = vv + ((1d - beta2) * (Math.pow(d, 2) - vv));
			m.set(n, c, h, w, mv);
			v.set(n, c, h, w, vv);
			x.set(n, c, h, w, (value - ((temp * mv) / (Math.sqrt(vv) + ConvolutionTools.DELTA))));
		});
	}

	public static class Param {

		private double learningRate = 0.001d;
		private double beta1 = 0.9d;
		private double beta2 = 0.999d;

		public double getLearningRate() {
			return learningRate;
		}

		public void setLearningRate(double learningRate) {
			this.learningRate = learningRate;
		}

		public double getBeta1() {
			return beta1;
		}

		public void setBeta1(double beta1) {
			this.beta1 = beta1;
		}

		public double getBeta2() {
			return beta2;
		}

		public void setBeta2(double beta2) {
			this.beta2 = beta2;
		}

	}
}