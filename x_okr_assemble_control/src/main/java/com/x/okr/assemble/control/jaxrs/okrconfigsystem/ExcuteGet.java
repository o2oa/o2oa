package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.SystemConfigIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.SystemConfigNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.SystemConfigQueryByIdException;
import com.x.okr.entity.OkrConfigSystem;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutOkrConfigSystem> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrConfigSystem> result = new ActionResult<>();
		WrapOutOkrConfigSystem wrap = null;
		OkrConfigSystem okrConfigSystem = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new SystemConfigIdEmptyException();
			result.error( exception );
		//	logger.error( e, effectivePerson, request, null);
		}else{
			String cacheKey = catchNamePrefix + "." + id;
			Element element = null;
			element = cache.get( cacheKey );
			if( element != null ){
				wrap = (WrapOutOkrConfigSystem) element.getObjectValue();
				result.setData( wrap );
			}else{
				try {
					okrConfigSystem = okrConfigSystemService.get( id );
					if( okrConfigSystem != null ){
						wrap = wrapout_copier.copy( okrConfigSystem );						
						cache.put( new Element( cacheKey, wrap ) );						
						result.setData(wrap);
					}else{
						Exception exception = new SystemConfigNotExistsException( id );
						result.error( exception );
						//logger.error( e, effectivePerson, request, null);
					}
				} catch (Exception e) {
					Exception exception = new SystemConfigQueryByIdException( e, id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
}