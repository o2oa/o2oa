package com.x.server.console;

import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.gson.GsonPropertyObject;

public class Version extends GsonPropertyObject {

	private String name;
	private String description;
	private Long size;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

 

	public Boolean after(Version o) {
		return after(o.name);
	}

	public Boolean after(String str) {
		if (NumberUtils.isNumber(str)) {
			Double d1 = NumberUtils.toDouble(this.name);
			Double d2 = NumberUtils.toDouble(str);
			return d1 > d2;
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
