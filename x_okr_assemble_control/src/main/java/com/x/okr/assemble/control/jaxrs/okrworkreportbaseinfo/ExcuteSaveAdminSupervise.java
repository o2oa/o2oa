package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.AdminSuperviseInfoEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.AdminSuperviseSaveException;

public class ExcuteSaveAdminSupervise extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSaveAdminSupervise.class );

	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, String reportId, String adminSuperviseInfo ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		boolean check = true;
		
		//校验工作ID是否存在
		if( adminSuperviseInfo == null || adminSuperviseInfo.isEmpty() ){
			check = false;
			Exception exception = new AdminSuperviseInfoEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			try {
				okrWorkReportOperationService.saveAdminSuperviseInfo( reportId, adminSuperviseInfo );
				result.setData( new WrapOutId( reportId ));
			} catch (Exception e) {
				check = false;
				Exception exception = new AdminSuperviseSaveException( e, reportId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}