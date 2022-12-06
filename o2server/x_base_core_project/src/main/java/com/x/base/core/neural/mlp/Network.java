package com.x.base.core.neural.mlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.x.base.core.neural.mlp.layer.Layer;
import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public class Network {

	private String name;

	private String description;

	private int input;

	private int output;

	private List<Layer> layers = new ArrayList<>();

	public String name() {
		return Objects.toString(this.name, "");
	}

	public void name(String name) {
		this.name = name;
	}

	public String description() {
		return Objects.toString(this.description, "");
	}

	public void description(String description) {
		this.description = description;
	}

	public int input() {
		return this.input;
	}

	public void input(int input) {
		this.input = input;
	}

	public int output() {
		return this.output;
	}

	public void output(int output) {
		this.output = output;
	}

	public List<Layer> layers() {
		return this.layers;
	}

	public void layers(List<Layer> list) {
		this.layers = list;
	}

	public MultilayerPerceptronMatrix predict(MultilayerPerceptronMatrix x) {
		for (Layer layer : layers) {
			x = layer.forward(x);
		}
		return x;
	}

	

}