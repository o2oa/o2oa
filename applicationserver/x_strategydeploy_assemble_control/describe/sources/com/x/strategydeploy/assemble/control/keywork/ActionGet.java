package com.x.strategydeploy.assemble.control.keywork;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.measures.exception.ExceptionMeasuresInfoIdEmpty;
import com.x.strategydeploy.assemble.control.service.KeyworkInfoQueryService;
import com.x.strategydeploy.core.entity.KeyworkInfo;

public class ActionGet extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		KeyworkInfo keyworkinfo = new KeyworkInfo();
		Wo wrapOut = null;
		boolean IsPass = true;

		if (null == id || id.isEmpty()) {
			IsPass = false;
			logger.info("重点工作 ID 为空。");
			Exception exception = new ExceptionMeasuresInfoIdEmpty();
			result.error(exception);
		}

		if (IsPass) {
			KeyworkInfoQueryService keyworkInfoQueryService = new KeyworkInfoQueryService();
			keyworkinfo = keyworkInfoQueryService.get(id);
			wrapOut = Wo.copier.copy(keyworkinfo);
			List<String> _ids = wrapOut.getMeasureslist();
			if (null != _ids && _ids.size() >= 1) {
				List<String> _titles;
				try {
					_titles = keyworkInfoQueryService.getMeasuresTitleListByIds(_ids);
					wrapOut.setMeasurestitlelist(_titles);
				} catch (Exception e) {
					throw e;
				}
			}			
			result.setData(wrapOut);
		}
		return result;
	}
}