package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.okr.entity.OkrConfigSecretary;

import net.sf.ehcache.Element;

public class ExcuteListMySercretary extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListMySercretary.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutOkrConfigSecretary>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutOkrConfigSecretary>> result = new ActionResult<List<WrapOutOkrConfigSecretary>>();
		List<WrapOutOkrConfigSecretary> wraps = null;
		List<String> ids = null;
		List<OkrConfigSecretary> okrConfigSecretaryList = null;
		String cacheKey = catchNamePrefix + "." + effectivePerson.getName();
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			wraps = (List<WrapOutOkrConfigSecretary>) element.getObjectValue();
			result.setData( wraps );
		}else{
			try {
				ids = okrConfigSecretaryService.listIdsByPerson( effectivePerson.getName() );
				if( ids != null && ids.size() > 0 ){
					okrConfigSecretaryList = okrConfigSecretaryService.listByIds( ids );
				}
				if( okrConfigSecretaryList != null ){
					wraps = wrapout_copier.copy( okrConfigSecretaryList );
					cache.put( new Element( cacheKey, wraps ) );
					result.setData( wraps );
				}else{
					logger.warn( "system can not get any object by {'user':'"+ effectivePerson.getName() +"'}. " );
				}
			} catch (Throwable th) {
				Exception exception = new SercretaryConfigListByIdsException( th );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}