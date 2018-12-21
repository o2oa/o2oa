package com.x.collaboration.assemble.websocket.jaxrs.online;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.x.base.core.project.http.WrapOutOnline;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;

public class ActionListOnlineAll extends BaseAction {

	public List<WrapOutOnline> execute() throws Exception {
		Set<String> set = new HashSet<>();
		set.addAll(ListTools.extractProperty(this.listOnlineAllLocal(), "person", String.class, true, true));
		set.addAll(ListTools.extractProperty(this.listOnLineAllRemote(), "person", String.class, true, true));
		List<WrapOutOnline> wraps = new ArrayList<>();
		for (String str : set) {
			WrapOutOnline wrap = new WrapOutOnline();
			wrap.setPerson(str);
			wrap.setOnlineStatus(WrapOutOnline.status_online);
			wraps.add(wrap);
		}
		SortTools.asc(wraps, false, "person");
		return wraps;
	}
}