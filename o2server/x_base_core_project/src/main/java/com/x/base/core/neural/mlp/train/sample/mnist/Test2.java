package com.x.base.core.neural.mlp.train.sample.mnist;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class Test2 {

	public static void main(String... args) throws Exception {

		RealMatrix w = MatrixUtils.createRealMatrix(8, 6);
		w.setColumn(0, new double[] { 0.00f, 0.01f, 0.02f, 0.03f, 0.04f, 0.05f, 0.06f, 0.07f });
		w.setColumn(1, new double[] { 0.10f, 0.11f, 0.12f, 0.13f, 0.14f, 0.15f, 0.16f, 0.17f });
		w.setColumn(2, new double[] { 0.20f, 0.21f, 0.22f, 0.23f, 0.24f, 0.25f, 0.26f, 0.27f });
		w.setColumn(3, new double[] { 0.30f, 0.31f, 0.32f, 0.33f, 0.34f, 0.35f, 0.36f, 0.37f });
		w.setColumn(4, new double[] { 0.40f, 0.41f, 0.42f, 0.43f, 0.44f, 0.45f, 0.46f, 0.47f });
		w.setColumn(5, new double[] { 0.50f, 0.51f, 0.52f, 0.53f, 0.54f, 0.55f, 0.56f, 0.57f });

		RealMatrix input = MatrixUtils.createRealMatrix(1, 8);
		input.setRow(0, new double[] { 1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d });

		System.out.println(input.multiply(w));
	}

}
