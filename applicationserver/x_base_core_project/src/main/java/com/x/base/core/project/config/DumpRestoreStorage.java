package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class DumpRestoreStorage extends GsonPropertyObject {

	public static DumpRestoreStorage defaultInstance() {
		return new DumpRestoreStorage();
	}

	public DumpRestoreStorage() {
		this.includes = new ArrayList<String>();
		this.excludes = new ArrayList<String>();
		this.batchSize = 500;
		this.redistribute = true;
		this.exceptionInvalidStorage = true;
		// this.skipEmpty = false;
	}

	private List<String> includes;
	private List<String> excludes;
	private Integer batchSize;
	private Boolean redistribute;
	private Boolean exceptionInvalidStorage;
	// private Boolean skipEmpty;

	public List<String> getIncludes() {
		List<String> list = new ArrayList<>();
		for (String str : ListTools.trim(this.includes, true, true)) {
			list.add(str);
		}
		return list;
	}

	public List<String> getExcludes() {
		List<String> list = new ArrayList<>();
		for (String str : ListTools.trim(this.excludes, true, true)) {
			list.add(str);
		}
		return list;
	}

	public Boolean getRedistribute() {
		return BooleanUtils.isNotFalse(this.redistribute);
	}

	// public Boolean getSkipEmpty() {
	// return BooleanUtils.isTrue(this.skipEmpty);
	// }

	/** 默认为true */
	public Boolean getExceptionInvalidStorage() {
		return BooleanUtils.isNotFalse(this.exceptionInvalidStorage);
	}

	public Integer getBatchSize() {
		if ((null == this.batchSize) || (this.batchSize < 1)) {
			return 500;
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

	public void setRedistribute(Boolean redistribute) {
		this.redistribute = redistribute;
	}

	// public void setSkipEmpty(Boolean skipEmpty) {
	// this.skipEmpty = skipEmpty;
	// }

	public void setExceptionInvalidStorage(Boolean exceptionInvalidStorage) {
		this.exceptionInvalidStorage = exceptionInvalidStorage;
	}

}
