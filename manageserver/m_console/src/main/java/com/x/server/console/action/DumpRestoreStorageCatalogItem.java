package com.x.server.console.action;

import com.x.base.core.project.gson.GsonPropertyObject;

public class DumpRestoreStorageCatalogItem extends GsonPropertyObject {
	private Integer count;
	private Long size;
	private Integer invalidStorage;
	private Integer empty;
	private Integer normal;

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Integer getInvalidStorage() {
		return invalidStorage;
	}

	public void setInvalidStorage(Integer invalidStorage) {
		this.invalidStorage = invalidStorage;
	}

	public Integer getEmpty() {
		return empty;
	}

	public void setEmpty(Integer empty) {
		this.empty = empty;
	}

	public Integer getNormal() {
		return normal;
	}

	public void setNormal(Integer normal) {
		this.normal = normal;
	}

}
