package com.x.program.center.jaxrs.center;

import com.google.gson.JsonElement;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.ReportToCenter;
import com.x.base.core.project.schedule.ReportToCenter.Report;
import com.x.program.center.ThisApplication;

class ActionReportApplication extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionReportApplication.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Report report = this.convertToWrapIn(jsonElement, Report.class);
		ThisApplication.reportQueue.send(report);
		ReportToCenter.Echo echo = new ReportToCenter.Echo();
		echo.setApplicationsToken(ThisApplication.context().applications().getToken());
		Wo wo = XGsonBuilder.convert(echo, Wo.class);
		result.setData(wo);
		return result;
	}

	public static class Wo extends ReportToCenter.Echo {

	}

}