package com.x.base.core.neural.mlp.layer;

import com.x.base.core.neural.mlp.MultilayerPerceptronTools;
import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;
import com.x.base.core.neural.mlp.optimizer.Adam;
import com.x.base.core.neural.mlp.optimizer.Optimizer;
import com.x.base.core.neural.mlp.optimizer.StochasticGradientDescent;

public class BatchNormalization implements Layer {

	// https://www.pianshen.com/article/638854876/

	private Adam optimizerGammaAdam;

	private Adam optimizerBetaAdam;

	private StochasticGradientDescent optimizerGammaStochasticGradientDescent;

	private StochasticGradientDescent optimizerBetaStochasticGradientDescent;

	private boolean initialized;

	private MultilayerPerceptronMatrix gamma;

	private MultilayerPerceptronMatrix beta;

	private MultilayerPerceptronMatrix xmu;

	private MultilayerPerceptronMatrix variance;

	private MultilayerPerceptronMatrix sqrtvar;

	private MultilayerPerceptronMatrix ivar;

	private MultilayerPerceptronMatrix xhat;

	private MultilayerPerceptronMatrix dgamma;

	private MultilayerPerceptronMatrix dbeta;

	public BatchNormalization() {
		this.optimizerGammaAdam = new Adam();
		this.optimizerBetaAdam = new Adam();
	}

	private void init(MultilayerPerceptronMatrix x) {
		this.gamma = new MultilayerPerceptronMatrix(1, x.column());
		this.gamma.set(1f);
		this.beta = new MultilayerPerceptronMatrix(1, x.column());
		this.beta.set(0f);
	}

	public Optimizer optimizerGamma() {
		if (null != optimizerGammaAdam) {
			return optimizerGammaAdam;
		} else if (null != optimizerGammaStochasticGradientDescent) {
			return optimizerGammaStochasticGradientDescent;
		}
		return null;
	}

	public Optimizer optimizerBeta() {
		if (null != optimizerBetaAdam) {
			return optimizerBetaAdam;
		} else if (null != optimizerBetaStochasticGradientDescent) {
			return optimizerBetaStochasticGradientDescent;
		}
		return null;
	}

	@Override
	public void update() {

		this.optimizerGamma().update(this.gamma, this.dgamma);

		this.optimizerBeta().update(this.beta, this.dbeta);

	}

	@Override
	public MultilayerPerceptronMatrix forward(MultilayerPerceptronMatrix x) {
		// 如果单条数据那么直接通过
		if (x.row() < 2) {
			return x;
		}
		if (!this.initialized) {
			this.init(x);
			this.initialized = true;
		}
		this.xmu = this.xmu(x);
		MultilayerPerceptronMatrix sq = this.sq(this.xmu);
		this.variance = this.variance(sq);
		this.sqrtvar = this.sqrtvar(variance);
		this.ivar = this.ivar(sqrtvar);
		this.xhat = this.xhat(this.xmu, this.ivar);
		MultilayerPerceptronMatrix gammax = this.gammax(this.gamma, this.xhat);
		return this.out(gammax, this.beta);
	}

	private MultilayerPerceptronMatrix xmu(MultilayerPerceptronMatrix x) {
		MultilayerPerceptronMatrix m = x.copy();
		for (int i = 0; i < m.column(); i++) {
			float mean = m.meanOfColumn(i);
			for (int j = 0; j < m.row(); j++) {
				m.set(j, i, m.get(j, i) - mean);
			}
		}
		return m;
	}

	private MultilayerPerceptronMatrix sq(MultilayerPerceptronMatrix xmu) {
		MultilayerPerceptronMatrix m = xmu.copy();
		m.visit((r, c, v) -> m.set(r, c, v * v));
		return m;
	}

	private MultilayerPerceptronMatrix variance(MultilayerPerceptronMatrix sq) {
		MultilayerPerceptronMatrix m = new MultilayerPerceptronMatrix(1, sq.column());
		for (int i = 0; i < sq.column(); i++) {
			m.set(0, i, sq.meanOfColumn(i));
		}
		return m;
	}

	private MultilayerPerceptronMatrix sqrtvar(MultilayerPerceptronMatrix variance) {
		MultilayerPerceptronMatrix m = variance.copy();
		m.visit((r, c, v) -> m.set(r, c, (float) Math.sqrt(v + MultilayerPerceptronTools.DELTA)));
		return m;
	}

	private MultilayerPerceptronMatrix ivar(MultilayerPerceptronMatrix sqrtvar) {
		MultilayerPerceptronMatrix m = sqrtvar.copy();
		m.visit((r, c, v) -> m.set(r, c, 1f / v));
		return m;
	}

	private MultilayerPerceptronMatrix xhat(MultilayerPerceptronMatrix xmu, MultilayerPerceptronMatrix ivar) {
		MultilayerPerceptronMatrix m = xmu.copy();
		for (int i = 0; i < m.column(); i++) {
			float v = ivar.get(0, i);
			for (int j = 0; j < m.row(); j++) {
				m.set(j, i, m.get(j, i) * v);
			}
		}
		return m;
	}

	private MultilayerPerceptronMatrix gammax(MultilayerPerceptronMatrix gamma, MultilayerPerceptronMatrix xhat) {
		MultilayerPerceptronMatrix m = xhat.copy();
		for (int i = 0; i < m.column(); i++) {
			float v = gamma.get(0, i);
			for (int j = 0; j < m.row(); j++) {
				m.set(j, i, m.get(j, i) * v);
			}
		}
		return m;
	}

	private MultilayerPerceptronMatrix out(MultilayerPerceptronMatrix gammax, MultilayerPerceptronMatrix beta) {
		for (int i = 0; i < beta.column(); i++) {
			float v = beta.get(0, i);
			for (int j = 0; j < gammax.row(); j++) {
				gammax.set(j, i, gammax.get(j, i) + v);
			}
		}
		return gammax;
	}

	@Override
	public MultilayerPerceptronMatrix backward(MultilayerPerceptronMatrix dout) {
		// 如果单条数据那么直接通过
		if (dout.row() < 2) {
			return dout;
		}
		this.dbeta = this.dbeta(dout);
		MultilayerPerceptronMatrix dgammax = dout.copy();
		this.dgamma = this.dgamma(dgammax, this.xhat);
		MultilayerPerceptronMatrix dxhat = this.dxhat(dgammax, this.gamma);
		MultilayerPerceptronMatrix divar = this.divar(dxhat, this.xmu);
		MultilayerPerceptronMatrix dxmu1 = this.dxmu1(dxhat, this.ivar);
		MultilayerPerceptronMatrix dsqrtvar = this.dsqrtvar(this.sqrtvar, divar);
		MultilayerPerceptronMatrix dvar = this.dvar(this.variance, dsqrtvar);
		MultilayerPerceptronMatrix dsq = this.dsq(dout.row(), dout.column(), dvar);
		MultilayerPerceptronMatrix dxmu2 = this.dxmu2(this.xmu, dsq);
		MultilayerPerceptronMatrix dx1 = this.dx1(dxmu1, dxmu2);
		MultilayerPerceptronMatrix dmu = this.dmu(dx1);
		MultilayerPerceptronMatrix dx2 = this.dx2(dout.row(), dout.column(), dmu);
		return this.dx(dx1, dx2);
	}

	private MultilayerPerceptronMatrix dbeta(MultilayerPerceptronMatrix dout) {
		return dout.sumAsColumn();
	}

	private MultilayerPerceptronMatrix dgamma(MultilayerPerceptronMatrix dgammax, MultilayerPerceptronMatrix xhat) {
		MultilayerPerceptronMatrix m = dgammax.copy();
		m.visit((r, c, v) -> m.set(r, c, v * xhat.get(r, c)));
		return m.sumAsColumn();
	}

	private MultilayerPerceptronMatrix dxhat(MultilayerPerceptronMatrix dgammax, MultilayerPerceptronMatrix gamma) {
		MultilayerPerceptronMatrix m = dgammax.copy();
		for (int i = 0; i < m.column(); i++) {
			float value = gamma.get(0, i);
			for (int j = 0; j < m.row(); j++) {
				m.set(j, i, m.get(j, i) * value);
			}
		}
		return m;
	}

	private MultilayerPerceptronMatrix divar(MultilayerPerceptronMatrix dxhat, MultilayerPerceptronMatrix xmu) {
		MultilayerPerceptronMatrix m = dxhat.copy();
		m.visit((r, c, v) -> m.set(r, c, v * xmu.get(r, c)));
		return m.sumAsColumn();
	}

	private MultilayerPerceptronMatrix dxmu1(MultilayerPerceptronMatrix dxhat, MultilayerPerceptronMatrix ivar) {
		MultilayerPerceptronMatrix m = dxhat.copy();
		for (int i = 0; i < m.column(); i++) {
			float value = ivar.get(0, i);
			for (int j = 0; j < m.row(); j++) {
				m.set(j, i, m.get(j, i) * value);
			}
		}
		return m;
	}

	private MultilayerPerceptronMatrix dsqrtvar(MultilayerPerceptronMatrix sqrtvar, MultilayerPerceptronMatrix divar) {
		MultilayerPerceptronMatrix m = sqrtvar.copy();
		m.visit((r, c, v) -> m.set(r, c, (-1.0f / (v * v)) * divar.get(r, c)));
		return m;
	}

	private MultilayerPerceptronMatrix dvar(MultilayerPerceptronMatrix var, MultilayerPerceptronMatrix dsqrtvar) {
		MultilayerPerceptronMatrix m = var.copy();
		m.visit((r, c, v) -> m.set(r, c, (0.5f / (float) ((Math.sqrt(v + MultilayerPerceptronTools.DELTA))) * dsqrtvar.get(r, c))));
		return m;

	}

	private MultilayerPerceptronMatrix dsq(int n, int d, MultilayerPerceptronMatrix dvar) {
		MultilayerPerceptronMatrix m = new MultilayerPerceptronMatrix(n, d);
		m.set(1f);
		for (int i = 0; i < m.column(); i++) {
			float value = dvar.get(0, i);
			for (int j = 0; j < m.row(); j++) {
				m.set(j, i, (1.0f / (float) n) * value);
			}
		}
		return m;
	}

	private MultilayerPerceptronMatrix dxmu2(MultilayerPerceptronMatrix xmu, MultilayerPerceptronMatrix dsq) {
		MultilayerPerceptronMatrix m = xmu.copy();
		m.visit((r, c, v) -> m.set(r, c, 2 * v * dsq.get(r, c)));
		return m;
	}

	private MultilayerPerceptronMatrix dx1(MultilayerPerceptronMatrix dxmu1, MultilayerPerceptronMatrix dxmu2) {
		MultilayerPerceptronMatrix m = dxmu1.copy();
		m.visit((r, c, v) -> m.set(r, c, v + dxmu2.get(r, c)));
		return m;
	}

	private MultilayerPerceptronMatrix dmu(MultilayerPerceptronMatrix dx1) {
		MultilayerPerceptronMatrix m = new MultilayerPerceptronMatrix(1, dx1.column());
		for (int i = 0; i < dx1.column(); i++) {
			m.set(0, i, -dx1.sumOfColumn(i));
		}
		return m;
	}

	private MultilayerPerceptronMatrix dx2(int n, int d, MultilayerPerceptronMatrix dmu) {
		MultilayerPerceptronMatrix m = new MultilayerPerceptronMatrix(n, d);
		m.set(1.0f);
		for (int i = 0; i < m.column(); i++) {
			float value = dmu.get(0, i);
			for (int j = 0; j < m.row(); j++) {
				m.set(j, i, (1.0f / n) * value);
			}
		}
		return m;
	}

	private MultilayerPerceptronMatrix dx(MultilayerPerceptronMatrix dx1, MultilayerPerceptronMatrix dx2) {
		MultilayerPerceptronMatrix m = dx1.copy();
		m.visit((r, c, v) -> m.set(r, c, v + dx2.get(r, c)));
		return m;
	}
}
