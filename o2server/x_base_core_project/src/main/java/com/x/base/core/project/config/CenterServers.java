package com.x.base.core.project.config;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

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

//	public List<Entry<String, CenterServer>> orderedEntry() {
//		List<Entry<String, CenterServer>> list = new ArrayList<>();
//		this.entrySet().stream().sorted((o1, o2) -> {
//			return o1.getValue().getOrder().compareTo(o2.getValue().getOrder());
//		}).forEachOrdered(o -> {
//			list.add(o);
//		});
//		return list;
//	}

	public List<Map.Entry<String, CenterServer>> orderedEntry() {
		Comparator<Entry<String, CenterServer>> compareOrder = Comparator.comparing(o -> o.getValue().getOrder(),
				Comparator.nullsLast(Comparator.naturalOrder()));
		Comparator<Entry<String, CenterServer>> compareName = Comparator.comparing(Map.Entry::getKey,
				Comparator.nullsLast(Comparator.naturalOrder()));
		return this.entrySet().stream().sorted(compareOrder.thenComparing(compareName)).collect(Collectors.toList());
	}

	public Entry<String, CenterServer> first() {

		List<Entry<String, CenterServer>> list = orderedEntry();

		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}

	}

	public Entry<String, CenterServer> findByNode(String node) {

		for (Entry<String, CenterServer> entry : this.entrySet()) {
			if (entry.getKey().equals(node)) {
				return entry;
			}
		}
		return null;
	}

	public CenterServer pirmary() throws Exception {
		return this.get(Config.resource_node_centersPirmaryNode());
	}
}