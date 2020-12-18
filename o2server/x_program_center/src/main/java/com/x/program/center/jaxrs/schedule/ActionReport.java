package com.x.program.center.jaxrs.schedule;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.ScheduleLog;

class ActionReport extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = gson.fromJson(jsonElement, Wi.class);
		ScheduleLog o = Wi.copier.copy(wi);
		/** 默认使用传递过来的id,如果不存在那么重新赋值 */
		if (StringUtils.isEmpty(wi.getScheduleLogId())) {
			o.setId(StringTools.uniqueToken());
		} else {
			o.setId(wi.getScheduleLogId());
		}
		NameValuePair pair = new NameValuePair();
		pair.setName(ScheduleLog.class.getName());
		pair.setValue(o);
		ThisApplication.logQueue.send(pair);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wi extends ScheduleLog {

		private static final long serialVersionUID = 1996856138701159925L;
		static WrapCopier<Wi, ScheduleLog> copier = WrapCopierFactory.wi(Wi.class, ScheduleLog.class, null,
				JpaObject.FieldsUnmodify);

		private String scheduleLogId;

		public String getScheduleLogId() {
			return scheduleLogId;
		}

		public void setScheduleLogId(String scheduleLogId) {
			this.scheduleLogId = scheduleLogId;
		}

	}

	public static class Wo extends WrapBoolean {
	}

}
