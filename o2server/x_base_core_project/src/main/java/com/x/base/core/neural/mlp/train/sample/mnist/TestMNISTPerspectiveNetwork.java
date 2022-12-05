package com.x.base.core.neural.mlp.train.sample.mnist;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.x.base.core.neural.mlp.dump.Dump;
import com.x.base.core.neural.mlp.train.sample.mnist.MNIST.DataSet;

public class TestMNISTPerspectiveNetwork {

	public static void main(String... args) throws Exception {
		String json = Files.readString(Paths.get("dump", "MNIST.json"));
		Dump dump = Dump.formJson(json);
		MNISTPerspectiveNetwork network = dump.toNetwork(MNISTPerspectiveNetwork.class);

		DataSet trainSet = MNIST.init().trainDataSet(true);
		network.perspective(trainSet.choice(4));
	}

}
