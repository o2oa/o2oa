package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
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
			logger.error( exception, effectivePerson, request, null);
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
						logger.error( exception, effectivePerson, request, null);
					}
				} catch (Throwable th) {
					Exception exception = new SystemConfigQueryByIdException( th, id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
}