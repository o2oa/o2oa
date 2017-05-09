package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.SystemConfigCodeEmptyException;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.SystemConfigNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.SystemConfigQueryByCodeException;
import com.x.okr.entity.OkrConfigSystem;

import net.sf.ehcache.Element;

public class ExcuteGetByCode extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGetByCode.class );
	
	protected ActionResult<WrapOutOkrConfigSystem> execute( HttpServletRequest request,EffectivePerson effectivePerson, WrapInOkrConfigSystem wrapIn ) throws Exception {
		ActionResult<WrapOutOkrConfigSystem> result = new ActionResult<WrapOutOkrConfigSystem>();
		WrapOutOkrConfigSystem wrap = null;
		OkrConfigSystem okrConfigSystem = null;
		
//		if( wrapIn == null ){
//			logger.error( "wrapIn is null, system can not get any object." );
//		}
		if( wrapIn.getConfigCode() == null || wrapIn.getConfigCode().isEmpty() ){
			Exception exception = new SystemConfigCodeEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}else{
			String cacheKey = catchNamePrefix + "." + wrapIn.getConfigCode();
			Element element = null;			
			element = cache.get( cacheKey );
			if( element != null ){
				wrap = ( WrapOutOkrConfigSystem ) element.getObjectValue();
				result.setData( wrap );
			}else{
				try {
					okrConfigSystem = okrConfigSystemService.getWithConfigCode( wrapIn.getConfigCode() );
					if( okrConfigSystem != null ){
						wrap = wrapout_copier.copy( okrConfigSystem );						
						cache.put( new Element( cacheKey, wrap ) );						
						result.setData(wrap);
					}else{
						Exception exception = new SystemConfigNotExistsException( wrapIn.getConfigCode() );
						result.error( exception );
						//logger.error( e, effectivePerson, request, null);
					}
				} catch (Exception e) {
					Exception exception = new SystemConfigQueryByCodeException( e, wrapIn.getConfigCode() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
}