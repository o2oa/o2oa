package com.x.base.core.neural.cnn.mnist;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;

import com.x.base.core.neural.cnn.ConvolutionTools;
import com.x.base.core.neural.cnn.matrix.ConvolutionMatrix;

public class MNIST {

	public static final String MNIST_TRAIN_IMAGES_FILE = "train-images.idx3-ubyte";
	public static final String MNIST_TRAIN_LABELS_FILE = "train-labels.idx1-ubyte";
	public static final String MNIST_TEST_IMAGES_FILE = "t10k-images.idx3-ubyte";
	public static final String MNIST_TEST_LABELS_FILE = "t10k-labels.idx1-ubyte";

	public static DataSet trainDataSet(boolean normalize) throws Exception {
		return dataSet(MNIST_TRAIN_IMAGES_FILE, MNIST_TRAIN_LABELS_FILE, 60000, normalize);
	}

	public static DataSet testDataSet(boolean normalize) throws Exception {
		return dataSet(MNIST_TEST_IMAGES_FILE, MNIST_TEST_LABELS_FILE, 10000, normalize);
	}

	private static DataSet dataSet(String imageFile, String labelFile, int count, boolean normalize) throws Exception {
		DataSet set = new DataSet();
		byte[] imageBytes = read(imageFile);
		byte[] labelBytes = read(labelFile);
		for (int i = 0; i < count; i++) {
			double[][][] o = new double[1][28][28];
			int n = 0;
			for (int j = i * 28 * 28 + 16; j < (i + 1) * 28 * 28 + 16; j++) {
				o[0][n % 28][n / 28] = Byte.toUnsignedInt(imageBytes[j]);
				n++;
			}
			if (normalize) {
				for (int a = 0; a < o.length; a++) {
					for (int b = 0; b < o[0].length; b++) {
						for (int c = 0; c < o[0][0].length; c++) {
							o[a][b][c] = o[a][b][c] / 255d;
						}
					}
				}
			}
			Data data = new Data();
			data.input = o;
			double[] label = new double[10];
			Arrays.fill(label, 0d);
			label[Byte.toUnsignedInt(labelBytes[8 + i])] = 1d;
			data.label = label;
			set.add(data);
		}
		return set;
	}

	public static class DataSet extends ArrayList<Data> {

		private static final long serialVersionUID = 1L;

		private Random random = new SecureRandom();

		public DataSet sub(int fromIndex, int toIndex) {
			DataSet set = new DataSet();
			for (int i = fromIndex; i < toIndex; i++) {
				set.add(this.get(i));
			}
			return set;
		}

		public DataSet filter(int label) {
			DataSet set = new DataSet();
			for (Data data : this) {
				if (ConvolutionTools.argmax(data.label) == label) {
					set.add(data);
				}
			}
			return set;
		}

		public DataSet choice(int... arr) {
			DataSet set = new DataSet();
			for (int i : arr) {
				set.add(this.get(i));
			}
			return set;
		}

		public DataSet choice(List<Integer> list) {
			DataSet set = new DataSet();
			for (int i : list) {
				set.add(this.get(i));
			}
			return set;
		}

		public DataSet randomChoice(int size) {
			DataSet set = new DataSet();
			random.ints(size, 0, this.size()).forEach(o -> set.add(this.get(o)));
			return set;
		}

		public ConvolutionMatrix input() {
			ConvolutionMatrix x = new ConvolutionMatrix(this.size(), 1, 28, 28);
			for (int i = 0; i < x.number(); i++) {
				x.set(i, this.get(i).input);
			}
			return x;
		}

		public ConvolutionMatrix label() {
			ConvolutionMatrix x = new ConvolutionMatrix(this.size(), 1, 1, 10);
			for (int i = 0; i < x.number(); i++) {
				x.set(i, 0, 0, this.get(i).label);
			}
			return x;
		}
	}

	public static class Data {
		protected double[][][] input;
		protected double[] label;

		public double[][][] input() {
			return input;
		}

		public double[] label() {
			return label;
		}
	}

	private static byte[] read(String fileName) throws Exception {
		Path p = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
		byte[] bs = null;
		try (InputStream in = Files.newInputStream(p)) {
			bs = IOUtils.toByteArray(in);
		}
		return bs;
	}

}
