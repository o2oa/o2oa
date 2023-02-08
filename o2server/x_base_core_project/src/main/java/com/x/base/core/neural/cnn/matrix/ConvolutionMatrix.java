package com.x.base.core.neural.cnn.matrix;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ConvolutionMatrix {

	private double[][][][] data;
	private int number;
	private int channel;
	private int height;
	private int width;

	public ConvolutionMatrix(int number, int channel, int height, int width) {
		this.number = number;
		this.channel = channel;
		this.height = height;
		this.width = width;
		this.data = new double[number][channel][height][width];
	}

	public int number() {
		return this.number;
	}

	public double[][][] get(int n) {
		return this.data[n];
	}

	public double[][] get(int n, int c) {
		return this.data[n][c];
	}

	public double[] get(int n, int c, int h) {
		return this.data[n][c][h];
	}

	public double get(int n, int c, int h, int w) {
		return this.data[n][c][h][w];
	}

	public ConvolutionMatrix set(double v) {
		for (int n = 0; n < this.number(); n++) {
			for (int c = 0; c < this.channel(); c++) {
				for (int h = 0; h < this.height(); h++) {
					for (int w = 0; w < this.width(); w++) {
						set(n, c, h, w, v);
					}
				}
			}
		}
		return this;
	}

	public ConvolutionMatrix set(int n, double[][][] v) {
		this.data[n] = v;
		return this;
	}

	public ConvolutionMatrix set(int n, int c, double[][] v) {
		this.data[n][c] = v;
		return this;
	}

	public ConvolutionMatrix set(int n, int c, int h, double[] v) {
		this.data[n][c][h] = v;
		return this;
	}

	public ConvolutionMatrix set(int n, int c, int h, int w, double v) {
		this.data[n][c][h][w] = v;
		return this;
	}

	public ConvolutionMatrix number(int n, double[][][] value) {
		data[n] = value;
		return this;
	}

	public int channel() {
		return this.channel;
	}

	public double[][] channel(int n, int c) {
		return data[n][c];
	}

	public ConvolutionMatrix number(int n, int c, double[][] value) {
		data[n][c] = value;
		return this;
	}

	public int height() {
		return this.height;
	}

	public double[] height(int n, int c, int h) {
		return data[n][c][h];
	}

	public ConvolutionMatrix height(int n, int c, int h, double[] value) {
		data[n][c][h] = value;
		return this;
	}

	public int width() {
		return this.width;
	}

	public double width(int n, int c, int h, int w) {
		return data[n][c][h][w];
	}

	public ConvolutionMatrix width(int n, int c, int h, int w, double value) {
		data[n][c][h][w] = value;
		return this;
	}

	public ConvolutionMatrix pad(int size, double value) {
		ConvolutionMatrix y = new ConvolutionMatrix(this.number(), this.channel(), this.height() + (size * 2),
				this.width() + (size * 2));
		@SuppressWarnings("unchecked")
		CompletableFuture<Void>[] futrues = new CompletableFuture[y.number];
		for (int n = 0; n < y.number(); n++) {
			futrues[n] = padFuture(n, size, value, y);
		}
		try {
			CompletableFuture.allOf(futrues).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return y;
	}

	private CompletableFuture<Void> padFuture(int n, int size, double value, ConvolutionMatrix y) {
		return CompletableFuture.runAsync(() -> {
			for (int c = 0; c < y.channel(); c++) {
				for (int h = 0; h < y.height(); h++) {
					for (int w = 0; w < y.width(); w++) {
						pad(n, size, value, y, c, h, w);
					}
				}
			}
		});
	}

	private void pad(int n, int size, double value, ConvolutionMatrix y, int c, int h, int w) {
		if ((h < size) || (w < size) || ((h - size) >= this.height()) || ((w - size) >= this.width())) {
			y.data[n][c][h][w] = value;
		} else {
			y.data[n][c][h][w] = this.data[n][c][h - size][w - size];
		}
	}

	public ConvolutionMatrix reduce(int size) {
		ConvolutionMatrix y = new ConvolutionMatrix(this.number(), this.channel(), this.height() - (size * 2),
				this.width() - (size * 2));
		System.out.println(this.dimensionToString());
		System.out.println(y.dimensionToString());
		@SuppressWarnings("unchecked")
		CompletableFuture<Void>[] futrues = new CompletableFuture[y.number()];
		for (int n = 0; n < y.number(); n++) {
			futrues[n] = reduceFuture(n, size, y);
		}
		try {
			CompletableFuture.allOf(futrues).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return y;
	}

	private CompletableFuture<Void> reduceFuture(int n, int size, ConvolutionMatrix y) {
		return CompletableFuture.runAsync(() -> {
			for (int c = 0; c < y.channel(); c++) {
				for (int h = 0; h < y.height(); h++) {
					for (int w = 0; w < y.width(); w++) {
						reduce(n, size, y, c, h, w);
					}
				}
			}
		});
	}

	private void reduce(int n, int size, ConvolutionMatrix y, int c, int h, int w) {
		System.out.println(this.data[n][c][h + size][w + size]);
		y.set(n, c, h, w, this.data[n][c][h + size][w + size]);
	}

	public double[][] strip() {
		double[][] value = new double[this.number()][this.channel() * this.height() * this.width()];
		for (int n = 0; n < this.number(); n++) {
			double[] s = new double[this.channel() * this.height() * this.width()];
			int i = 0;
			for (int c = 0; c < this.channel(); c++) {
				for (int h = 0; h < this.height(); h++) {
					for (int w = 0; w < this.width(); w++) {
						s[i++] = data[n][c][h][w];
					}
				}
			}
			value[n] = s;
		}
		return value;
	}

	public double[] stripOfNumber(int number, int fromHeight, int fromWidth, int toHeight, int toWidth) {
		double[] value = new double[this.channel() * (toHeight - fromHeight + 1) * (toWidth - fromWidth + 1)];
		int i = 0;
		for (int c = 0; c < this.channel(); c++) {
			for (int h = fromHeight; h <= toHeight; h++) {
				for (int w = fromWidth; w <= toWidth; w++) {
					value[i++] = data[number][c][h][w];
				}
			}
		}
		return value;
	}

	public double[] stripOfNumber(int number) {
		double[] value = new double[this.channel() * this.height() * this.width()];
		int i = 0;
		for (int c = 0; c < this.channel(); c++) {
			for (int h = 0; h < this.height(); h++) {
				for (int w = 0; w < this.width(); w++) {
					value[i++] = data[number][c][h][w];
				}
			}
		}
		return value;
	}

	public ConvolutionMatrix copy() {
		ConvolutionMatrix o = new ConvolutionMatrix(this.number(), this.channel(), this.height(), this.width());
		for (int n = 0; n < this.number(); n++) {
			for (int c = 0; c < this.channel(); c++) {
				for (int h = 0; h < this.height(); h++) {
					for (int w = 0; w < this.width(); w++) {
						o.set(n, c, h, w, data[n][c][h][w]);
					}
				}
			}
		}
		return o;
	}

	public void visit(Visitor visitor) {
		for (int n = 0; n < this.number(); n++) {
			for (int c = 0; c < this.channel(); c++) {
				for (int h = 0; h < this.height(); h++) {
					for (int w = 0; w < this.width(); w++) {
						visitor.visit(n, c, h, w, this.get(n, c, h, w));
					}
				}
			}
		}
	}

	@FunctionalInterface
	public interface Visitor {

		void visit(int n, int c, int h, int w, double value);

	}

	@Override
	public String toString() {
		return Arrays.deepToString(this.data);
	}

	public String dimensionToString() {
		return this.getClass() + ", n=" + this.number() + ", c=" + this.channel() + ", h=" + this.height() + ", w="
				+ this.height() + ".";
	}

}