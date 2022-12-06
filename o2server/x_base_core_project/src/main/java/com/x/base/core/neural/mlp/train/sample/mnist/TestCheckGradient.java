package com.x.base.core.neural.mlp.train.sample.mnist;

import com.x.base.core.neural.mlp.layer.Affine;
import com.x.base.core.neural.mlp.layer.BatchNormalization;
import com.x.base.core.neural.mlp.layer.activation.Relu;
import com.x.base.core.neural.mlp.loss.Loss;
import com.x.base.core.neural.mlp.loss.MeanSquareError;
import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;

public class TestCheckGradient {

	private static Affine affine1;
	private static Affine affine2;
	private static Relu relu1 = new Relu();
	private static Relu relu2 = new Relu();
	private static BatchNormalization batchNormalization1 = new BatchNormalization();
	private static BatchNormalization batchNormalization2 = new BatchNormalization();
	private static Loss loss1 = new MeanSquareError();
	private static Loss loss2 = new MeanSquareError();

	private static MultilayerPerceptronMatrix w = new MultilayerPerceptronMatrix(8, 6);
	private static MultilayerPerceptronMatrix b = new MultilayerPerceptronMatrix(1, 6);
	private static MultilayerPerceptronMatrix input = new MultilayerPerceptronMatrix(1, 8);
	private static MultilayerPerceptronMatrix output = new MultilayerPerceptronMatrix(1, 6);

	public static void main(String... args) throws Exception {
		w.column(0, new float[] { 0.00f, 0.01f, 0.02f, 0.03f, 0.04f, 0.05f, 0.06f, 0.07f });
		w.column(1, new float[] { 0.10f, 0.11f, 0.12f, 0.13f, 0.14f, 0.15f, 0.16f, 0.17f });
		w.column(2, new float[] { 0.20f, 0.21f, 0.22f, 0.23f, 0.24f, 0.25f, 0.26f, 0.27f });
		w.column(3, new float[] { 0.30f, 0.31f, 0.32f, 0.33f, 0.34f, 0.35f, 0.36f, 0.37f });
		w.column(4, new float[] { 0.40f, 0.41f, 0.42f, 0.43f, 0.44f, 0.45f, 0.46f, 0.47f });
		w.column(5, new float[] { 0.50f, 0.51f, 0.52f, 0.53f, 0.54f, 0.55f, 0.56f, 0.57f });
		b.row(0, new float[] { 0f, 0f, 0f, 0f, 0f, 0f });
		input.row(0, new float[] { 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f });
		output.row(0, new float[] { 0f, 1f, 0f, 0f, 0f, 0f });
		affine1 = new Affine("gradient", 8, 6);
		affine2 = new Affine("numericalGradient", 8, 6);
		affine1.w(w.copy());
		affine1.b(b.copy());
		affine2.w(w.copy());
		affine2.b(b.copy());

		MultilayerPerceptronMatrix y1 = affine1.forward(input);
		MultilayerPerceptronMatrix y12 = batchNormalization1.forward(y1);
		loss1.forward(y12, output);
		affine1.backward(batchNormalization1.backward(loss1.backward()));
		numericalGradient(affine2, batchNormalization2, loss2, affine2.w(), affine2.dw(), input, output);
		System.out.println("gradient:" + sum(affine1.dw()) + ", numericalGradient:" + sum(affine2.dw()));
	}

	private static double sum(MultilayerPerceptronMatrix x) {
		double s = 0f;
		for (int r = 0; r < x.row(); r++) {
			for (int c = 0; c < x.column(); c++) {
				s += x.get(r, c);
			}
		}
		return s;
	}

	private static void numericalGradient(Affine affine, BatchNormalization batchNormalization, Loss loss,
			MultilayerPerceptronMatrix m, MultilayerPerceptronMatrix d, MultilayerPerceptronMatrix x,
			MultilayerPerceptronMatrix t) {
		for (int i = 0; i < m.row(); i++) {
			for (int j = 0; j < m.column(); j++) {
				float original = m.get(i, j);
				m.set(i, j, original + (1e-4f));
				float f1 = loss.forward(batchNormalization.forward(affine.forward(x)), t);
				m.set(i, j, original - (1e-4f));
				float f2 = loss.forward(batchNormalization.forward(affine.forward(x)), t);
				System.out.println(original + "->" + f1 + ":" + f2);
				d.set(i, j, (f1 - f2) / (2 * (1e-4f)));
				m.set(i, j, original);
			}
		}
	}

}
