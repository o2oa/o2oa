package com.x.base.core.project.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.gson.GsonPropertyObject;

public class DumpRestoreStorage extends GsonPropertyObject {

	public static DumpRestoreStorage defaultInstance() {
		return new DumpRestoreStorage();
	}

	public DumpRestoreStorage() {
		this.includes = new ArrayList<StorageType>();
		this.excludes = new ArrayList<StorageType>();
		this.redistribute = false;
	}

	private List<StorageType> includes;
	private List<StorageType> excludes;
	private Boolean redistribute;

	public List<StorageType> getIncludes() {
		return (null == includes) ? new ArrayList<StorageType>() : includes;
	}

	public List<StorageType> getExcludes() {
		return (null == excludes) ? new ArrayList<StorageType>() : excludes;
	}

	public Boolean getRedistribute() {
		return BooleanUtils.isTrue(this.redistribute);
	}

	public void setIncludes(List<StorageType> includes) {
		this.includes = includes;
	}

	public void setExcludes(List<StorageType> excludes) {
		this.excludes = excludes;
	}

	public void setRedistribute(Boolean redistribute) {
		this.redistribute = redistribute;
	}

}
