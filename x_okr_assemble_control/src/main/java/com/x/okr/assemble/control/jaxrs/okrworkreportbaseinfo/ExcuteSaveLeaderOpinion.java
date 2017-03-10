package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ExcuteSaveLeaderOpinion extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSaveLeaderOpinion.class );

	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, WrapInOkrWorkReportBaseInfo wrapIn, String processorIdentity ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrWorkReportBaseInfo okrWorkReportBaseInfo  = null;
		boolean check = true;
		
		if( check ){
			//校验工作ID是否存在
			if( wrapIn.getOpinion() == null || wrapIn.getOpinion().isEmpty() ){
				check = false;
				Exception exception = new LeaderOpinionEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( wrapIn.getId() == null || wrapIn.getId().isEmpty() ){
				check = false;
				Exception exception = new WorkReportIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try{
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( wrapIn.getId() );
				if( okrWorkReportBaseInfo == null ){
					check = false;
					Exception exception = new WorkReportNotExistsException( wrapIn.getId() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new WorkReportQueryByIdException( e, wrapIn.getId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				okrWorkReportOperationService.saveLeaderOpinionInfo( okrWorkReportBaseInfo, wrapIn.getOpinion(), processorIdentity );			
				result.setData( new WrapOutId( okrWorkReportBaseInfo.getId() ));
			} catch (Exception e) {
				check = false;
				Exception exception = new LeaderOpinionSaveException( e, wrapIn.getId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}