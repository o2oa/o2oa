package com.x.okr.assemble.control.jaxrs.okrtask;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.okr.entity.OkrTask;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutOkrTask> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrTask> result = new ActionResult<>();
		WrapOutOkrTask wrap = null;
		OkrTask okrTask = null;
		Boolean check = true;
		if(check){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new TaskIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				okrTask = okrTaskService.get( id );
				if( okrTask != null ){
					wrap = wrapout_copier.copy( okrTask );
					result.setData(wrap);
				}else{
					Exception exception = new TaskNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Throwable th) {
				Exception exception = new TaskQueryByIdException( th, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}	
		return result;
	}
	
}