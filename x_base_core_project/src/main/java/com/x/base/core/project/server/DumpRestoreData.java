package com.x.base.core.project.server;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.utils.ClassTools;
import com.x.base.core.utils.ListTools;

public class DumpRestoreData extends GsonPropertyObject {

	public static DumpRestoreData defaultInstance() {
		return new DumpRestoreData();
	}

	public DumpRestoreData() {
		this.includes = new ArrayList<String>();
		this.excludes = new ArrayList<String>();
	}

	private List<String> includes;
	private List<String> excludes;

	public List<String> getIncludes() {
		List<String> list = new ArrayList<>();
		for (String str : ListTools.trim(includes, true, true)) {
			if (ClassTools.isClass(str)) {
				list.add(str);
			}
		}
		return list;
	}

	public List<String> getExcludes() {
		List<String> list = new ArrayList<>();
		for (String str : ListTools.trim(excludes, true, true)) {
			if (ClassTools.isClass(str)) {
				list.add(str);
			}
		}
		return list;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

}
