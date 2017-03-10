package com.x.okr.assemble.control.jaxrs.okrconfigworklevel;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.okr.entity.OkrConfigWorkLevel;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutOkrConfigWorkLevel> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrConfigWorkLevel> result = new ActionResult<>();
		WrapOutOkrConfigWorkLevel wrap = null;
		OkrConfigWorkLevel okrConfigWorkLevel = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new WorkLevelConfigIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}else{
			try {
				okrConfigWorkLevel = okrConfigWorkLevelService.get( id );
				if( okrConfigWorkLevel != null ){
					wrap = wrapout_copier.copy( okrConfigWorkLevel );
					result.setData(wrap);
				}else{
					Exception exception = new WorkLevelConfigNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Throwable th) {
				Exception exception = new WorkLevelConfigQueryByIdException( th, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}