package com.x.okr.assemble.control.jaxrs.okrtaskhandled;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.TaskHandledIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.TaskHandledNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.TaskHandledQueryByIdException;
import com.x.okr.entity.OkrTaskHandled;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutOkrTaskHandled> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrTaskHandled> result = new ActionResult<>();
		WrapOutOkrTaskHandled wrap = null;
		OkrTaskHandled okrTaskHandled = null;
		Boolean check = true;
		if(check){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new TaskHandledIdEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				okrTaskHandled = okrTaskHandledService.get( id );
				if( okrTaskHandled != null ){
					wrap = wrapout_copier.copy( okrTaskHandled );
					result.setData(wrap);
				}else{
					Exception exception = new TaskHandledNotExistsException( id );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				Exception exception = new TaskHandledQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}