package com.x.meeting.assemble.control.wrapout;

import java.util.List;

import com.x.base.core.bean.NameIdPair;
import com.x.base.core.gson.GsonPropertyObject;

public class WrapOutOpenMeeting extends GsonPropertyObject {

	private Boolean enable;
	private String host;
	private Integer port;
	private String oauth2Id;
	private List<NameIdPair> roomList;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public List<NameIdPair> getRoomList() {
		return roomList;
	}

	public void setRoomList(List<NameIdPair> roomList) {
		this.roomList = roomList;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getOauth2Id() {
		return oauth2Id;
	}

	public void setOauth2Id(String oauth2Id) {
		this.oauth2Id = oauth2Id;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

}
