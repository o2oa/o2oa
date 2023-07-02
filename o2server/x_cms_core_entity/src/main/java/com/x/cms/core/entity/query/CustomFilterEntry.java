package com.x.cms.core.entity.query;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class CustomFilterEntry extends GsonPropertyObject {

	private String path;

	private String title;

	private FormatType formatType;

	public Boolean available() {
		if (StringUtils.isEmpty(path)) {
			return false;
		}
		if (StringUtils.isEmpty(title)) {
			return false;
		}
		if (null == formatType) {
			return false;
		}
		return false;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public FormatType getFormatType() {
		return formatType;
	}

	public void setFormatType(FormatType formatType) {
		this.formatType = formatType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
