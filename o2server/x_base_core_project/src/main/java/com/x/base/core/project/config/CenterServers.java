package com.x.base.core.project.config;

import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class CenterServers extends ConcurrentSkipListMap<String, CenterServer> {

	private static final long serialVersionUID = 220144952029277793L;

	public CenterServers() {
		super();
	}

	public CenterServers(Nodes nodeConfigs) {
		for (Entry<String, Node> o : nodeConfigs.entrySet()) {
			CenterServer server = o.getValue().getCenter();
			if ((null != server) && BooleanUtils.isTrue(server.getEnable())) {
				this.put(o.getKey(), server);
			}
		}
	}

	public ListOrderedSet<Entry<String, CenterServer>> orderedEntrySet() {
		ListOrderedSet<Entry<String, CenterServer>> set = new ListOrderedSet<>();
		this.entrySet().stream().sorted((o1, o2) -> {
			return o1.getValue().getOrder() - o2.getValue().getOrder();
		}).forEachOrdered(o -> {
			set.add(o);
		});
		return set;
	}

	public CenterServer first() {

		ListOrderedSet<Entry<String, CenterServer>> set = orderedEntrySet();

		if (set.isEmpty()) {
			return null;
		} else {
			return set.get(0).getValue();
		}

	}

	public CenterServer pirmary() throws Exception {
		return this.get(Config.resource_node_centersPirmaryNode());
	}
}