package com.x.collaboration.assemble.websocket.jaxrs.online;

import java.util.List;

import com.x.base.core.project.http.WrapInStringList;
import com.x.base.core.project.http.WrapOutOnline;

public class ActionListOnlineLocal extends BaseAction {

	public List<WrapOutOnline> execute(WrapInStringList wrapIn) throws Exception {
		return this.listOnlineLocal(wrapIn);
	}
}