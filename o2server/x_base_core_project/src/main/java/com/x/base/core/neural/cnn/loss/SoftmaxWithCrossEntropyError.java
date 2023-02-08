package com.x.base.core.neural.cnn.loss;

import com.x.base.core.neural.cnn.ConvolutionTools;
import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;

public class SoftmaxWithCrossEntropyError implements Loss {

	private double[][] y;

	private double[][] rt;

	private ConvolutionMatrix x;

	public double forward(ConvolutionMatrix x, ConvolutionMatrix t) {
		this.x = x;
		// System.out.println("x:" + x);
		double[][] rx = reshape(x);
		this.y = softmax(rx);
		this.rt = reshape(t);
		return crossEntropyError(this.y, rt);
	}

	private double[][] reshape(ConvolutionMatrix m) {
		double[][] o = new double[m.number() * m.channel() * m.height()][m.width()];
		for (int n = 0; n < m.number(); n++) {
			o[n] = m.get(n, 0, 0);
		}
		return o;
	}

	private ConvolutionMatrix reshape(double[][] arr) {
		ConvolutionMatrix m = new ConvolutionMatrix(arr.length, 1, 1, arr[0].length);
		for (int n = 0; n < arr.length; n++) {
			m.set(n, 0, 0, arr[n]);
		}
		return m;
	}

	public ConvolutionMatrix backward() {
		double batch = this.rt.length;
		double[][] o = ConvolutionTools.subtract(this.y, this.rt);
		ConvolutionMatrix m = this.reshape(o);
		m.visit((n, c, h, w, v) -> m.set(n, c, h, w, v / batch));
		return m;
	}

	private double crossEntropyError(double[][] x, double[][] t) {
		double sum = 0d;
		for (int i = 0; i < x.length; i++) {
			double[] xrow = x[i];
			double[] trow = t[i];
			int hot = ConvolutionTools.argmax(trow);
			sum += Math.log(xrow[hot] + ConvolutionTools.DELTA);
		}
		return -(sum / (double) x.length);
	}

	private double[][] softmax(double[][] x) {
		double[][] o = new double[x.length][x[0].length];
		for (int i = 0; i < x.length; i++) {
			double[] rowData = x[i];
			double max = ConvolutionTools.max(rowData);
			for (int j = 0; j < rowData.length; j++) {
				rowData[j] = Math.exp(rowData[j] - max);
			}
			double sum = 0d;
			for (double d : rowData) {
				sum += d;
			}
			if (0d == sum) {
				sum = ConvolutionTools.DELTA;
			}
			for (int j = 0; j < rowData.length; j++) {
				rowData[j] = rowData[j] / sum;
			}
			o[i] = rowData;
		}
		return o;
	}
}