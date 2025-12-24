package com.x.server.console;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.action.DumpData;

public class DumpDataTask implements Job {

	private static Logger logger = LoggerFactory.getLogger(DumpDataTask.class);

	private static final String MATCH = "dumpData_[1,2][0,9][0-9][0-9][0,1][0-9][0-3][0-9][0-5][0-9][0-5][0-9][0-5][0-9]";

	private static final Pattern DIR_NAME_PATTERN = Pattern.compile("^" + MATCH + "$");
	private static final Pattern FILE_NAME_PATTERN = Pattern.compile("^" + MATCH + ".zip$");

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			logger.print("schedule dump data to:{}.", Config.currentNode().dumpData().path());
			if (Config.currentNode().dumpData().size() > 0) {
				File dir = new File(Config.base(), "local/dump");
				if (dir.exists() && dir.isDirectory()) {
					final List<File> list = new ArrayList<>();
					try (Stream<Path> children = Files.list(dir.toPath())) {
						children.filter(Files::isDirectory) // 只保留目录，不列示文件
								.filter(p -> DIR_NAME_PATTERN.matcher(p.getFileName().toString()).matches())
								.sorted(Comparator.comparing(p -> p.getFileName().toString(),
										Comparator.reverseOrder()))
								.skip(Config.currentNode().dumpData().size() - 1L)
								.forEach(p -> list.add(p.toAbsolutePath().toFile()));
					}
					try (Stream<Path> children = Files.list(dir.toPath())) {
						children.filter(Files::isRegularFile)
								.filter(p -> FILE_NAME_PATTERN.matcher(p.getFileName().toString()).matches())
								.sorted(Comparator.comparing(p -> p.getFileName().toString(),
										Comparator.reverseOrder()))
								.skip(Config.currentNode().dumpData().size() - 1L)
								.forEach(p -> list.add(p.toAbsolutePath().toFile()));
					}
					for (File file : list) {
						logger.print("dumpDataTask delete file: {}.", file.getAbsolutePath());
						FileUtils.forceDelete(file);
					}
				}
			}
			DumpData action = new DumpData();
			action.execute(Config.currentNode().dumpData().path());
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
}