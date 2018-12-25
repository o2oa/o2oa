package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionAdminSuperviseInfoEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionAdminSuperviseSave;

public class ActionSaveAdminSupervise extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSaveAdminSupervise.class );

	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String reportId, String adminSuperviseInfo ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		boolean check = true;
		
		//校验工作ID是否存在
		if( adminSuperviseInfo == null || adminSuperviseInfo.isEmpty() ){
			check = false;
			Exception exception = new ExceptionAdminSuperviseInfoEmpty();
			result.error( exception );
		}
		
		if( check ){
			try {
				okrWorkReportOperationService.saveAdminSuperviseInfo( reportId, adminSuperviseInfo );
				result.setData( new Wo( reportId ));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAdminSuperviseSave( e, reportId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
}