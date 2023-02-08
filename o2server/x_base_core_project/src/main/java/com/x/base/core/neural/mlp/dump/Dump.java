package com.x.base.core.neural.mlp.dump;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.x.base.core.neural.mlp.Network;
import com.x.base.core.neural.mlp.layer.Affine;
import com.x.base.core.neural.mlp.layer.BatchNormalization;
import com.x.base.core.neural.mlp.layer.Layer;
import com.x.base.core.neural.mlp.layer.activation.Relu;
import com.x.base.core.neural.mlp.layer.activation.Sigmoid;
import com.x.base.core.neural.mlp.layer.activation.Tanh;
import com.x.base.core.neural.mlp.loss.Loss;
import com.x.base.core.neural.mlp.loss.MeanSquareError;
import com.x.base.core.neural.mlp.loss.SigmoidWithMeanSquareError;
import com.x.base.core.neural.mlp.loss.SoftmaxWithCrossEntropyError;
import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;
import com.x.base.core.neural.mlp.train.TrainNetwork;

public class Dump {

	private String name;
	private String description;
	private int input;
	private int output;
	private int[] hiddens;

	private Map<Integer, Affine> affines = new TreeMap<>();
	private Map<Integer, BatchNormalization> batchNormalizations = new TreeMap<>();
	private Map<Integer, Relu> relus = new TreeMap<>();
	private Map<Integer, Sigmoid> sigmoids = new TreeMap<>();
	private Map<Integer, Tanh> tanhs = new TreeMap<>();

	private MeanSquareError meanSquareError = null;
	private SigmoidWithMeanSquareError sigmoidWithMeanSquareError = null;
	private SoftmaxWithCrossEntropyError softmaxWithCrossEntropyError = null;

	private static Gson gson = new GsonBuilder().setPrettyPrinting()
			.registerTypeAdapter(MultilayerPerceptronMatrix.class, new MultilayerPerceptronMatrixAdapter()).create();
//	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public Dump(TrainNetwork trainNetwork) {
		this.name = trainNetwork.name();
		this.description = trainNetwork.description();
		this.input = trainNetwork.input();
		this.output = trainNetwork.output();
		this.hiddens = trainNetwork.hiddens();
		for (int i = 0; i < trainNetwork.layers().size(); i++) {
			Layer layer = trainNetwork.layers().get(i);
			if (layer instanceof Affine) {
				affines.put(Integer.valueOf(i), (Affine) layer);
			} else if (layer instanceof BatchNormalization) {
				batchNormalizations.put(Integer.valueOf(i), (BatchNormalization) layer);
			} else if (layer instanceof Relu) {
				relus.put(Integer.valueOf(i), (Relu) layer);
			} else if (layer instanceof Sigmoid) {
				sigmoids.put(Integer.valueOf(i), (Sigmoid) layer);
			} else if (layer instanceof Tanh) {
				tanhs.put(Integer.valueOf(i), (Tanh) layer);
			}
		}
		Loss loss = trainNetwork.loss();
		if (null != loss) {
			if (loss instanceof MeanSquareError) {
				this.meanSquareError = (MeanSquareError) loss;
			} else if (loss instanceof SigmoidWithMeanSquareError) {
				this.sigmoidWithMeanSquareError = (SigmoidWithMeanSquareError) loss;
			} else if (loss instanceof SoftmaxWithCrossEntropyError) {
				this.softmaxWithCrossEntropyError = (SoftmaxWithCrossEntropyError) loss;
			}
		}
	}

	private List<Layer> layers() {
		List<Layer> list = new ArrayList<>();
		for (int i = 0; i < sizeOfLayers(); i++) {
			final int j = i;
			affines.entrySet().stream().filter(o -> j == o.getKey()).findFirst().ifPresent(o -> list.add(o.getValue()));
			batchNormalizations.entrySet().stream().filter(o -> j == o.getKey()).findFirst()
					.ifPresent(o -> list.add(o.getValue()));
			relus.entrySet().stream().filter(o -> j == o.getKey()).findFirst().ifPresent(o -> list.add(o.getValue()));
			sigmoids.entrySet().stream().filter(o -> j == o.getKey()).findFirst()
					.ifPresent(o -> list.add(o.getValue()));
			tanhs.entrySet().stream().filter(o -> j == o.getKey()).findFirst().ifPresent(o -> list.add(o.getValue()));
		}
		return list;
	}

	private int sizeOfLayers() {
		return this.affines.size() + this.batchNormalizations.size() + this.relus.size() + this.sigmoids.size()
				+ this.tanhs.size();
	}

	public static Dump formJson(String json) {
		return gson.fromJson(json, Dump.class);
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public Network toNetwork() {
		Network network = new Network();
		network.name(this.name);
		network.description(this.description);
		network.input(this.input);
		network.output(this.output);
		network.layers(this.layers());
		return network;
	}

	public <T extends Network> T toNetwork(Class<T> cls)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		T t = cls.getConstructor().newInstance();
		t.name(this.name);
		t.description(this.description);
		t.input(this.input);
		t.output(this.output);
		t.layers(this.layers());
		return t;
	}

	public TrainNetwork toTrainNetwork() {
		TrainNetwork network = new TrainNetwork(name, input, input, hiddens) {
			@Override
			public void init() {
				// nothing
			}
		};
		network.name(name).description(description).input(input).output(output).hiddens(hiddens).layers(this.layers());
		if (null != this.meanSquareError) {
			network.loss(meanSquareError);
		} else if (null != this.sigmoidWithMeanSquareError) {
			network.loss(this.sigmoidWithMeanSquareError);
		} else if (null != this.softmaxWithCrossEntropyError) {
			network.loss(this.softmaxWithCrossEntropyError);
		}
		return network;
	}

}