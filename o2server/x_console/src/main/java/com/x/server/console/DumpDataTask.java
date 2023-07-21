package com.x.server.console;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.action.DumpData;

public class DumpDataTask implements Job {

	private static Logger logger = LoggerFactory.getLogger(DumpDataTask.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			logger.print("schedule dump data to:{}.", Config.currentNode().dumpData().path());
			DumpData action = new DumpData();
			action.execute(Config.currentNode().dumpData().path());
			if (Config.currentNode().dumpData().size() > 0) {
				File dir = new File(Config.base(), "local/dump");
				List<File> list = new ArrayList<>();
				if (dir.exists() && dir.isDirectory()) {
					for (File f : FileUtils.listFilesAndDirs(dir, FalseFileFilter.FALSE, new RegexFileFilter(
							"^dumpData_[1,2][0,9][0-9][0-9][0,1][0-9][0-3][0-9][0-5][0-9][0-5][0-9][0-5][0-9]$"))) {
						if (dir != f) {
							list.add(f);
						}
					}
					list = list.stream().sorted(Comparator.comparing(File::getName).reversed())
							.collect(Collectors.toList());
					if (list.size() > Config.currentNode().dumpData().size()) {
						for (int i = Config.currentNode().dumpData().size(); i < list.size(); i++) {
							File file = list.get(i);
							logger.print("dumpDataTask delete:{}.", file.getAbsolutePath());
							FileUtils.forceDelete(file);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
}