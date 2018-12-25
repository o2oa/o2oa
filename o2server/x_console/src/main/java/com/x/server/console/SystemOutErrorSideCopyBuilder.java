package com.x.server.console;

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.util.RolloverFileOutputStream;

import com.x.base.core.project.config.Config;

public class SystemOutErrorSideCopyBuilder {

	private static PrintStream stdout;
	private static PrintStream stderr;

	private static PrintStream rolloverFilePrintStream;

	private static volatile boolean started;

	public static void start() throws Exception {
		stdout = System.out;
		stderr = System.err;
		File logDir = new File(Config.base(), "logs");
		FileUtils.forceMkdir(logDir);
		RolloverFileOutputStream rolloverFileOutputStream = new RolloverFileOutputStream(logDir + "/yyyy_mm_dd.out.log",
				false, 180);
		rolloverFilePrintStream = new PrintStream(rolloverFileOutputStream);
		SideCopyPrintStream sideCopyOut = new SideCopyPrintStream(stdout, rolloverFilePrintStream);
		SideCopyPrintStream sideCopyErr = new SideCopyPrintStream(stderr, rolloverFilePrintStream);
		System.out.println("redirct stdout/stderr to " + rolloverFileOutputStream.getDatedFilename());
		System.setOut(sideCopyOut);
		System.setErr(sideCopyErr);
		started = true;
	}

	public static void stop() throws Exception {
		if (started) {
			System.setOut(stdout);
			System.setErr(stderr);
			rolloverFilePrintStream.close();
		}
	}

}