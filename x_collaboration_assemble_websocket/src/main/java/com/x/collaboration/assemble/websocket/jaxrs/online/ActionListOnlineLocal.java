package com.x.collaboration.assemble.websocket.jaxrs.online;

import java.util.List;

import com.x.base.core.http.WrapInListString;
import com.x.base.core.http.WrapOutOnline;

public class ActionListOnlineLocal extends ActionBase {

	public List<WrapOutOnline> execute(WrapInListString wrapIn) throws Exception {
		return this.listOnlineLocal(wrapIn);
	}
}