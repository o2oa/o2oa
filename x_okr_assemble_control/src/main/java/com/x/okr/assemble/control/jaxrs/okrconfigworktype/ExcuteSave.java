package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.WorkTypeConfigSaveException;
import com.x.okr.entity.OkrConfigWorkType;

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, WrapInOkrConfigWorkType wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrConfigWorkType okrConfigWorkType = null;
		
		if( wrapIn != null ){
			try {
				okrConfigWorkType = okrConfigWorkTypeService.save( wrapIn );
				result.setData( new WrapOutId( okrConfigWorkType.getId() ));
				ApplicationCache.notify( OkrConfigWorkType.class );
			} catch (Exception e) {
				Exception exception = new WorkTypeConfigSaveException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
//		else{
//			result.error( new Exception( "请求传入的参数为空，无法继续保存!" ) );
//			result.setUserMessage( "请求传入的参数为空，无法继续保存!" );
//		}
		return result;
	}
	
}