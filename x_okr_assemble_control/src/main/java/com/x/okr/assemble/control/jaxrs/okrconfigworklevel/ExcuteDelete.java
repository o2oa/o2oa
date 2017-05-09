package com.x.okr.assemble.control.jaxrs.okrconfigworklevel;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrconfigworklevel.exception.WorkLevelConfigDeleteException;
import com.x.okr.assemble.control.jaxrs.okrconfigworklevel.exception.WorkLevelConfigIdEmptyException;

public class ExcuteDelete extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		if( id == null || id.isEmpty() ){
			Exception exception = new WorkLevelConfigIdEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}else{
			try{
				okrConfigWorkLevelService.delete( id );
				result.setData( new WrapOutId(id));
			}catch(Exception e){
				Exception exception = new WorkLevelConfigDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}