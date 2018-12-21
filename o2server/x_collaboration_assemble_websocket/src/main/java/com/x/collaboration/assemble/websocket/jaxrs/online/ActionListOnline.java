package com.x.collaboration.assemble.websocket.jaxrs.online;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.WrapInStringList;
import com.x.base.core.project.http.WrapOutOnline;
import com.x.base.core.project.tools.ListTools;

public class ActionListOnline extends BaseAction {

	public List<WrapOutOnline> execute(WrapInStringList wrapIn) throws Exception {
		List<WrapOutOnline> wraps = new ArrayList<>();
		if (ListTools.isNotEmpty(wrapIn.getValueList())) {
			for (String str : wrapIn.getValueList()) {
				WrapOutOnline o = new WrapOutOnline();
				o.setPerson(str);
				o.setOnlineStatus(WrapOutOnline.status_offline);
				wraps.add(o);
			}
			for (WrapOutOnline o : this.listOnlineLocal(wrapIn)) {
				if (StringUtils.equals(o.getOnlineStatus(), WrapOutOnline.status_online)) {
					updateOnline(wraps, o.getPerson());
				}
			}
			for (WrapOutOnline o : this.listOnLineRemote(wrapIn)) {
				if (StringUtils.equals(o.getOnlineStatus(), WrapOutOnline.status_online)) {
					updateOnline(wraps, o.getPerson());
				}
			}
		}
		return wraps;
	}

	private void updateOnline(List<WrapOutOnline> wraps, String person) {
		for (WrapOutOnline wrap : wraps) {
			if (StringUtils.equals(wrap.getPerson(), person)) {
				wrap.setOnlineStatus(WrapOutOnline.status_online);
				break;
			}
		}
	}
}