package com.x.base.core.neural.mlp;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public class DataSet<E extends Data> extends ArrayList<E> implements com.x.base.core.neural.DataSet<E> {

	private static final long serialVersionUID = 1L;

	public DataSet<E> sub(int fromIndex, int toIndex) {
		DataSet<E> set = new DataSet<>();
		for (int i = fromIndex; i < toIndex; i++) {
			set.add(this.get(i));
		}
		return set;
	}

	public DataSet<E> filter(int label) {
		DataSet<E> set = new DataSet<>();
		for (E d : this) {
			if (MultilayerPerceptronTools.argmax(d.label()) == label) {
				set.add(d);
			}
		}
		return set;
	}

	public DataSet<E> choice(int... arr) {
		DataSet<E> set = new DataSet<>();
		for (int i : arr) {
			set.add(this.get(i));
		}
		return set;
	}

	public DataSet<E> choice(List<Integer> list) {
		DataSet<E> set = new DataSet<>();
		for (int i : list) {
			set.add(this.get(i));
		}
		return set;
	}

	public DataSet<E> randomChoice(int size) {
		int[] ints = MultilayerPerceptronTools.uniqueRandomInts(size, 0, this.size());
		DataSet<E> set = new DataSet<>();
		for (int i : ints) {
			set.add(this.get(i));
		}
		return set;
	}

	public MultilayerPerceptronMatrix input() {
		MultilayerPerceptronMatrix x = new MultilayerPerceptronMatrix(this.size(), this.get(0).inputSize());
		for (int i = 0; i < x.row(); i++) {
			x.row(i, this.get(i).input());
		}
		return x;
	}

	public MultilayerPerceptronMatrix label() {
		MultilayerPerceptronMatrix x = new MultilayerPerceptronMatrix(this.size(), this.get(0).labelSize());
		for (int i = 0; i < x.row(); i++) {
			x.row(i, this.get(i).label());
		}
		return x;
	}

}