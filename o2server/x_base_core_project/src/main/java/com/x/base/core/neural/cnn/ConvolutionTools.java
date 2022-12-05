package com.x.base.core.neural.cnn;

import java.util.concurrent.CompletableFuture;

public class ConvolutionTools {

	public static final double DELTA = 1e-10;

	private ConvolutionTools() {
		// nothing
	}

	public static double[][] sum(double[][] x, double[][] y) {
		if (null == x) {
			return y;
		}
		if (null == y) {
			return x;
		}
		double[][] o = new double[x.length][x[0].length];
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				o[i][j] = x[i][j] + y[i][j];
			}
		}
		return o;
	}

	public static double[][] add(double[][] x, double b) {
		double[][] o = new double[x.length][x[0].length];
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				o[i][j] = x[i][j] + b;
			}
		}
		return o;
	}

	public static double[][] conv(double[][] x, double[][] y, int stride) {
		int h = ((x.length - y.length) / stride) + 1;
		int w = ((x[0].length - y[0].length) / stride) + 1;
		double[][] o = new double[h][w];
		@SuppressWarnings("unchecked")
		CompletableFuture<Void>[] futures = new CompletableFuture[h];
		for (int i = 0; i < h; i++) {
			futures[i] = convFuture(x, y, stride, w, o, i);
		}
		try {
			CompletableFuture.allOf(futures).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	private static CompletableFuture<Void> convFuture(double[][] x, double[][] y, int stride, int w, double[][] o,
			int i) {
		return CompletableFuture.runAsync(() -> {
			for (int j = 0; j < w; j++) {
				int xh = i * stride;
				int xw = j * stride;
				double s = 0d;
				for (int yh = 0; yh < y.length; yh++) {
					for (int yw = 0; yw < y[0].length; yw++) {
						s += x[xh + yh][xw + yw] * y[yh][yw];
					}
				}
				o[i][j] = s;
			}
		});
	}

	public static double[][] flip(double[][] x) {
		int h = x.length;
		int w = x[0].length;
		double[][] o = new double[h][w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				o[i][j] = x[h - i - 1][w - j - 1];
			}
		}
		return o;
	}

	public static double[][] dot(double[][] x, double[][] y) {
		double[][] o = new double[x.length][y[0].length];
		@SuppressWarnings("unchecked")
		CompletableFuture<Void>[] futures = new CompletableFuture[o.length];
		for (int i = 0; i < o.length; i++) {
			futures[i] = dotFuture(x, y, o, i);
		}
		try {
			CompletableFuture.allOf(futures).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	private static CompletableFuture<Void> dotFuture(double[][] x, double[][] y, double[][] o, int i) {
		return CompletableFuture.runAsync(() -> {
			for (int j = 0; j < o[0].length; j++) {
				double v = 0d;
				for (int k = 0; k < x[0].length; k++) {
					v += x[i][k] * y[k][j];
				}
				o[i][j] = v;
			}
		});
	}

	public static double[][] transpose(double[][] x) {
		double[][] y = new double[x[0].length][x.length];
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				y[j][i] = x[i][j];
			}
		}
		return y;
	}

	public static double[][] subtract(double[][] x, double[][] y) {
		double[][] o = new double[x.length][x[0].length];
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				o[i][j] = x[i][j] - y[i][j];
			}
		}
		return o;
	}

	public static double sum(double[][] x) {
		double s = 0f;
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				s += x[i][j];
			}
		}
		return s;
	}
	
	public static double max(final double... array) {
		double max = array[0];
		for (int j = 1; j < array.length; j++) {
			if (array[j] > max) {
				max = array[j];
			}
		}
		return max;
	}
	
	public static int argmax(double[] arr) {
		double max = arr[0];
		int idx = 0;
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > max) {
				max = arr[i];
				idx = i;
			}
		}
		return idx;
	}

}
