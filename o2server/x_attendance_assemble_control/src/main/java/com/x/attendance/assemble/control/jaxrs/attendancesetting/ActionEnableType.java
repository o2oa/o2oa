package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;


class ActionEnableType extends BaseAction {

	public static final String TYPE_QIYEWEIXIN = "qywx";
	public static final String TYPE_DINGDING = "dingding";

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		if (Config.qiyeweixin().getEnable() && Config.qiyeweixin().getAttendanceSyncEnable()) {
			wo.setValue(TYPE_QIYEWEIXIN);
		} else if (Config.dingding().getEnable() && Config.dingding().getAttendanceSyncEnable()) {
			wo.setValue(TYPE_DINGDING);
		} else {
			wo.setValue("");
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapString {

	}

}
