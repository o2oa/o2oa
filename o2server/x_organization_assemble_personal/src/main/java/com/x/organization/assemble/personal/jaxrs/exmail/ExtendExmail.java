package com.x.organization.assemble.personal.jaxrs.exmail;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ExtendExmail extends GsonPropertyObject {

	private static final long serialVersionUID = -9170917871322581562L;

	public static final String TYPE = "exmail";

	private Long unreadCount;

	private List<String> titleList = new ArrayList<>();

	public Long getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(Long unreadCount) {
		this.unreadCount = unreadCount;
	}

	public List<String> getTitleList() {
		return titleList;
	}

	public void setTitleList(List<String> titleList) {
		this.titleList = titleList;
	}

}
