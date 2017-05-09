package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.WorkTypeConfigDeleteException;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.WorkTypeConfigIdEmptyException;
import com.x.okr.entity.OkrConfigWorkType;

public class ExcuteDelete extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		
		if( id == null || id.isEmpty() ){
			Exception exception = new WorkTypeConfigIdEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}else{
			try{
				okrConfigWorkTypeService.delete( id );
				result.setData( new WrapOutId( id ));
				ApplicationCache.notify( OkrConfigWorkType.class );
			}catch(Exception e){
				Exception exception = new WorkTypeConfigDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}