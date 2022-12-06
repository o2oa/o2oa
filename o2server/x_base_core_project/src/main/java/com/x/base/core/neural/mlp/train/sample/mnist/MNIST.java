package com.x.base.core.neural.mlp.train.sample.mnist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

import com.x.base.core.neural.mlp.MultilayerPerceptronTools;
import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public class MNIST {

	private MNIST() {

	}

	public static MNIST init() throws URISyntaxException, IOException {
		load(MNIST_TRAIN_IMAGES_NAME);
		load(MNIST_TRAIN_LABELS_NAME);
		load(MNIST_TEST_IMAGES_NAME);
		load(MNIST_TEST_LABELS_NAME);
		return new MNIST();
	}

	private static void load(String name) throws URISyntaxException, IOException {
		// Path p = Paths.get(ClassLoader.getSystemResource(Paths.get(MNIST_DIR,
		// name).toString()).toURI());
		Path p = Paths.get(MNIST_DIR, name);
		if (!Files.exists(p)) {
			byte[] bytes = IOUtils.toByteArray(new URL(URL_BASE + MNIST_TRAIN_IMAGES_NAME + ".gz"));
			try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
				Files.write(p, IOUtils.toByteArray(gis));
			}
		}
	}

	public static final String URL_BASE = "http://yann.lecun.com/exdb/mnist/";

	public static final String MNIST_DIR = "MNIST_data";

	public static final String MNIST_TRAIN_IMAGES_NAME = "train-images.idx3-ubyte";
	public static final String MNIST_TRAIN_LABELS_NAME = "train-labels.idx1-ubyte";
	public static final String MNIST_TEST_IMAGES_NAME = "t10k-images.idx3-ubyte";
	public static final String MNIST_TEST_LABELS_NAME = "t10k-labels.idx1-ubyte";

	public DataSet trainDataSet(boolean normalize) throws IOException, URISyntaxException {
		return dataSet(Paths.get(MNIST_DIR, MNIST_TRAIN_IMAGES_NAME), Paths.get(MNIST_DIR, MNIST_TRAIN_LABELS_NAME),
				60000, normalize);
	}

	public DataSet testDataSet(boolean normalize) throws IOException, URISyntaxException {
		return dataSet(Paths.get(MNIST_DIR, MNIST_TEST_IMAGES_NAME), Paths.get(MNIST_DIR, MNIST_TEST_LABELS_NAME),
				10000, normalize);
	}

	private DataSet dataSet(Path imagePath, Path labelPath, int count, boolean normalize)
			throws IOException, URISyntaxException {
		DataSet set = new DataSet();
		byte[] imageBytes = read(imagePath);
		byte[] labelBytes = read(labelPath);
		for (int i = 0; i < count; i++) {
			float[] input = new float[28 * 28];
			int n = 0;
			for (int j = i * 28 * 28 + 16; j < (i + 1) * 28 * 28 + 16; j++) {
				input[n++] = Byte.toUnsignedInt(imageBytes[j]);
			}
			if (normalize) {
				for (int j = 0; j < input.length; j++) {
					input[j] = input[j] / 255f;
				}
			}
			Data data = new Data();
			data.input = input;
			float[] label = new float[10];
			Arrays.fill(label, 0f);
			label[Byte.toUnsignedInt(labelBytes[8 + i])] = 1f;
			data.label = label;
			set.add(data);
		}
		return set;
	}

	public class DataSet extends ArrayList<Data> implements com.x.base.core.neural.DataSet<Data> {

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
				if (MultilayerPerceptronTools.argmax(data.label) == label) {
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

		public MultilayerPerceptronMatrix input() {
			MultilayerPerceptronMatrix x = new MultilayerPerceptronMatrix(this.size(), 28 * 28);
			for (int i = 0; i < x.row(); i++) {
				x.row(i, this.get(i).input);
			}
			return x;
		}

		public MultilayerPerceptronMatrix label() {
			MultilayerPerceptronMatrix x = new MultilayerPerceptronMatrix(this.size(), 10);
			for (int i = 0; i < x.row(); i++) {
				x.row(i, this.get(i).label);
			}
			return x;
		}
	}

	public static class Data implements com.x.base.core.neural.mlp.Data {
		protected float[] input;
		protected float[] label;

		public float[] input() {
			return input;
		}

		public float[] label() {
			return label;
		}

		@Override
		public int inputSize() {
			return 28 * 28;
		}

		@Override
		public int labelSize() {
			return 10;
		}
	}

	private static byte[] read(Path path) throws IOException, URISyntaxException {
		byte[] bs = null;
		try (InputStream in = Files.newInputStream(path)) {
			bs = IOUtils.toByteArray(in);
		}
		return bs;
	}

}
