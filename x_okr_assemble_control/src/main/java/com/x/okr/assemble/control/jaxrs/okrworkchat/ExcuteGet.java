package com.x.okr.assemble.control.jaxrs.okrworkchat;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.okr.entity.OkrWorkChat;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutOkrWorkChat> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrWorkChat> result = new ActionResult<>();
		WrapOutOkrWorkChat wrap = null;
		OkrWorkChat okrWorkChat = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new WorkChatIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}else{
			try {
				okrWorkChat = okrWorkChatService.get( id );
				if( okrWorkChat != null ){
					wrap = wrapout_copier.copy( okrWorkChat );
					result.setData(wrap);
				}else{
					Exception exception = new WorkChatNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Throwable th) {
				Exception exception = new WorkChatQueryByIdException( th, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}