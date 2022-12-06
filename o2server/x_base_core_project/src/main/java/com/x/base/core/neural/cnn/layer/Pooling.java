package com.x.base.core.neural.cnn.layer;

import java.util.concurrent.CompletableFuture;

import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;

public class Pooling implements Layer {

	private int height;
	private int width;
	private int stride;
	private int pad;

	private ConvolutionMatrix x;

	public int height() {
		return this.height;
	}

	public int width() {
		return this.width;
	}

	public int stride() {
		return this.stride;
	}

	public int pad() {
		return this.pad;
	}

	public ConvolutionMatrix x() {
		return this.x;
	}

	public Pooling(int height, int width, int stride, int pad) {
		this.height = height;
		this.width = width;
		this.stride = stride;
		this.pad = pad;
	}

	@Override
	public ConvolutionMatrix forward(ConvolutionMatrix x) {
		if (this.pad() > 0) {
			this.x = x.pad(1, 0);
		} else {
			this.x = x;
		}
		int oh = (this.x().height() - this.height()) / this.stride() + 1;
		int ow = (this.x().width() - this.width()) / this.stride() + 1;
		ConvolutionMatrix o = new ConvolutionMatrix(this.x().number(), this.x().channel(), oh, ow);
		@SuppressWarnings("unchecked")
		CompletableFuture<Void>[] futures = new CompletableFuture[x.number()];
		for (int n = 0; n < this.x().number(); n++) {
			futures[n] = forwardFuture(this.x(), oh, ow, o, n);
		}
		try {
			CompletableFuture.allOf(futures).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	private CompletableFuture<Void> forwardFuture(ConvolutionMatrix x, int oh, int ow, ConvolutionMatrix o, int n) {
		return CompletableFuture.runAsync(() -> {
			for (int c = 0; c < x.channel(); c++) {
				o.set(n, c, forward(x.get(n, c), oh, ow));
			}
		});
	}

	public double[][] forward(double[][] x, int oh, int ow) {
		double[][] o = new double[oh][ow];
		for (int i = 0; i < oh; i++) {
			for (int j = 0; j < ow; j++) {
				int xh = i * this.stride();
				int xw = j * this.stride();
				double max = Double.MIN_VALUE;
				for (int h = 0; h < this.height(); h++) {
					for (int w = 0; w < this.width(); w++) {
						if (x[xh + h][xw + w] > max) {
							max = x[xh + h][xw + w];
						}
					}
				}
				o[i][j] = max;
			}
		}
		return o;
	}

	@Override
	public ConvolutionMatrix backward(ConvolutionMatrix y) {
		ConvolutionMatrix o = new ConvolutionMatrix(this.x().number(), this.x().channel(), this.x().height(),
				this.x().width());
		@SuppressWarnings("unchecked")
		CompletableFuture<Void>[] futures = new CompletableFuture[y.number()];
		for (int n = 0; n < y.number(); n++) {
			futures[n] = backwardFuture(y, n, o);
		}
		try {
			CompletableFuture.allOf(futures).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (this.pad() > 0) {
			o = o.reduce(this.pad());
		}
		return o;
	}

	private CompletableFuture<Void> backwardFuture(ConvolutionMatrix y, int n, ConvolutionMatrix o) {
		return CompletableFuture.runAsync(() -> {
			for (int c = 0; c < y.channel(); c++) {
				o.set(n, c, this.backward(n, c, y.get(n, c)));
			}
		});
	}

	private double[][] backward(int n, int c, double[][] y) {
		double[][] o = new double[this.x().height()][this.x().width()];
		for (int i = 0; i < y.length; i++) {
			for (int j = 0; j < y[0].length; j++) {
				int xh = i * this.stride();
				int xw = j * this.stride();
				double max = Double.MIN_VALUE;
				int maxh = 0;
				int maxw = 0;
				for (int h = 0; h < this.height(); h++) {
					for (int w = 0; w < this.width(); w++) {
						if (this.x().get(n, c, xh + h, xw + w) > max) {
							max = this.x().get(n, c, xh + h, xw + w);
							maxh = xh + h;
							maxw = xw + w;
						}
					}
				}
				o[maxh][maxw] = o[maxh][maxw] + y[i][j];
			}
		}
		return o;
	}

	@Override
	public void update() {
		// nothing
	}

}