package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoOperationService;

public class ExcuteRecycle extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteRecycle.class );
	private OkrWorkBaseInfoOperationService okrWorkBaseInfoOperationService = new OkrWorkBaseInfoOperationService();
	
	protected ActionResult<WrapOutOkrWorkBaseInfo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		if( id == null || id.isEmpty() ){
			Exception exception = new WorkIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}else{
			try{
				okrWorkBaseInfoOperationService.recycleWork( id );
			}catch(Exception e){
				Exception exception = new WorkRecycleException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}