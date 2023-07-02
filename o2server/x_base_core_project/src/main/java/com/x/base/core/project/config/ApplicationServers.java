package com.x.base.core.project.config;

import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang3.BooleanUtils;

public class ApplicationServers extends ConcurrentSkipListMap<String, ApplicationServer> {

	private static final long serialVersionUID = 3089494214242890204L;

	public ApplicationServers() {
		super();
	}

	public ApplicationServers(Nodes nodeConfigs) {
		for (Entry<String, Node> o : nodeConfigs.entrySet()) {
			ApplicationServer server = o.getValue().getApplication();
			if (null != server) {
				if (BooleanUtils.isTrue(server.getEnable())) {
					this.put(o.getKey(), server);
				}
			}
		}
	}
}
