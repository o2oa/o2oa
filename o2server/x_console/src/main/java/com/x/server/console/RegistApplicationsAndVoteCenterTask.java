package com.x.server.console;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.node.RegistApplicationsEvent;
import com.x.server.console.node.UpdateApplicationsEvent;
import com.x.server.console.node.VoteCenterEvent;
import com.x.server.console.server.Servers;

import org.apache.commons.lang3.BooleanUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RegistApplicationsAndVoteCenterTask implements Job {

	private static Logger logger = LoggerFactory.getLogger(RegistApplicationsAndVoteCenterTask.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			// 先选举center
			Config.resource_node_eventQueue().put(XGsonBuilder.instance().toJsonTree(new VoteCenterEvent()));
			if (BooleanUtils.isTrue(Servers.applicationServerIsRunning())) {
				Config.resource_node_eventQueue()
						.put(XGsonBuilder.instance().toJsonTree(new RegistApplicationsEvent()));
			} else {
				Config.resource_node_eventQueue()
						.put(XGsonBuilder.instance().toJsonTree(new UpdateApplicationsEvent()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}