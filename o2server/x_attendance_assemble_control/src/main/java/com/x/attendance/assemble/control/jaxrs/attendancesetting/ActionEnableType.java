package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.attendance.assemble.control.Business;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.organization.OrganizationDefinition;


class ActionEnableType extends BaseAction {

	public static final String TYPE_QIYEWEIXIN = "qywx";
	public static final String TYPE_DINGDING = "dingding";

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.Manager, OrganizationDefinition.AttendanceManager))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
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
	}

	public static class Wo extends WrapString {

	}

}
