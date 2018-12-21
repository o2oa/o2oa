package com.x.base.core.project.config;

import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang3.BooleanUtils;

public class DataServers extends ConcurrentSkipListMap<String, DataServer> {

	private static final long serialVersionUID = 7784713403083443039L;

	public DataServers() {
		super();
	}

	public DataServers(Nodes nodeConfigs) {
		for (Entry<String, Node> o : nodeConfigs.entrySet()) {
			DataServer server = o.getValue().getData();
			if (null != server) {
				if (BooleanUtils.isTrue(server.getEnable())) {
					this.put(o.getKey(), server);
				}
			}
		}
	}
}
