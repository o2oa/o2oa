package com.x.base.core.project.config;

import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang3.BooleanUtils;

public class StorageServers extends ConcurrentSkipListMap<String, StorageServer> {

	private static final long serialVersionUID = -674719411318974604L;

	public StorageServers() {
		super();
	}

	public StorageServers(Nodes nodeConfigs) {
		for (Entry<String, Node> o : nodeConfigs.entrySet()) {
			StorageServer server = o.getValue().getStorage();
			if (null != server) {
				if (BooleanUtils.isTrue(server.getEnable())) {
					this.put(o.getKey(), server);
				}
			}
		}
	}
}
