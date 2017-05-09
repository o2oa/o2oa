package com.x.okr.assemble.control.jaxrs.okrtask;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.TaskIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.TaskNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.TaskQueryByIdException;
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
				//logger.error( e, effectivePerson, request, null);
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
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				Exception exception = new TaskQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}	
		return result;
	}
	
}