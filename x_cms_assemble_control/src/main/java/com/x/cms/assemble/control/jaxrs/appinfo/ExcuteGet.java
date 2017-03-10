package com.x.cms.assemble.control.jaxrs.appinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.core.entity.AppInfo;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutAppInfo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutAppInfo> result = new ActionResult<>();
		WrapOutAppInfo wrap = null;
		AppInfo appInfo = null;
		Boolean check = true;
		
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new AppInfoIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey( id );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wrap = ( WrapOutAppInfo ) element.getObjectValue();
			result.setData(wrap);
		} else {
			if( check ){
				try {
					appInfo = appInfoServiceAdv.get( id );
					if( appInfo == null ){
						check = false;
						Exception exception = new AppInfoNotExistsException( id );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new AppInfoQueryByIdException( e, id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
			if( check ){
				try {
					wrap = WrapTools.appInfo_wrapout_copier.copy( appInfo );
					cache.put(new Element( cacheKey, wrap ));
					result.setData( wrap );
				} catch (Exception e) {
					Exception exception = new AppInfoWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		
		return result;
	}

}