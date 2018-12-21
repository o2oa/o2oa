package com.x.processplatform.assemble.bam.stub;

import com.x.base.core.project.gson.GsonPropertyObject;

public class UnitStub extends GsonPropertyObject {

	private String name;
	private String value;
	private Integer level;
	private String levelName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

}
