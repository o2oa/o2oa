package com.x.base.core.neural.mlp.matrix;

import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;

public class MultilayerPerceptronMatrix {
	private float[][] data;
	private int row;
	private int column;

	public MultilayerPerceptronMatrix(int row, int column) {
		this.row = row;
		this.column = column;
		this.data = new float[row][column];
	}

	public int row() {
		return this.row;
	}

	public float[] row(int r) {
		return this.data[r].clone();
	}

	public MultilayerPerceptronMatrix row(int r, float[] row) {
		this.data[r] = row;
		return this;
	}

	public int column() {
		return this.column;
	}

	public float[] column(int c) {
		float[] v = new float[this.row];
		for (int i = 0; i < this.row; i++) {
			v[i] = data[i][c];
		}
		return v;
	}

	public MultilayerPerceptronMatrix column(int c, float[] column) {
		for (int i = 0; i < row(); i++) {
			data[i][c] = column[i];
		}
		return this;
	}

	public MultilayerPerceptronMatrix dot1(MultilayerPerceptronMatrix y) {
		MultilayerPerceptronMatrix result = new MultilayerPerceptronMatrix(this.row, y.column);
		for (int r = 0; r < result.row; r++) {
			for (int c = 0; c < result.column; c++) {
				float v = 0f;
				for (int i = 0; i < this.column; i++) {
					v += this.get(r, i) * y.get(i, c);
				}
				result.set(r, c, v);
			}
		}
		return result;
	}

	public MultilayerPerceptronMatrix dot(MultilayerPerceptronMatrix y) {
		MultilayerPerceptronMatrix result = new MultilayerPerceptronMatrix(this.row, y.column);
		@SuppressWarnings("unchecked")
		CompletableFuture<Void>[] futrues = new CompletableFuture[result.row()];
		for (int r = 0; r < result.row(); r++) {
			futrues[r] = future(r, y, result);
		}
		try {
			CompletableFuture.allOf(futrues).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private CompletableFuture<Void> future(int r, MultilayerPerceptronMatrix y, MultilayerPerceptronMatrix result) {
		return CompletableFuture.runAsync(() -> {
			for (int c = 0; c < result.column; c++) {
				float v = 0f;
				for (int i = 0; i < this.column; i++) {
					v += this.get(r, i) * y.get(i, c);
				}
				result.set(r, c, v);
			}
		});
	}

	public float get(int r, int c) {
		return data[r][c];
	}

	public MultilayerPerceptronMatrix set(int r, int c, float v) {
		data[r][c] = v;
		return this;
	}

	public MultilayerPerceptronMatrix set(float v) {
		for (int i = 0; i < row(); i++) {
			for (int j = 0; j < column(); j++) {
				data[i][j] = v;
			}
		}
		return this;
	}

	public MultilayerPerceptronMatrix copy() {
		MultilayerPerceptronMatrix n = new MultilayerPerceptronMatrix(this.row, this.column);
		for (int r = 0; r < row(); r++) {
			for (int c = 0; c < column(); c++) {
				n.set(r, c, this.get(r, c));
			}
		}
		return n;
	}

	public MultilayerPerceptronMatrix transpose() {
		MultilayerPerceptronMatrix n = new MultilayerPerceptronMatrix(this.column, this.row);
		for (int i = 0; i < row(); i++) {
			for (int j = 0; j < column(); j++) {
				n.set(j, i, this.get(i, j));
			}
		}
		return n;
	}

	public MultilayerPerceptronMatrix sumAsColumn() {
		MultilayerPerceptronMatrix m = new MultilayerPerceptronMatrix(1, this.column);
		for (int i = 0; i < this.column; i++) {
			float v = 0f;
			for (int j = 0; j < this.row; j++) {
				v += this.get(j, i);
			}
			m.set(0, i, v);
		}
		return m;
	}

	public void visit(Visitor visitor) {
		for (int i = 0; i < this.row; i++) {
			for (int j = 0; j < this.column; j++) {
				visitor.visit(i, j, this.get(i, j));
			}
		}
	}

	public float meanOfColumn(int c) {
		return this.sumOfColumn(c) / (float) this.row;
	}

	public float sumOfColumn(int c) {
		float v = 0;
		for (int i = 0; i < this.row; i++) {
			v += data[i][c];
		}
		return v;
	}

	public MultilayerPerceptronMatrix sigmoid() {
		MultilayerPerceptronMatrix m = this.copy();
		m.visit((r, c, v) -> m.set(r, c, (float) (1 / (1 + Math.exp(-v)))));
		return m;
	}

	public MultilayerPerceptronMatrix add(MultilayerPerceptronMatrix y) {
		MultilayerPerceptronMatrix m = new MultilayerPerceptronMatrix(row(), column());
		this.visit((r, c, v) -> m.set(r, c, this.get(r, c) + y.get(r, c)));
		return m;
	}

	public MultilayerPerceptronMatrix subtract(MultilayerPerceptronMatrix y) {
		MultilayerPerceptronMatrix m = new MultilayerPerceptronMatrix(row(), column());
		this.visit((r, c, v) -> m.set(r, c, this.get(r, c) - y.get(r, c)));
		return m;
	}

	@FunctionalInterface
	public interface Visitor {

		void visit(int row, int column, float value);

	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
