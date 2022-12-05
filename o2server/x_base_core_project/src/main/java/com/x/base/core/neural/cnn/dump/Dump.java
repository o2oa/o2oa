package com.x.base.core.neural.cnn.dump;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.x.base.core.neural.cnn.Network;
import com.x.base.core.neural.cnn.layer.Affine;
import com.x.base.core.neural.cnn.layer.Convolution;
import com.x.base.core.neural.cnn.layer.Layer;
import com.x.base.core.neural.cnn.layer.Pooling;
import com.x.base.core.neural.cnn.layer.activation.Relu;
import com.x.base.core.neural.cnn.loss.Loss;
import com.x.base.core.neural.cnn.loss.SoftmaxWithCrossEntropyError;
import com.x.base.core.neural.cnn.train.TrainNetwork;

public class Dump {

	private String name;
	private String description;

	private Map<Integer, Convolution> convolutions = new TreeMap<>();
	private Map<Integer, Pooling> poolings = new TreeMap<>();
	private Map<Integer, Relu> relus = new TreeMap<>();
	private Map<Integer, Affine> affines = new TreeMap<>();

	private SoftmaxWithCrossEntropyError softmaxWithCrossEntropyError = null;

	public Dump(TrainNetwork trainNetwork) {
		this.name = trainNetwork.name();
		this.description = trainNetwork.description();
		for (int i = 0; i < trainNetwork.layers().size(); i++) {
			Layer layer = trainNetwork.layers().get(i);
			if (layer instanceof Convolution) {
				convolutions.put(Integer.valueOf(i), (Convolution) layer);
			} else if (layer instanceof Pooling) {
				poolings.put(Integer.valueOf(i), (Pooling) layer);
			} else if (layer instanceof Relu) {
				relus.put(Integer.valueOf(i), (Relu) layer);
			} else if (layer instanceof Affine) {
				affines.put(Integer.valueOf(i), (Affine) layer);
			}
		}
		Loss loss = trainNetwork.loss();
		if (loss instanceof SoftmaxWithCrossEntropyError) {
			this.softmaxWithCrossEntropyError = (SoftmaxWithCrossEntropyError) loss;
		}
	}

	private List<Layer> layers() {
		List<Layer> list = new ArrayList<>();
		for (int i = 0; i < sizeOfLayers(); i++) {
			final int j = i;
			convolutions.entrySet().stream().filter(o -> j == o.getKey()).findFirst()
					.ifPresent(o -> list.add(o.getValue()));
			poolings.entrySet().stream().filter(o -> j == o.getKey()).findFirst()
					.ifPresent(o -> list.add(o.getValue()));
			relus.entrySet().stream().filter(o -> j == o.getKey()).findFirst().ifPresent(o -> list.add(o.getValue()));
			affines.entrySet().stream().filter(o -> j == o.getKey()).findFirst().ifPresent(o -> list.add(o.getValue()));
		}
		return list;
	}

	private int sizeOfLayers() {
		return this.convolutions.size() + this.poolings.size() + this.relus.size() + this.affines.size();
	}

	public static Dump formJson(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, Dump.class);
	}

	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public Network toNetwork() {
		Network network = new Network();
		network.name(this.name).description(this.description);
		network.layers(this.layers());
		return network;
	}

	public TrainNetwork toTrainNetwork() {
		TrainNetwork network = new TrainNetwork(name) {
			@Override
			public void init() {
				// nothing
			}
		};
		network.name(name).description(description).layers(this.layers());
		if (null != this.softmaxWithCrossEntropyError) {
			network.loss(this.softmaxWithCrossEntropyError);
		}
		return network;
	}

}