package com.x.server.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.OsArchDetect;

public final class AiAgent {

	private static final Logger logger = LoggerFactory.getLogger(AiAgent.class);

	/**
	 * 启动阶段错误输出窗口。 这段时间内 stderr 会打印到主服务器 console。 超过这个时间后仍会继续读取
	 * stderr，但不再打印，避免子进程阻塞。
	 */
	private static final Duration STARTUP_ERROR_WINDOW = Duration.ofSeconds(30);

	private final String base;

	private volatile Process process;

	public AiAgent(String base) {
		this.base = base;
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "aiagent-process-shutdown-hook"));
	}

	public synchronized void startIfExists() {
		if (process != null && process.isAlive()) {
			return;
		}

		Path dir = Paths.get(base, "servers/aiagent");
		Path javaCmd = resolveJavaCmd(dir);
		Path bootJar = dir.resolve("boot.jar");

		if (!Files.isDirectory(dir)) {
			return;
		}
		if (!Files.isRegularFile(javaCmd)) {
			return;
		}
		if (!Files.isRegularFile(bootJar)) {
			return;
		}

		try {
			Process p = buildProcess(dir, javaCmd, bootJar).start();
			process = p;

			startErrorReader(p, STARTUP_ERROR_WINDOW);

			p.onExit().thenRun(() -> {
				if (process == p) {
					process = null;
				}
			});

		} catch (IOException e) {
			logger.error(e);
		}
	}

	private void shutdown() {
		Process p = this.process;
		if (p == null) {
			return;
		}

		try {
			if (!p.isAlive()) {
				return;
			}

			p.destroy();

			if (!p.waitFor(3, TimeUnit.SECONDS) && p.isAlive()) {
				p.destroyForcibly();
				p.waitFor(3, TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			if (p.isAlive()) {
				p.destroyForcibly();
			}
		}
	}

	private Path resolveJavaCmd(Path dir) {
		String os = OsArchDetect.os();
		if (OsArchDetect.OS_WINDOWS_X64.equals(os)) {
			return dir.resolve("jvm").resolve(os).resolve("bin").resolve("java.exe");
		}
		return dir.resolve("jvm").resolve(os).resolve("bin").resolve("java");
	}

	private static ProcessBuilder buildProcess(Path dir, Path javaCmd, Path bootJar) {
		ProcessBuilder pb = new ProcessBuilder(javaCmd.toAbsolutePath().toString(), "--add-modules",
				"jdk.incubator.vector", "--enable-native-access=ALL-UNNAMED", "-Xmx2g",
				"-XX:NativeMemoryTracking=summary", "-jar", bootJar.toAbsolutePath().toString());

		pb.directory(dir.toFile());

		// 正常输出不要
		pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);

		// 错误输出保留为 PIPE，供父进程读取
		pb.redirectError(ProcessBuilder.Redirect.PIPE);

		return pb;
	}

	private static void startErrorReader(Process process, Duration startupWindow) {
		final long deadlineNanos = System.nanoTime() + startupWindow.toNanos();

		Thread t = new Thread(() -> {
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {

				String line;
				while ((line = reader.readLine()) != null) {
					if (System.nanoTime() <= deadlineNanos) {
						System.err.println("[aiagent] " + line);
					}
					// 超过启动窗口后不打印，但仍继续读取，避免 stderr 堵塞
				}
			} catch (IOException e) {
				// 这里通常发生在进程退出或流关闭时，不必升级成错误
			}
		}, "aiagent-stderr-reader");
		t.setDaemon(true);
		t.start();
	}
}