package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.core.entity.AppCategoryPermission;

import net.sf.ehcache.Element;

public class ExcuteListByAppInfo extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListByAppInfo.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutAppCategoryPermission>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String appId, String permission ) throws Exception {
		ActionResult<List<WrapOutAppCategoryPermission>> result = new ActionResult<>();
		List<WrapOutAppCategoryPermission> wraps = null;
		List<String> ids = null;
		List<AppCategoryPermission> appCategoryPermissionList = null;
		Boolean check = true;
		
		String cacheKey = ApplicationCache.concreteCacheKey( "app", appId, permission );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = ( List<WrapOutAppCategoryPermission> ) element.getObjectValue();
			result.setData(wraps);
		} else {
			if( check ){
				if( appId == null || appId.isEmpty() ){
					check = false;
					Exception exception = new AppCategoryPermissionAppIdEmptyException();
					result.error( exception );
				}
			}
			if( check ){
				try {
					ids = appCategoryPermissionServiceAdv.listPermissionByAppInfo( appId, permission );
				} catch (Exception e) {
					check = false;
					Exception exception = new AppCategoryPermissionListByAppIdException( e, appId );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
			if( check ){
				if( ids != null && !ids.isEmpty() ){
					try {
						appCategoryPermissionList = appCategoryPermissionServiceAdv.list( ids );
					} catch (Exception e) {
						check = false;
						Exception exception = new AppCategoryPermissionListByIdsException( e );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}
			}
			if( check ){
				if( appCategoryPermissionList != null && !appCategoryPermissionList.isEmpty() ){
					try {
						wraps = WrapTools.appCategoryPermission_wrapout_copier.copy( appCategoryPermissionList );
						Collections.sort(wraps);
						cache.put(new Element( cacheKey, wraps ));
						result.setData(wraps);
					} catch (Exception e) {
						Exception exception = new AppCategoryPermissionWrapOutException( e );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}
			}
		}
		
		return result;
	}
	
}