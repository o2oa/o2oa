package com.x.okr.assemble.control.jaxrs.okrtaskhandled;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
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
				logger.error( exception, effectivePerson, request, null);
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
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Throwable th) {
				Exception exception = new TaskHandledQueryByIdException( th, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}