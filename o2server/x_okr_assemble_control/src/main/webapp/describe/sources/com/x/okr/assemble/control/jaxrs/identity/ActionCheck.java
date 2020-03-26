package com.x.okr.assemble.control.jaxrs.identity;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionCheck extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionCheck.class );
	
	protected ActionResult<WrapOutString> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutString> result = new ActionResult<>();
		try {
			WrapOutString wrapOutString = new WrapOutString();
			okrSystemIdentityOperatorService.checkAllAbnormalIdentityInSystem();
			result.setData(wrapOutString);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system check identity got an exception.");
			logger.error(e, effectivePerson, request, null);
		}
		return result;
	}
	
}