package com.x.collaboration.assemble.websocket.jaxrs.online;

import com.x.base.core.project.http.WrapOutOnline;

public class ActionGetOnline extends BaseAction {

	public WrapOutOnline execute(String person) throws Exception {
		WrapOutOnline wrap = new WrapOutOnline();
		wrap.setPerson(person);
		if (this.getOnlineLocal(person)) {
			wrap.setOnlineStatus(WrapOutOnline.status_online);
		} else if (this.getOnLineRemote(person)) {
			wrap.setOnlineStatus(WrapOutOnline.status_online);
		} else {
			wrap.setOnlineStatus(WrapOutOnline.status_offline);
		}
		return wrap;
	}
}