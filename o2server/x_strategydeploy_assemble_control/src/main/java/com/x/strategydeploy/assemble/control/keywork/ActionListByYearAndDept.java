package com.x.strategydeploy.assemble.control.keywork;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.service.KeyworkInfoQueryService;
import com.x.strategydeploy.core.entity.KeyworkInfo;

public class ActionListByYearAndDept extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionListByYearAndDept.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String year, String dept) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
		KeyworkInfoQueryService keyworkInfoQueryService = new KeyworkInfoQueryService();
		List<KeyworkInfo> measuresinfoList = new ArrayList<KeyworkInfo>();
		List<Wo> wrapOutList = new ArrayList<Wo>();
		measuresinfoList = keyworkInfoQueryService.getListByYearAndDept(year, dept);
		wrapOutList = Wo.copier.copy(measuresinfoList);
		result.setData(wrapOutList);
		return result;
	}
}
