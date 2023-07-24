package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;

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

	public List<String> names() throws Exception {
		List<String> names = new ArrayList<>();
		int idx = 0;
		for (DataServer o : this.values()) {
			idx++;
			if (BooleanUtils.isTrue(o.getEnable())) {
				names.add("s" + ("" + (1000 + idx)).substring(1));
			}
		}
		return names;
	}

	public String name(DataServer dataServer) throws Exception {
		String name = "";
		int idx = 0;
		for (DataServer o : this.values()) {
			idx++;
			if (BooleanUtils.isTrue(o.getEnable()) && Objects.equals(o, dataServer)) {
				name = "s" + ("" + (1000 + idx)).substring(1);
				break;
			}
		}
		if (StringUtils.isEmpty(name)) {
			throw new Exception("dataServer not in dataServers.");
		}
		return name;
	}

	public List<String> findNamesOfContainerEntity(String className) throws Exception {
		List<String> names = new ArrayList<>();
		for (DataServer o : this.values()) {
			if (BooleanUtils.isTrue(o.getEnable())) {
				List<String> list = ListTools.toList(className);
				list = ListTools.includesExcludesWildcard(list, o.getIncludes(), o.getExcludes());
				if (!list.isEmpty()) {
					names.add(this.name(o));
				}
			}

		}
		return names;
	}

	public String log(String name) {
		int idx = 0;
		idx++;
		for (DataServer o : this.values()) {
			if (BooleanUtils.isTrue(o.getEnable())) {
				String n = "s" + ("" + (1000 + idx)).substring(1);
				if (StringUtils.equals(n, name)) {
					String value = o.getLogLevel();
					return "DefaultLevel=WARN, Tool=" + value + ", Enhance=" + value + ", METADATA=" + value
							+ ", Runtime=" + value + ", Query=" + value + ", DataCache=" + value + ", JDBC=" + value
							+ ", SQL=" + value;
				}
			}
		}
		return "DefaultLevel=WARN, Tool=WARN, Enhance=WARN, METADATA=WARN, Runtime=WARN, Query=WARN, DataCache=WARN, JDBC=ERROR, SQL=WARN";
	}

}
