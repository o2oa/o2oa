package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.project.tools.ListTools;

public class WebServers extends ConcurrentSkipListMap<String, WebServer> {

	private static final long serialVersionUID = -706102090064680898L;

	public WebServers() {
		super();
	}

	public WebServers(Nodes nodeConfigs) {
		for (Entry<String, Node> o : nodeConfigs.entrySet()) {
			WebServer server = o.getValue().getWeb();
			if (null != server) {
				if (BooleanUtils.isTrue(server.getEnable())) {
					this.put(o.getKey(), server);
				}
			}
		}
	}

	public Entry<String, WebServer> getRandom() throws Exception {
		List<Entry<String, WebServer>> list = new ArrayList<>();
		for (Entry<String, WebServer> o : this.entrySet()) {
			if (BooleanUtils.isTrue(o.getValue().getEnable())) {
				list.add(o);
			}
		}
		if (ListTools.isEmpty(list)) {
			return null;
		}
		this.sortWithWeight(list);
		int total = 0;
		for (Entry<String, WebServer> o : list) {
			total += o.getValue().getWeight();
		}
		Random random = new Random();
		int rdm = random.nextInt(total);
		int current = 0;
		for (Entry<String, WebServer> o : list) {
			current += o.getValue().getWeight();
			if (rdm <= current) {
				return o;
			}
		}
		throw new Exception("randomWithWeight error.");
	}

	private void sortWithWeight(List<Entry<String, WebServer>> list) {
		Collections.sort(list, new Comparator<Entry<String, WebServer>>() {
			public int compare(Entry<String, WebServer> o1, Entry<String, WebServer> o2) {
				return ObjectUtils.compare(o1.getValue().getWeight(), o2.getValue().getWeight(), true);
			}
		});
	}

}
