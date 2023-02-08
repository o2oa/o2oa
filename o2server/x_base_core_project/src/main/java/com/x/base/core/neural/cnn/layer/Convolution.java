package com.x.base.core.neural.cnn.layer;

import java.util.concurrent.CompletableFuture;

import com.x.base.core.neural.cnn.ConvolutionTools;
import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;
import com.x.base.core.neural.cnn.optimizer.Optimizer;

public class Convolution implements Layer {

	// https://zhuanlan.zhihu.com/p/40951745
	// https://www.cnblogs.com/pinard/p/6494810.html
	private int stride = 1;

	private int pad = 0;

	private int padValue = 0;

	private ConvolutionMatrix x;

	private ConvolutionMatrix w;

	private ConvolutionMatrix b;

	private ConvolutionMatrix dw;

	private ConvolutionMatrix db;

	private Optimizer optimizer;

	public ConvolutionMatrix x() {
		return this.x;
	}

	public int stride() {
		return this.stride;
	}

	public Convolution stride(int stride) {
		this.stride = stride;
		return this;
	}

	public int pad() {
		return this.pad;
	}

	public Convolution pad(int pad) {
		this.pad = pad;
		return this;
	}

	public int padValue() {
		return this.padValue;
	}

	public Convolution padValue(int padValue) {
		this.padValue = padValue;
		return this;
	}

	public Convolution w(ConvolutionMatrix w) {
		this.w = w;
		this.dw = new ConvolutionMatrix(w.number(), w.channel(), w.height(), w.width());
		return this;
	}

	public ConvolutionMatrix w() {
		return this.w;
	}

	public Convolution b(ConvolutionMatrix b) {
		this.b = b;
		this.db = new ConvolutionMatrix(b.number(), b.channel(), b.height(), b.width());
		return this;
	}

	public ConvolutionMatrix b() {
		return this.b;
	}

	public Convolution db(ConvolutionMatrix db) {
		this.db = db;
		return this;
	}

	public ConvolutionMatrix db() {
		return this.db;
	}

	public Convolution dw(ConvolutionMatrix dw) {
		this.dw = dw;
		return this;
	}

	public ConvolutionMatrix dw() {
		return this.dw;
	}

	public Convolution optimizer(Optimizer optimizer) {
		this.optimizer = optimizer;
		return this;
	}

	public Optimizer optimizer() {
		return this.optimizer;
	}

	public ConvolutionMatrix forward(ConvolutionMatrix x) {
		if (this.pad > 0) {
			this.x = x.pad(this.pad, this.padValue);
		} else {
			this.x = x.copy();
		}
		int rh = ((this.x().height() - this.w().height()) / this.stride()) + 1;
		int rw = ((this.x().width() - this.w().width()) / this.stride()) + 1;
		ConvolutionMatrix y = new ConvolutionMatrix(x.number(), this.w().number(), rh, rw);
		@SuppressWarnings("unchecked")
		CompletableFuture<Void>[] futrues = new CompletableFuture[x.number()];
		for (int xn = 0; xn < x.number(); xn++) {
			futrues[xn] = forwardFuture(xn, y);
		}
		try {
			CompletableFuture.allOf(futrues).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return y;
	}

	private CompletableFuture<Void> forwardFuture(int xn, ConvolutionMatrix y) {
		return CompletableFuture.runAsync(() -> {
			for (int wn = 0; wn < this.w().number(); wn++) {
				y.set(xn, wn, ConvolutionTools.add(forwardChannelConv(xn, wn), this.b().get(wn, 0, 0, 0)));
			}
		});
	}

	private double[][] forwardChannelConv(int xn, int wn) {
		double[][] o = null;
		for (int c = 0; c < this.w().channel(); c++) {
			o = ConvolutionTools.sum(o, ConvolutionTools.conv(this.x().get(xn, c), this.w().get(wn, c), this.stride()));
		}
		return o;
	}

	@Override
	public ConvolutionMatrix backward(ConvolutionMatrix y) {
		this.dbBackward(y);
		this.dwBackward(y);
		return this.dxBackward(y);
	}

	private void dbBackward(ConvolutionMatrix y) {
		for (int c = 0; c < y.channel(); c++) {
			double s = 0d;
			for (int n = 0; n < y.number(); n++) {
				s += ConvolutionTools.sum(y.get(n, c));
			}
			this.db().set(c, 0, 0, 0, s);
		}
	}

	private void dwBackward(ConvolutionMatrix y) {
		for (int wn = 0; wn < this.w().number(); wn++) {
			for (int wc = 0; wc < this.w().channel(); wc++) {
				double[][] o = null;
				for (int yn = 0; yn < y.number(); yn++) {
					o = ConvolutionTools.sum(o,
							ConvolutionTools.conv(this.x().get(yn, wc), y.get(yn, wn), this.stride()));
				}
				this.dw().set(wn, wc, o);
			}
		}
	}

	private ConvolutionMatrix dxBackward(ConvolutionMatrix y) {
		y = y.pad(this.w().width() - 1, 0);
		ConvolutionMatrix dx = new ConvolutionMatrix(y.number(), y.channel(), this.x().height(), this.x().width());
		for (int yn = 0; yn < y.number(); yn++) {
			for (int wc = 0; wc < this.w().channel(); wc++) {
				double[][] o = null;
				for (int wn = 0; wn < this.w().number(); wn++) {
					o = ConvolutionTools.sum(o, ConvolutionTools.conv(y.get(yn, wn),
							ConvolutionTools.flip(this.w().get(wn, wc)), this.stride()));
				}
				dx.set(yn, wc, o);
			}
		}
		if (this.pad() > 0) {
			dx = dx.reduce(this.pad());
		}
		return dx;
	}

	@Override
	public void update() {
		optimizer.update(w, dw);
		optimizer.update(b, db);
	}
}
