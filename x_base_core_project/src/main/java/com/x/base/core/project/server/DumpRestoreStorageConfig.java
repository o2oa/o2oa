package com.x.base.core.project.server;

import java.util.List;

import com.x.base.core.entity.StorageType;
import com.x.base.core.gson.GsonPropertyObject;

public class DumpRestoreStorageConfig extends GsonPropertyObject {

	public DumpRestoreStorageConfig() {

	}

	private List<StorageType> includes;

	private List<StorageType> excludes;

	private Boolean redistribute;

	public List<StorageType> getIncludes() {
		return includes;
	}

	public void setIncludes(List<StorageType> includes) {
		this.includes = includes;
	}

	public List<StorageType> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<StorageType> excludes) {
		this.excludes = excludes;
	}

	public Boolean getRedistribute() {
		return redistribute;
	}

	public void setRedistribute(Boolean redistribute) {
		this.redistribute = redistribute;
	}

}
