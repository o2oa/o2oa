package com.x.server.console.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.junit.Test;

import com.x.server.console.OptionFactory;

public class TestClient {
	@Test
	public void test() throws IOException {
		File file = new File("");
		System.out.println(file.getAbsolutePath());

	}

	@Test
	public void basePath() {
		try {
			Options options = OptionFactory.options();
			HelpFormatter formatter = new HelpFormatter();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			formatter.printHelp("O2Platform", options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}