package com.x.meeting.assemble.control.wrapout;

import com.x.base.core.gson.GsonPropertyObject;

public class WrapOutOpenMeeting extends GsonPropertyObject {
	private Long id;
	private String name;
	private String url;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
