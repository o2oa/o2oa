package com.x.okr.assemble.control.jaxrs.okrconfigworklevel;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrconfigworklevel.exception.WorkLevelConfigSaveException;
import com.x.okr.entity.OkrConfigWorkLevel;

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, WrapInOkrConfigWorkLevel wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrConfigWorkLevel okrConfigWorkLevel = null;

		if( wrapIn != null ){
			try {
				okrConfigWorkLevel = okrConfigWorkLevelService.save( wrapIn );
				result.setData( new WrapOutId( okrConfigWorkLevel.getId() ));
			} catch (Exception e) {
				Exception exception = new WorkLevelConfigSaveException( e );
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