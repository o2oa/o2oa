package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class DumpRestoreData extends GsonPropertyObject {

	public static DumpRestoreData defaultInstance() {
		return new DumpRestoreData();
	}

	public DumpRestoreData() {
		this.includes = new ArrayList<String>();
		this.excludes = new ArrayList<String>();
		this.batchSize = 2000;
	}

	private List<String> includes;
	private List<String> excludes;
	private Integer batchSize;

	public List<String> getIncludes() {
		List<String> list = new ArrayList<>();
		for (String str : ListTools.trim(includes, true, true)) {
			list.add(str);
		}
		return list;
	}

	public List<String> getExcludes() {
		List<String> list = new ArrayList<>();
		for (String str : ListTools.trim(excludes, true, true)) {
			list.add(str);
		}
		return list;
	}

	public Integer getBatchSize() {
		if ((null == this.batchSize) || (this.batchSize < 1)) {
			return 2000;
		}
		return this.batchSize;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

}
