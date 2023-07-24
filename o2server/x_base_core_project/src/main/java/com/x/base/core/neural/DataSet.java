package com.x.base.core.neural;

import java.util.List;

import com.x.base.core.neural.mlp.Data;

public interface DataSet<E extends Data> {

	public DataSet<E> sub(int fromIndex, int toIndex);

	public DataSet<E> filter(int label);

	public DataSet<E> choice(int... arr);

	public DataSet<E> choice(List<Integer> list);

	public DataSet<E> randomChoice(int size);

}
