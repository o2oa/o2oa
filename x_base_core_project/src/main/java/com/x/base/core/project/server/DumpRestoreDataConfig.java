package com.x.base.core.project.server;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;

public class DumpRestoreDataConfig extends GsonPropertyObject {

	public DumpRestoreDataConfig() {

	}

	private List<String> includes;

	private List<String> excludes;

	public List<String> getIncludes() {
		return includes;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

}
