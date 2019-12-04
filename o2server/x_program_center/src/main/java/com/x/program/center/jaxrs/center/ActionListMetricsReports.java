package com.x.program.center.jaxrs.center;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.program.center.ThisApplication;

import java.util.Map;

class ActionListMetricsReports extends BaseAction {

	ActionResult<Map> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Map> result = new ActionResult<>();
		result.setData(ThisApplication.metricsReportMap);
		return result;
	}
}