package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.service.ExcuteWorkReportCreateService;

public class ActionAutoCreateWorkReport extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionAutoCreateWorkReport.class );

	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request,EffectivePerson effectivePerson ) throws Exception {
		WrapOutBoolean wrapOutBoolean = new WrapOutBoolean();
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try {
			new ExcuteWorkReportCreateService().execute();
			wrapOutBoolean.setValue( true );
			result.setData( wrapOutBoolean );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute reportAutoCreate got an exception. " );
			logger.error( e );
		}
		return result;
	}
}