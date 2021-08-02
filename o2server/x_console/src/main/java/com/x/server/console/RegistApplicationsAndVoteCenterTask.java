package com.x.server.console;

import org.apache.commons.lang3.BooleanUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.node.RefreshApplicationsEvent;
import com.x.server.console.node.RegistApplicationsEvent;
import com.x.server.console.node.UpdateApplicationsEvent;
import com.x.server.console.node.VoteCenterEvent;
import com.x.server.console.server.Servers;

public class RegistApplicationsAndVoteCenterTask implements Job {

	private static Logger logger = LoggerFactory.getLogger(RegistApplicationsAndVoteCenterTask.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			// 刷新本地application
			Config.resource_node_eventQueue().put(XGsonBuilder.instance().toJsonTree(new RefreshApplicationsEvent()));
			if (BooleanUtils.isTrue(Servers.applicationServerIsStarted())) {
				// 先选举center
				Config.resource_node_eventQueue().put(XGsonBuilder.instance().toJsonTree(new VoteCenterEvent()));
				// 注册node上所有的appliction到各个center
				Config.resource_node_eventQueue()
						.put(XGsonBuilder.instance().toJsonTree(new RegistApplicationsEvent()));
				Config.resource_node_eventQueue()
						.put(XGsonBuilder.instance().toJsonTree(new UpdateApplicationsEvent()));
			}
		} catch (Exception e) {
			logger.error(e);
			Thread.currentThread().interrupt();
		}
	}
}