package com.x.server.console.log;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class BypassLoggerPrintStream extends PrintStream {

	private PrintStream printStream;
	private Logger logger;

	public BypassLoggerPrintStream(PrintStream printStream, Logger logger) {
		super(NullOutputStream.NULL_OUTPUT_STREAM);
		this.printStream = printStream;
		this.logger = logger;
	}

	@Override
	public void flush() {
		printStream.flush();
	}

	@Override
	public void close() {
		printStream.close();
	}

	@Override
	public boolean checkError() {
		return printStream.checkError();
	}

	@Override
	public void write(int b) {
		printStream.write(b);
		if (logger.isInfoEnabled()) {
			logger.info("{}", Objects.toString(b));
		}
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		printStream.write(buf, off, len);
		if (logger.isInfoEnabled()) {
			String value = new String(ArrayUtils.subarray(buf, off, off + len));
			logger.info("{}", StringUtils.removeEnd(value, System.lineSeparator()));
		}
	}

	@Override
	public void print(boolean b) {
		printStream.print(b);
		if (logger.isInfoEnabled()) {
			logger.info("{}", b);
		}
	}

	@Override
	public void print(char c) {
		printStream.print(c);
		if (logger.isInfoEnabled()) {
			logger.info("{}", c);
		}
	}

	@Override
	public void print(int i) {
		printStream.print(i);
		if (logger.isInfoEnabled()) {
			logger.info("{}", i);
		}
	}

	@Override
	public void print(long l) {
		printStream.print(l);
		if (logger.isInfoEnabled()) {
			logger.info("{}", l);
		}
	}

	@Override
	public void print(float f) {
		printStream.print(f);
		if (logger.isInfoEnabled()) {
			logger.info("{}", f);
		}
	}

	@Override
	public void print(double d) {
		printStream.print(d);
		if (logger.isInfoEnabled()) {
			logger.info("{}", d);
		}
	}

	@Override
	public void print(char[] s) {
		printStream.print(s);
		if (logger.isInfoEnabled()) {
			logger.info("{}", String.valueOf(s));
		}
	}

	@Override
	public void print(String s) {
		printStream.print(s);
		if (logger.isInfoEnabled()) {
			logger.info("{}", s);
		}
	}

	@Override
	public void print(Object obj) {
		printStream.print(obj);
		if (logger.isInfoEnabled()) {
			logger.info("{}", Objects.toString(obj));
		}
	}

	@Override
	public void println() {
		printStream.println();
		if (logger.isInfoEnabled()) {
			logger.info("{}", System.lineSeparator());
		}
	}

	@Override
	public void println(boolean x) {
		printStream.println(x);
		if (logger.isInfoEnabled()) {
			logger.info("{}", x);
		}
	}

	@Override
	public void println(char x) {
		printStream.println(x);
		if (logger.isInfoEnabled()) {
			logger.info("{}", x);
		}
	}

	@Override
	public void println(int x) {
		printStream.println(x);
		if (logger.isInfoEnabled()) {
			logger.info("{}", x);
		}
	}

	@Override
	public void println(long x) {
		printStream.println(x);
		if (logger.isInfoEnabled()) {
			logger.info("{}", x);
		}
	}

	@Override
	public void println(float x) {
		printStream.println(x);
		logger.info("{}", x);
	}

	@Override
	public void println(double x) {
		printStream.println(x);
		if (logger.isInfoEnabled()) {
			logger.info("{}", x);
		}
	}

	@Override
	public void println(char[] x) {
		printStream.println(x);
		if (logger.isInfoEnabled()) {
			logger.info("{}", String.valueOf(x));
		}
	}

	@Override
	public void println(String x) {
		printStream.println(x);
		if (logger.isInfoEnabled()) {
			logger.info("{}", x);
		}
	}

	@Override
	public void println(Object x) {
		printStream.println(x);
		if (logger.isInfoEnabled()) {
			logger.info("{}", Objects.toString(x));
		}
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		printStream.printf(format, args);
		if (logger.isInfoEnabled()) {
			logger.info("{}", String.format(format, args));
		}
		return this;
	}

	@Override
	public PrintStream printf(Locale l, String format, Object... args) {
		printStream.printf(l, format, args);
		if (logger.isInfoEnabled()) {
			logger.info("{}", String.format(l, format, args));
		}
		return this;
	}

	@Override
	public PrintStream format(String format, Object... args) {
		printStream.format(format, args);
		return this;
	}

	@Override
	public PrintStream format(Locale l, String format, Object... args) {
		printStream.format(l, format, args);
		return this;
	}

}
