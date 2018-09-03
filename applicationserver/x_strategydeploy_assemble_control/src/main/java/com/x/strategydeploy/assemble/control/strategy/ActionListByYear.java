package com.x.strategydeploy.assemble.control.strategy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.service.StrategyDeployOperationService;
import com.x.strategydeploy.assemble.control.service.StrategyDeployQueryService;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class ActionListByYear extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionListByYear.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String year) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
		StrategyDeployQueryService strategyDeployQueryService = new StrategyDeployQueryService();
		StrategyDeployOperationService strategyDeployOperationService = new StrategyDeployOperationService();
		List<StrategyDeploy> strategydeployList = new ArrayList<StrategyDeploy>();
		List<Wo> wrapOutList = new ArrayList<Wo>();
		strategydeployList = strategyDeployQueryService.getListByYear(year);
		wrapOutList = Wo.copier.copy(strategydeployList);
		wrapOutList = strategyDeployOperationService.setActions(wrapOutList,effectivePerson);
		result.setData(wrapOutList);
		return result;
	}
}
