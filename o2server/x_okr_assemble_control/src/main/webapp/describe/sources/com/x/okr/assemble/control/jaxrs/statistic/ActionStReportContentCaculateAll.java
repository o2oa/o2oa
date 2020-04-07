package com.x.okr.assemble.control.jaxrs.statistic;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.service.ExcuteSt_WorkReportContentService;

public class ActionStReportContentCaculateAll extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionStReportContentCaculateAll.class);

	protected ActionResult<WrapOutString> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutString> result = new ActionResult<>();
		try {
			new ExcuteSt_WorkReportContentService().executeAll();
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "OKR_St_WorkReportContent completed and excute got an exception." );
			logger.error( e, effectivePerson, request, null);
		}
		return result;
	}

}