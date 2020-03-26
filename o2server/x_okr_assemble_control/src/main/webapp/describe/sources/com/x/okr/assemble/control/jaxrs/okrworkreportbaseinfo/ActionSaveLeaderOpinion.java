package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionLeaderOpinionEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionLeaderOpinionSave;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportQueryById;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ActionSaveLeaderOpinion extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSaveLeaderOpinion.class );

	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, WiOkrWorkReportBaseInfo wrapIn, String processorIdentity ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrWorkReportBaseInfo okrWorkReportBaseInfo  = null;
		boolean check = true;
		
		if( check ){
			//校验工作ID是否存在
			if( wrapIn.getOpinion() == null || wrapIn.getOpinion().isEmpty() ){
				check = false;
				Exception exception = new ExceptionLeaderOpinionEmpty();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( wrapIn.getId() == null || wrapIn.getId().isEmpty() ){
				check = false;
				Exception exception = new ExceptionWorkReportIdEmpty();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try{
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( wrapIn.getId() );
				if( okrWorkReportBaseInfo == null ){
					check = false;
					Exception exception = new ExceptionWorkReportNotExists( wrapIn.getId() );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionWorkReportQueryById( e, wrapIn.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				okrWorkReportOperationService.saveLeaderOpinionInfo( okrWorkReportBaseInfo, wrapIn.getOpinion(), processorIdentity );			
				result.setData( new Wo( okrWorkReportBaseInfo.getId() ));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionLeaderOpinionSave( e, wrapIn.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
}