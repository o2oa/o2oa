package com.x.base.core.http;

import com.x.base.core.gson.GsonPropertyObject;

public class WrapOutOnline extends GsonPropertyObject {

	public final static String status_online = "online";
	public final static String status_offline = "offline";

	private String person;

	private String onlineStatus;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

}
