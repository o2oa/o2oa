package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.WorkTypeConfigIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.WorkTypeConfigNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.WorkTypeConfigQueryByIdException;
import com.x.okr.entity.OkrConfigWorkType;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutOkrConfigWorkType> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrConfigWorkType> result = new ActionResult<>();
		WrapOutOkrConfigWorkType wrap = null;
		OkrConfigWorkType okrConfigWorkType = null;

		if( id == null || id.isEmpty() ){
			Exception exception = new WorkTypeConfigIdEmptyException();
			result.error( exception );
		//	logger.error( e, effectivePerson, request, null);
		}else{
			String cacheKey = catchNamePrefix + "." + id;
			Element element = null;
			
			element = cache.get( cacheKey );
			if( element != null ){
				wrap = (WrapOutOkrConfigWorkType) element.getObjectValue();
				result.setData( wrap );
			}else{
				try {
					okrConfigWorkType = okrConfigWorkTypeService.get( id );
					if( okrConfigWorkType != null ){
						wrap = wrapout_copier.copy( okrConfigWorkType );
						cache.put( new Element( cacheKey, wrap ) );						
						result.setData(wrap);
					}else{
						Exception exception = new WorkTypeConfigNotExistsException( id );
						result.error( exception );
						//logger.error( e, effectivePerson, request, null);
					}
				} catch (Exception e) {
					Exception exception = new WorkTypeConfigQueryByIdException( e, id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}		
		return result;
	}
	
}