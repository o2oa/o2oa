package com.x.base.core.neural.mlp.layer;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;
import com.x.base.core.neural.mlp.optimizer.Adam;
import com.x.base.core.neural.mlp.optimizer.Optimizer;
import com.x.base.core.neural.mlp.optimizer.StochasticGradientDescent;

public class Affine implements Layer {

	private String name;

	private MultilayerPerceptronMatrix w;

	private MultilayerPerceptronMatrix b;

	private transient MultilayerPerceptronMatrix dw;

	private transient MultilayerPerceptronMatrix db;

	private transient MultilayerPerceptronMatrix x;

	private int input;

	private int output;

	private Adam owAdam = new Adam();

	private Adam obAdam = new Adam();

	private StochasticGradientDescent owStochasticGradientDescent = null;

	private StochasticGradientDescent obStochasticGradientDescent = null;

	public Affine(String name, int input, int output) {
		this.name = name;
		this.input = input;
		this.output = output;
		this.w = new MultilayerPerceptronMatrix(input, output);
		this.b = new MultilayerPerceptronMatrix(1, output);
		Random random = new SecureRandom();
		// 初始化Relu激活函数
		float factor = (float) Math.sqrt(2.0 / (float) input);
		w.visit((r, c, v) -> w.set(r, c, random.nextFloat() * factor));
		b.set(0f);
		this.dw = new MultilayerPerceptronMatrix(input, output);
		this.db = new MultilayerPerceptronMatrix(1, output);
	}

	public int input() {
		return this.input;
	}

	public int output() {
		return this.output;
	}

	public Affine(String name, int input, int output, Optimizer ow, Optimizer ob) {
		this(name, input, output);
		if (ow instanceof StochasticGradientDescent) {
			this.owStochasticGradientDescent = (StochasticGradientDescent) ow;
			this.owAdam = null;
		} else if (ow instanceof Adam) {
			this.owStochasticGradientDescent = null;
			this.owAdam = (Adam) ow;
		}
		if (ob instanceof StochasticGradientDescent) {
			this.obStochasticGradientDescent = (StochasticGradientDescent) ob;
			this.obAdam = null;
		} else if (ob instanceof Adam) {
			this.obStochasticGradientDescent = null;
			this.obAdam = (Adam) ob;
		}
	}

	public MultilayerPerceptronMatrix forward(MultilayerPerceptronMatrix x) {
		this.x = x;
		MultilayerPerceptronMatrix y = x.dot(this.w);
		y.visit((r, c, v) -> y.set(r, c, v + b.get(0, c)));
		return y;
	}

	public MultilayerPerceptronMatrix backward(MultilayerPerceptronMatrix dy) {
		this.dw = this.x.transpose().dot(dy);
		this.db = dy.sumAsColumn();
		return dy.dot(this.w.transpose());
	}

	public void update() {
		this.ow().update(w, dw);
		this.ob().update(b, db);
	}

	public Optimizer ow() {
		if (null != owAdam) {
			return owAdam;
		} else if (null != owStochasticGradientDescent) {
			return owStochasticGradientDescent;
		} else {
			return null;
		}
	}

	public Optimizer ob() {
		if (null != obAdam) {
			return obAdam;
		} else if (null != obStochasticGradientDescent) {
			return obStochasticGradientDescent;
		} else {
			return null;
		}
	}

	public MultilayerPerceptronMatrix w() {
		return this.w;
	}

	public Affine w(MultilayerPerceptronMatrix w) {
		this.w = w;
		return this;
	}

	public MultilayerPerceptronMatrix b() {
		return this.b;
	}

	public Affine b(MultilayerPerceptronMatrix b) {
		this.b = b;
		return this;
	}

	public MultilayerPerceptronMatrix dw() {
		return this.dw;
	}

	public MultilayerPerceptronMatrix db() {
		return this.db;
	}

	@Override
	public String name() {
		return Objects.toString(this.name, "");
	}
}
