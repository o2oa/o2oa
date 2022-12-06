package com.x.base.core.neural.cnn.train;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import com.x.base.core.neural.cnn.layer.Affine;
import com.x.base.core.neural.cnn.layer.Convolution;
import com.x.base.core.neural.cnn.layer.Pooling;
import com.x.base.core.neural.cnn.layer.activation.Relu;
import com.x.base.core.neural.cnn.loss.Loss;
import com.x.base.core.neural.cnn.loss.SoftmaxWithCrossEntropyError;
import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;
import com.x.base.core.neural.cnn.mnist.MNIST;
import com.x.base.core.neural.cnn.mnist.MNIST.DataSet;
import com.x.base.core.neural.cnn.optimizer.StochasticGradientDescent;

public class Training {

	private static Random random = new SecureRandom();
	private static TrainNetwork network = null;

	public static void main(String... args) throws Exception {
		network = new TrainNetwork("MNIST") {
			@Override
			public void init() {
				this.layers().add(concreteConv01());
				this.layers().add(concreteRelu02());
				this.layers().add(concretePooling03());
				this.layers().add(concreteAffine04());
				this.layers().add(concreteRelu05());
				this.layers().add(concreteAffine06());
				this.loss(concreteLoss());
			}
		};
		DataSet trainSet = MNIST.trainDataSet(true);
		DataSet testSet = MNIST.testDataSet(true);

		for (int i = 0; i < 600 * 10000; i++) {
			DataSet set = trainSet.randomChoice(100);
			ConvolutionMatrix y = network.predict(set.input());
			network.loss().forward(y, set.label());
			ConvolutionMatrix dy = network.loss().backward();
			network.gradient(dy).update();
			if (i % 100 == 0) {
				DumpThread thread = new DumpThread(network, testSet);
				thread.start();
			}
		}
	}

	private static Convolution concreteConv01() {
		Convolution convolution = new Convolution();
		ConvolutionMatrix w = new ConvolutionMatrix(30, 1, 5, 5);
		w.visit((n, c, height, width, v) -> w.set(n, c, height, width, random.nextDouble() * 0.01));
		ConvolutionMatrix b = new ConvolutionMatrix(30, 1, 1, 1);
		convolution.w(w);
		convolution.b(b);
		convolution.stride(1);
		convolution.pad(0);
		convolution.optimizer(new StochasticGradientDescent());
		return convolution;
	}

	private static Relu concreteRelu02() {
		return new Relu();
	}

	private static Pooling concretePooling03() {
		return new Pooling(2, 2, 2, 0);
	}

	private static Affine concreteAffine04() {
		Affine affine = new Affine();
		ConvolutionMatrix w = new ConvolutionMatrix(12 * 12 * 30, 1, 1, 100);
		w.visit((n, c, height, width, v) -> w.set(n, c, height, width, random.nextDouble() * 0.01));
		ConvolutionMatrix b = new ConvolutionMatrix(100, 1, 1, 1);
		affine.w(w);
		affine.b(b);
		affine.optimizer(new StochasticGradientDescent());
		return affine;
	}

	private static Relu concreteRelu05() {
		return new Relu();
	}

	private static Affine concreteAffine06() {
		Affine affine = new Affine();
		ConvolutionMatrix w = new ConvolutionMatrix(100, 1, 1, 10);
		w.visit((n, c, height, width, v) -> w.set(n, c, height, width, random.nextDouble() * 0.01));
		ConvolutionMatrix b = new ConvolutionMatrix(10, 1, 1, 1);
		affine.w(w);
		affine.b(b);
		affine.optimizer(new StochasticGradientDescent());
		return affine;
	}

	private static Loss concreteLoss() {
		return new SoftmaxWithCrossEntropyError();
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
