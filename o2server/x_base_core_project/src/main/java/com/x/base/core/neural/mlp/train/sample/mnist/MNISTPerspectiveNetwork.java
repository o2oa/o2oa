package com.x.base.core.neural.mlp.train.sample.mnist;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.x.base.core.neural.mlp.MultilayerPerceptronTools;
import com.x.base.core.neural.mlp.Network;
import com.x.base.core.neural.mlp.layer.Affine;
import com.x.base.core.neural.mlp.layer.BatchNormalization;
import com.x.base.core.neural.mlp.layer.Layer;
import com.x.base.core.neural.mlp.layer.activation.Relu;
import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;
import com.x.base.core.neural.mlp.train.sample.mnist.MNIST.DataSet;

public class MNISTPerspectiveNetwork extends Network {

	public Affine[] affines() {
		List<Affine> list = new ArrayList<>();
		for (Layer layer : this.layers()) {
			if (layer instanceof Affine) {
				list.add((Affine) layer);
			}
		}
		return list.toArray(new Affine[] {});
	}

	public BatchNormalization[] batchNormalizations() {
		List<BatchNormalization> list = new ArrayList<>();
		for (Layer layer : this.layers()) {
			if (layer instanceof BatchNormalization) {
				list.add((BatchNormalization) layer);
			}
		}
		return list.toArray(new BatchNormalization[] {});
	}

	public Relu[] relus() {
		List<Relu> list = new ArrayList<>();
		for (Layer layer : this.layers()) {
			if (layer instanceof Relu) {
				list.add((Relu) layer);
			}
		}
		return list.toArray(new Relu[] {});
	}

	@Override
	public MultilayerPerceptronMatrix predict(MultilayerPerceptronMatrix x) {
		for (Layer layer : layers()) {
			x = layer.forward(x);
			if (layer == relus()[0]) {
				perspectiveRelu(x, 0, 28, 28);
			} else if (layer == relus()[1]) {
				perspectiveRelu(x, 1, 10, 10);
			}
//			} else if (layer == relus()[2]) {
//				perspectiveRelu(x, 2);
//			}
		}
		return x;

	}

	private void perspectiveRelu(MultilayerPerceptronMatrix y, int position, int rowSize, int columnSize) {
		float[] values = y.row(0);
		try {
			float[] combineValues = new float[rowSize * columnSize];
			for (int i = 0; i < combineValues.length; i++) {
				combineValues[i] = 0f;
			}
			for (int i = 0; i < values.length; i++) {
				if (values[i] > 0) {
					float[] ws = affines()[position].w().column(i);
					String json = (new Gson()).toJson(ws);
					Files.writeString(Paths.get("perspective", "w" + position + "_" + i + ".json"), json);
					BufferedImage image = new BufferedImage(rowSize, columnSize, BufferedImage.TYPE_INT_RGB);
					ws = MultilayerPerceptronTools.normalize0to1(ws);
					for (int column = 0; column < columnSize; column++) {
						for (int row = 0; row < rowSize; row++) {
							if (ws[column * columnSize + row] * 255 > 200d) {
								combineValues[column * columnSize + row] = combineValues[column * columnSize + row]
										+ ws[column * columnSize + row];
							}
							int value = (int) (ws[column * columnSize + row] * 255d);
							if (value < 210) {
								value = 0;
							}
							Color color = new Color(value, value, value);
							image.setRGB(row, column, color.getRGB());
						}
					}
					File out = new File("perspective/w" + position + "_" + i + ".jpg");
					ImageIO.write(image, "jpg", out);

				}
			}
			BufferedImage combine = new BufferedImage(rowSize, columnSize, BufferedImage.TYPE_INT_RGB);
			combineValues = MultilayerPerceptronTools.normalize0to1(combineValues);
			for (int column = 0; column < columnSize; column++) {
				for (int row = 0; row < rowSize; row++) {
					int v = (int) (combineValues[column * columnSize + row] * 255d);
					Color color = new Color(v, v, v);
					combine.setRGB(row, column, color.getRGB());
				}
			}
			File out = new File("perspective/w" + position + "_combine.jpg");
			ImageIO.write(combine, "jpg", out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void perspective(DataSet dataSet) {
		for (int i = 0; i < dataSet.size(); i++) {
			DataSet ds = dataSet.choice(i);
			MultilayerPerceptronMatrix y = this.predict(ds.input());
			int p = MultilayerPerceptronTools.argmax(y.row(0));
			int l = MultilayerPerceptronTools.argmax(ds.label().row(0));
			System.out.println("predict:" + p + ", label:" + l);
		}
	}

}
