package com.x.collaboration.assemble.websocket.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.websocket.Session;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.collaboration.assemble.websocket.ThisApplication;

public class CleanupConnectionsTimer implements Job {

	private static Logger logger = LoggerFactory.getLogger(CleanupConnectionsTimer.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			List<String> removes = new ArrayList<>();
			for (Entry<String, Session> entry : ThisApplication.connections.entrySet()) {
				if ((null == entry.getValue()) || (!entry.getValue().isOpen())) {
					removes.add(entry.getKey());
				}
			}
			for (String str : removes) {
				ThisApplication.connections.remove(str);
			}
			logger.info("clean {} websocket session.", removes.size());
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

}