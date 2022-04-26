package com.x.server.console.action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ProcessPlatformExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessPlatformExecutor.class);

	public void execute() throws Exception {
		ExecutorService[] executorServices = Config.resource_node_processPlatformExecutors();
		List<String> list = new ArrayList<>();
		for (int i = 0; i < executorServices.length; i++) {
			ExecutorService service = executorServices[i];
			ThreadPoolExecutor executor = (ThreadPoolExecutor) service;
			BlockingQueue<Runnable> queue = executor.getQueue();
			list.add(String.format("processPlatform executorServices[%d] completed:%d, block:%d.", i,
					executor.getCompletedTaskCount(), queue.size()));
			if (!queue.isEmpty()) {
				List<String> os = new ArrayList<>();
				for (Runnable o : queue) {
					os.add(o.getClass().toString());
				}
				list.add("  +++ blocking: " + StringUtils.join(os, ",") + ".");
			}
		}
		LOGGER.print(StringUtils.join(list, StringUtils.LF));

	}
}