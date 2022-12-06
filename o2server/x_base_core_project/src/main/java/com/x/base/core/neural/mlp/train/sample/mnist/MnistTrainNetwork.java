package com.x.base.core.neural.mlp.train.sample.mnist;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import com.x.base.core.neural.mlp.dump.Dump;
import com.x.base.core.neural.mlp.layer.Affine;
import com.x.base.core.neural.mlp.layer.activation.Relu;
import com.x.base.core.neural.mlp.loss.SoftmaxWithCrossEntropyError;
import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;
import com.x.base.core.neural.mlp.train.TrainNetwork;
import com.x.base.core.neural.mlp.train.sample.mnist.MNIST.DataSet;

public class MnistTrainNetwork {

	public static void main(String... args) throws Exception {

		TrainNetwork network = new TrainNetwork("MNIST", 784, 10, 100, 100) {
			@Override
			public void init() {
				int[] arr = new int[hiddens().length + 2];
				arr[0] = input();
				for (int i = 0; i < hiddens().length; i++) {
					arr[i + 1] = hiddens()[i];
				}
				arr[hiddens().length + 1] = output();
				for (int i = 0; i < arr.length - 1; i++) {
					Affine affine = new Affine("affine" + String.format("%02d", i), arr[i], arr[i + 1]);
					layers().add(affine);
					// layers().add(new BatchNormalization());
					if (i != (arr.length - 2)) {
						layers().add(new Relu());
					}
				}
				loss(new SoftmaxWithCrossEntropyError());
			}
		};

		MNIST mnist = MNIST.init();
		DataSet trainSet = mnist.trainDataSet(true);
		DataSet testSet = mnist.testDataSet(true);

		for (int i = 0; i < 600 * 10000; i++) {
			DataSet set = trainSet.randomChoice(100);
			MultilayerPerceptronMatrix y = network.predict(set.input());
			network.loss().forward(y, set.label());
			MultilayerPerceptronMatrix dy = network.loss().backward();
			network.gradient(dy).update();
			if (i % 600 == 0) {
				DumpThread thread = new DumpThread(network, testSet);
				thread.start();
			}
		}
	}

	public static class DumpThread extends Thread {
		private TrainNetwork trainNetwork;
		private DataSet testSet;

		public DumpThread(TrainNetwork trainNetwork, DataSet testSet) {
			this.trainNetwork = trainNetwork;
			this.testSet = testSet;
		}

		@Override
		public void run() {
			try {
				DataSet test = testSet.randomChoice(100);
				double loss = trainNetwork.loss().forward(trainNetwork.predict(test.input()), test.label());
				double accuracy = trainNetwork.accuracy(test.input(), test.label());
				System.out.println(
						trainNetwork.name() + "(" + (new Date()) + ")-> loss:" + loss + ", accuracy:" + accuracy + ".");
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
