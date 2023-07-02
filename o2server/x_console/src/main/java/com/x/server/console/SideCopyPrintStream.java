package com.x.server.console;

import java.io.PrintStream;
import java.util.Locale;

import org.apache.commons.io.output.NullOutputStream;

public class SideCopyPrintStream extends PrintStream {

	private PrintStream original;
	private PrintStream copy;

	public SideCopyPrintStream(PrintStream original, PrintStream copy) {
		super(NullOutputStream.NULL_OUTPUT_STREAM);
		this.original = original;
		this.copy = copy;
	}

	public void flush() {
		original.flush();
		copy.flush();
	}

	public void close() {
		original.close();
		copy.close();
	}

	public boolean checkError() {
		return original.checkError();
	}

	public void write(int b) {
		original.write(b);
		copy.write(b);
	}

	public void write(byte buf[], int off, int len) {
		original.write(buf, off, len);
		copy.write(buf, off, len);
	}

	public void print(boolean b) {
		original.print(b);
		copy.print(b);
	}

	public void print(char c) {
		original.print(c);
		copy.print(c);
	}

	public void print(int i) {
		original.print(i);
		copy.print(i);
	}

	public void print(long l) {
		original.print(l);
		copy.print(l);
	}

	public void print(float f) {
		original.print(f);
		copy.print(f);
	}

	public void print(double d) {
		original.print(d);
		copy.print(d);
	}

	public void print(char s[]) {
		original.print(s);
		copy.print(s);
	}

	public void print(String s) {
		original.print(s);
		copy.print(s);
	}

	public void print(Object obj) {
		original.print(obj);
		copy.print(obj);
	}

	public void println() {
		original.println();
		copy.println();
	}

	public void println(boolean x) {
		original.println(x);
		copy.println(x);
	}

	public void println(char x) {
		original.println(x);
		copy.println(x);
	}

	public void println(int x) {
		original.println(x);
		copy.println(x);
	}

	public void println(long x) {
		original.println(x);
		copy.println(x);
	}

	public void println(float x) {
		original.println(x);
		copy.println(x);
	}

	public void println(double x) {
		original.println(x);
		copy.println(x);
	}

	public void println(char x[]) {
		original.println(x);
		copy.println(x);
	}

	public void println(String x) {
		original.println(x);
		copy.println(x);
	}

	public void println(Object x) {
		original.println(x);
		copy.println(x);
	}

	public PrintStream printf(String format, Object... args) {
		original.printf(format, args);
		copy.printf(format, args);
		return this;
	}

	public PrintStream printf(Locale l, String format, Object... args) {
		original.printf(l, format, args);
		copy.printf(l, format, args);
		return this;
	}

	public PrintStream format(String format, Object... args) {
		original.format(format, args);
		copy.format(format, args);
		return this;
	}

	public PrintStream format(Locale l, String format, Object... args) {
		original.format(l, format, args);
		copy.format(l, format, args);
		return this;
	}

	public PrintStream append(CharSequence csq) {
		original.append(csq);
		copy.append(csq);
		return this;
	}

	public PrintStream append(CharSequence csq, int start, int end) {
		original.append(csq, start, end);
		copy.append(csq, start, end);
		return this;
	}

	public PrintStream append(char c) {
		original.append(c);
		copy.append(c);
		return this;
	}

}
