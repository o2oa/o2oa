package com.x.collaboration.assemble.websocket.jaxrs.online;

import java.util.List;

import com.x.base.core.project.http.WrapOutOnline;

public class ActionListOnlineAllLocal extends BaseAction {

	public List<WrapOutOnline> execute() throws Exception {
		return this.listOnlineAllLocal();
	}
}