package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.entity.OkrConfigSecretary;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutOkrConfigSecretary> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrConfigSecretary> result = new ActionResult<>();
		WrapOutOkrConfigSecretary wrap = null;
		OkrConfigSecretary okrConfigSecretary = null;
		
		if( id == null || id.isEmpty() ){
			Exception exception = new SercretaryConfigIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}else{
			String cacheKey = catchNamePrefix + "." + id;
			Element element = null;
			element = cache.get( cacheKey );
			if( element != null ){
				wrap = ( WrapOutOkrConfigSecretary ) element.getObjectValue();
				result.setData( wrap );
			}else{
				try {
					okrConfigSecretary = okrConfigSecretaryService.get( id );
					if( okrConfigSecretary != null ){
						wrap = wrapout_copier.copy( okrConfigSecretary );
						cache.put( new Element( cacheKey, wrap ) );
						result.setData(wrap);
					}else{
						Exception exception = new SercretaryConfigNotExistsException( id );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				} catch (Throwable th) {
					Exception exception = new SercretaryConfigQueryByIdException( th, id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
}