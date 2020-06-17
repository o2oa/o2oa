package com.x.server.console.action;

import java.util.Date;
import java.util.Map.Entry;

import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import org.apache.commons.lang3.StringUtils;

public class ShowCluster {

	private static Logger logger = LoggerFactory.getLogger(ShowCluster.class);

	private Date start = new Date();

	public boolean execute() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("cluster center server:").append(StringUtils.LF);
		int i = 1;
		for (Entry<String, CenterServer> entry : Config.nodes().centerServers().orderedEntry()) {
			sb.append(String.format("%d: %s, port:%d, sslEnable:%s.", i++, entry.getKey(), entry.getValue().getPort(),
					entry.getValue().getSslEnable() + "")).append(StringUtils.LF);
		}
		sb.append("center pirmary node:" + Config.resource_node_centersPirmaryNode()).append(StringUtils.LF);
		sb.append("center pirmary port:" + Config.resource_node_centersPirmaryPort()).append(StringUtils.LF);
		sb.append("center pirmary sslEnable:" + Config.resource_node_centersPirmarySslEnable()).append(StringUtils.LF);
		sb.append("applications:").append(StringUtils.LF);
		sb.append(Config.resource_node_applications());
		logger.print(sb.toString());
		return true;
	}

}