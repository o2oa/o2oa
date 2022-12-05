package com.x.base.core.neural.cnn.train;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.x.base.core.neural.cnn.ConvolutionTools;
import com.x.base.core.neural.cnn.dump.Dump;
import com.x.base.core.neural.cnn.layer.Layer;
import com.x.base.core.neural.cnn.loss.Loss;
import com.x.base.core.neural.cnn.loss.SoftmaxWithCrossEntropyError;
import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;

public abstract class TrainNetwork {

	private String name;
	private String description;

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

	public TrainNetwork(String name) {

		this.name = name;
		this.init();

	}

	public abstract void init();

	public TrainNetwork initParameter() {
		return this;
	}

	public ConvolutionMatrix predict(ConvolutionMatrix x) {
		for (Layer layer : layers()) {
			x = layer.forward(x);
		}
		return x;
	}

	public TrainNetwork gradient(ConvolutionMatrix dy) {
		for (int i = layers.size() - 1; i >= 0; i--) {
			dy = layers.get(i).backward(dy);
		}
		return this;
	}

	public double accuracy(ConvolutionMatrix x, ConvolutionMatrix t) {
		ConvolutionMatrix y = this.predict(x);
		int match = 0;
		for (int n = 0; n < y.number(); n++) {
			for (int c = 0; c < y.channel(); c++) {
				for (int h = 0; h < y.height(); h++) {
					if (ConvolutionTools.argmax(y.get(n, c, h)) == ConvolutionTools.argmax(t.get(n, c, h))) {
						match++;
					}
				}
			}
		}
		return (match / (double) (y.number() * y.channel() * y.height()));
	}

	public TrainNetwork update() {
		for (int i = layers().size() - 1; i >= 0; i--) {
			layers().get(i).update();
		}
		return this;
	}

	public static class DumpThread extends Thread {
		private TrainNetwork trainNetwork;

		public DumpThread(TrainNetwork trainNetwork) {
			this.trainNetwork = trainNetwork;
		}

		@Override
		public void run() {
			try {
				Dump dump = new Dump(trainNetwork);
				Path dir = Paths.get("dump");
				Files.createDirectories(dir);
				Path path = dir.resolve(trainNetwork.name() + ".json");
				Files.writeString(path, dump.toJson(), StandardCharsets.UTF_8);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
