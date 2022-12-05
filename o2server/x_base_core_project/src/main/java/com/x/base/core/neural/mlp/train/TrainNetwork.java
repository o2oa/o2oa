package com.x.base.core.neural.mlp.train;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.x.base.core.neural.mlp.MultilayerPerceptronTools;
import com.x.base.core.neural.mlp.layer.Affine;
import com.x.base.core.neural.mlp.layer.Layer;
import com.x.base.core.neural.mlp.layer.activation.Relu;
import com.x.base.core.neural.mlp.loss.Loss;
import com.x.base.core.neural.mlp.loss.SoftmaxWithCrossEntropyError;
import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public abstract class TrainNetwork {

	private String name;

	private String description;

	private int input;

	private int output;

	private int[] hiddens;

	private List<Layer> layers = new ArrayList<>();

	private Loss loss = new SoftmaxWithCrossEntropyError();

	public String name() {
		return Objects.toString(this.name, "");
	}

	public TrainNetwork name(String name) {
		this.name = name;
		return this;
	}

	public String description() {
		return Objects.toString(this.description, "");
	}

	public TrainNetwork description(String description) {
		this.description = description;
		return this;
	}

	public int input() {
		return this.input;
	}

	public TrainNetwork input(int input) {
		this.input = input;
		return this;
	}

	public int output() {
		return this.output;
	}

	public TrainNetwork output(int output) {
		this.output = output;
		return this;
	}

	public int[] hiddens() {
		return this.hiddens;
	}

	public TrainNetwork hiddens(int[] hiddens) {
		this.hiddens = hiddens;
		return this;
	}

	public List<Layer> layers() {
		return this.layers;
	}

	public void layers(List<Layer> list) {
		this.layers = list;
	}

	public Loss loss() {
		return this.loss;
	}

	public TrainNetwork loss(Loss loss) {
		this.loss = loss;
		return this;
	}

	public TrainNetwork() {

	}

	public TrainNetwork(String name, int input, int output, int... hiddens) {

		this.name = name;
		this.input = input;
		this.output = output;
		this.hiddens = hiddens;

		this.init();

	}

	public abstract void init();

	public TrainNetwork defaultInit() {
		int[] arr = new int[hiddens.length + 2];
		arr[0] = input;
		for (int i = 0; i < hiddens.length; i++) {
			arr[i + 1] = hiddens[i];
		}

		arr[hiddens.length + 1] = output;
		for (int i = 0; i < arr.length - 1; i++) {
			Affine affine = new Affine("affine" + String.format("%02d", i), arr[i], arr[i + 1]);
			layers.add(affine);
			layers.add(new Relu());
		}
		return this;
	}

	public TrainNetwork initParameter() {
		return this;
	}

	public List<Affine> listAffine() {
		List<Affine> list = new ArrayList<>();
		for (Layer layer : this.layers) {
			if (layer instanceof Affine) {
				list.add((Affine) layer);
			}
		}
		return list;
	}

	public MultilayerPerceptronMatrix predict(MultilayerPerceptronMatrix x) {
		for (Layer layer : layers) {
			x = layer.forward(x);
		}
		return x;
	}

	public TrainNetwork gradient(MultilayerPerceptronMatrix dy) {
		for (int i = layers.size() - 1; i >= 0; i--) {
			dy = layers.get(i).backward(dy);
		}
		return this;
	}

	public float accuracy(MultilayerPerceptronMatrix x, MultilayerPerceptronMatrix t) {
		MultilayerPerceptronMatrix y = this.predict(x);
		int match = 0;
		for (int r = 0; r < y.row(); r++) {
			if (MultilayerPerceptronTools.argmax(y.row(r)) == MultilayerPerceptronTools.argmax(t.row(r))) {
				match++;
			}
		}
		return (match / (float) y.row());
	}

	public void update() {
		for (Layer layer : this.layers()) {
			layer.update();
		}
	}

}
