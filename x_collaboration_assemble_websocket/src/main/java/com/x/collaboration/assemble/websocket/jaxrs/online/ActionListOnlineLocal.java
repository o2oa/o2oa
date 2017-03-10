package com.x.collaboration.assemble.websocket.jaxrs.online;

import java.util.List;

import com.x.base.core.http.WrapInStringList;
import com.x.base.core.http.WrapOutOnline;

public class ActionListOnlineLocal extends ActionBase {

	public List<WrapOutOnline> execute(WrapInStringList wrapIn) throws Exception {
		return this.listOnlineLocal(wrapIn);
	}
}