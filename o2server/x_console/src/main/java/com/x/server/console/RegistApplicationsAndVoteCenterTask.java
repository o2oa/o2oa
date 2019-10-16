package com.x.server.console;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.node.RegistApplicationsEvent;
import com.x.server.console.node.UpdateApplicationsEvent;
import com.x.server.console.node.VoteCenterEvent;
import com.x.server.console.server.Servers;

public class RegistApplicationsAndVoteCenterTask implements Job {

	private static Logger logger = LoggerFactory.getLogger(RegistApplicationsAndVoteCenterTask.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (Servers.applicationServerIsRunning()) {
				Config.resource_node_eventQueue()
						.put(XGsonBuilder.instance().toJsonTree(new RegistApplicationsEvent()));
			} else {
				Config.resource_node_eventQueue()
						.put(XGsonBuilder.instance().toJsonTree(new UpdateApplicationsEvent()));
			}
			Config.resource_node_eventQueue().put(XGsonBuilder.instance().toJsonTree(new VoteCenterEvent()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}