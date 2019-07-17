package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.factory.SlicePropertiesBuilder;
import com.x.base.core.project.tools.ListTools;

public class ExternalDataSources extends CopyOnWriteArrayList<ExternalDataSource> {

	private static final long serialVersionUID = 4502077979125945875L;

	public static ExternalDataSources defaultInstance() {
		return new ExternalDataSources();
	}

	public ExternalDataSources() {
		super();
	}

	public Boolean enable() {
		if (this.isEmpty()) {
			return false;
		}
		for (ExternalDataSource o : this) {
			if (BooleanUtils.isTrue(o.getEnable())) {
				return true;
			}
		}
		return false;

	}

	public String name(ExternalDataSource externalDataSource) throws Exception {
		String name = "";
		int idx = 0;
		for (ExternalDataSource o : this) {
			idx++;
			if (BooleanUtils.isTrue(o.getEnable()) && Objects.equals(o, externalDataSource)) {
				name = "s" + ("" + (1000 + idx)).substring(1);
				break;
			}
		}
		if (StringUtils.isEmpty(name)) {
			throw new Exception("externalDataSource not in externalDataSources.");
		}
		return name;
	}

	public List<String> names() throws Exception {
		List<String> names = new ArrayList<>();
		int idx = 0;
		for (ExternalDataSource o : this) {
			idx++;
			if (BooleanUtils.isTrue(o.getEnable())) {
				names.add("s" + ("" + (1000 + idx)).substring(1));
			}
		}
		return names;
	}

	public List<String> findNamesOfContainerEntity(String className) throws Exception {
		List<String> names = new ArrayList<>();
		for (ExternalDataSource o : this) {
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
		for (ExternalDataSource o : this) {
			idx++;
			if (BooleanUtils.isTrue(o.getEnable())) {
				String n = "s" + ("" + (1000 + idx)).substring(1);
				if (StringUtils.equals(n, name)) {
					String value = o.getLogLevel().toString();
					return "DefaultLevel=WARN, Tool=" + value + ", Enhance=" + value + ", METADATA=" + value
							+ ", Runtime=" + value + ", Query=" + value + ", DataCache=" + value + ", JDBC=" + value
							+ ", SQL=" + value;
				}
			}
		}
		return "DefaultLevel=WARN, Tool=WARN, Enhance=WARN, METADATA=WARN, Runtime=WARN, Query=WARN, DataCache=WARN, JDBC=ERROR, SQL=WARN";
	}

	public String dictionary() throws Exception {
		for (ExternalDataSource o : this) {
			if (BooleanUtils.isTrue(o.getEnable())) {
				return o.getDictionary();
			}
		}
		throw new Exception("dictionary error.");
	}

	public boolean hasSchema() throws Exception {
		for (ExternalDataSource o : this) {
			if (BooleanUtils.isTrue(o.getEnable())) {
				return SlicePropertiesBuilder.hasSchemaOfUrl(o.getUrl());
			}
		}
		throw new Exception("hasSchema error.");
	}

}
