package com.x.strategydeploy.assemble.control.measures;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.measures.exception.ExceptionMeasuresInfoIdEmpty;
import com.x.strategydeploy.assemble.control.service.MeasuresInfoQueryService;
import com.x.strategydeploy.core.entity.MeasuresInfo;

public class ActionGet extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		MeasuresInfo measuresinfo = new MeasuresInfo();
		Wo wrapOut = null;
		boolean IsPass = true;

		if (null == id || id.isEmpty()) {
			IsPass = false;
			logger.info("战略部署 ID 为空。");
			Exception exception = new ExceptionMeasuresInfoIdEmpty();
			result.error(exception);
		}

		if (IsPass) {
			MeasuresInfoQueryService StrategyDeployQueryService = new MeasuresInfoQueryService();
			measuresinfo = StrategyDeployQueryService.get(id);
			wrapOut = Wo.copier.copy(measuresinfo);
			result.setData(wrapOut);
		}

		return result;
	}
}