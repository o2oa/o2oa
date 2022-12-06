package com.x.base.core.neural.cnn.layer;

import com.x.base.core.neural.cnn.ConvolutionTools;
import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;
import com.x.base.core.neural.cnn.optimizer.Optimizer;

public class Affine implements Layer {

	private String name;

	private ConvolutionMatrix w;
	private ConvolutionMatrix dw;

	private ConvolutionMatrix b;
	private ConvolutionMatrix db;

	private ConvolutionMatrix x;

	private Optimizer optimizer;

	public String name() {
		return this.name;
	}

	public Affine w(ConvolutionMatrix w) {
		this.w = w;
		this.dw = new ConvolutionMatrix(w.number(), w.channel(), w.height(), w.width());
		return this;
	}

	public Affine b(ConvolutionMatrix b) {
		this.b = b;
		this.db = new ConvolutionMatrix(b.number(), b.channel(), b.height(), b.width());
		return this;
	}

	public Affine optimizer(Optimizer optimizer) {
		this.optimizer = optimizer;
		return this;
	}

	public ConvolutionMatrix b() {
		return this.b;
	}

	public ConvolutionMatrix w() {
		return this.w;
	}

	public ConvolutionMatrix dw() {
		return this.dw;
	}

	public ConvolutionMatrix x() {
		return this.x;
	}

	public Optimizer optimizer() {
		return this.optimizer;
	}

	@Override
	public ConvolutionMatrix forward(ConvolutionMatrix x) {
		this.x = x;
		double[][] arr = this.reshape(x);
		double[][] o = ConvolutionTools.dot(arr, this.reshape(this.w()));
		ConvolutionMatrix m = new ConvolutionMatrix(o.length, 1, 1, o[0].length);
		for (int i = 0; i < o.length; i++) {
			m.set(i, 0, 0, o[i]);
		}
		return m;
	}

	@Override
	public ConvolutionMatrix backward(ConvolutionMatrix dy) {
		this.dwBackward(dy);
		this.dbBackward(dy);
		double[][] arr = ConvolutionTools.dot(reshape(dy), ConvolutionTools.transpose(reshape(this.w())));
		return reshape(arr);
	}

	private double[][] reshape(ConvolutionMatrix x) {
		double[][] arr = new double[x.number()][x.channel() * x.height() * x.width()];
		int sizeOfChannel = x.height() * x.width();
		x.visit((n, c, h, w, v) -> arr[n][(c * sizeOfChannel) + (h * x.width()) + w] = v);
		return arr;
	}

	private ConvolutionMatrix reshape(double[][] arr) {
		ConvolutionMatrix o = new ConvolutionMatrix(x.number(), x.channel(), x.height(), x.width());
		int sizeOfChannel = x.height() * x.width();
		o.visit((n, c, h, w, v) -> o.set(n, c, h, w, arr[n][(c * sizeOfChannel) + (h * x.width()) + w]));
		return o;
	}

	private void dwBackward(ConvolutionMatrix dy) {
		double[][] o = new double[x.number()][x.channel() * x.height() * x.width()];
		int sizeOfChannel = x.height() * x.width();
		for (int n = 0; n < x.number(); n++) {
			for (int c = 0; c < x.channel(); c++) {
				for (int h = 0; h < x.height(); h++) {
					for (int width = 0; width < x.width(); width++) {
						o[n][c * sizeOfChannel + h * x.width() + width] = x.get(n, c, h, width);
					}
				}
			}
		}
		double[][] values = ConvolutionTools.dot(ConvolutionTools.transpose(o), this.reshape(dy));
		for (int n = 0; n < values.length; n++) {
			this.dw.set(n, 0, 0, values[n]);
		}
	}

	private void dbBackward(ConvolutionMatrix dy) {
		for (int i = 0; i < dy.width(); i++) {
			double v = 0d;
			for (int j = 0; j < dy.height(); j++) {
				v += dy.get(0, 0, j, i);
			}
			db.set(i, 0, 0, 0, v);
		}
	}

	@Override
	public void update() {
		optimizer.update(w, dw);
		optimizer.update(b, db);
	}

}
