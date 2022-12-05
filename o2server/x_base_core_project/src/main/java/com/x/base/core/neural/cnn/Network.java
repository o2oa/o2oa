package com.x.base.core.neural.cnn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.x.base.core.neural.cnn.layer.Layer;
import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;

public class Network {

	private String name;

	private String description;

	private List<Layer> layers = new ArrayList<>();

	public String description() {
		return Objects.toString(this.description, "");
	}

	public Network description(String description) {
		this.description = description;
		return this;
	}

	public String name() {
		return Objects.toString(this.name, "");
	}

	public Network name(String name) {
		this.name = name;
		return this;
	}

	public List<Layer> layers() {
		return this.layers;
	}

	public void layers(List<Layer> list) {
		this.layers = list;
	}

	public ConvolutionMatrix predict(ConvolutionMatrix x) {
		for (Layer layer : layers) {
			x = layer.forward(x);
		}
		return x;
	}
}
