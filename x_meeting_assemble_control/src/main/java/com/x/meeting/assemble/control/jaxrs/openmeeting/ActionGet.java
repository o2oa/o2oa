package com.x.meeting.assemble.control.jaxrs.openmeeting;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.http.ActionResult;
import com.x.base.core.project.server.Config;
import com.x.meeting.assemble.control.wrapout.WrapOutOpenMeeting;

public class ActionGet extends ActionBase {

	public ActionResult<WrapOutOpenMeeting> execute() throws Exception {
		ActionResult<WrapOutOpenMeeting> result = new ActionResult<>();
		WrapOutOpenMeeting wrap = new WrapOutOpenMeeting();
		wrap.setEnable(Config.meeting().getEnable());
		/* 如果启用了再加载 */
		if (BooleanUtils.isTrue(Config.meeting().getEnable())) {
			wrap.setRoomList(null);
			wrap.setHost(Config.meeting().getHost());
			wrap.setPort(Config.meeting().getPort());
			wrap.setOauth2Id(Config.meeting().getOauth2Id());
		}
		result.setData(wrap);
		return result;
	}

}
