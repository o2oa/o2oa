package com.x.base.core.project.executor;

import java.io.Closeable;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.io.IOUtils;

public class Signal implements Closeable {

	private PipedInputStream input;
	private PipedOutputStream output;
	private volatile boolean completed = false;

	public Signal() {
		try {
			this.output = new PipedOutputStream();
			this.input = new PipedInputStream();
			input.connect(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void write(String text) {
		try {
			if (!completed) {
				this.output.write(Objects.toString(text, "").getBytes(StandardCharsets.UTF_8));
				this.output.close();
				completed = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String read() {
		String value = null;
		try {
			value = IOUtils.toString(this.input, StandardCharsets.UTF_8);
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	@Override
	public void close() {
		IOUtils.closeQuietly(output, null);
		IOUtils.closeQuietly(input, null);
	}

}
