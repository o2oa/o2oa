package com.x.strategydeploy.assemble.control.strategy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.service.StrategyDeployQueryService;
import com.x.strategydeploy.assemble.control.strategy.exception.ExceptionStrategyDeployIdEmpty;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class ActionGet extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		StrategyDeploy strategydeploy = new StrategyDeploy();
		Wo wrapOut = null;
		boolean IsPass = true;

		if (null == id || id.isEmpty()) {
			IsPass = false;
			logger.info("战略部署 ID 为空。");
			Exception exception = new ExceptionStrategyDeployIdEmpty();
			result.error(exception);
		}

		if (IsPass) {
			StrategyDeployQueryService StrategyDeployQueryService = new StrategyDeployQueryService();
			strategydeploy = StrategyDeployQueryService.get(id);
			wrapOut = Wo.copier.copy(strategydeploy);
			
			List<String> actions = new ArrayList<>();
			actions.add("OPEN");
			actions.add("EDIT");
			actions.add("DELETE");
			wrapOut.setActions(actions);
			result.setData(wrapOut);
		}

		return result;
	}
}
