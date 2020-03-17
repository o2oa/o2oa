package com.x.cms.assemble.control.jaxrs.appinfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfo;

import net.sf.ehcache.Element;

public class ActionGetAnonymous extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGetAnonymous.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String flag ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		AppInfo appInfo = null;
		Boolean check = true;
		
		if( StringUtils.isEmpty(flag) ){
			check = false;
			Exception exception = new ExceptionAppInfoIdEmpty();
			result.error( exception );
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey( flag );
		Element element = cache.get( cacheKey );
		
		if (( null != element ) && ( null != element.getObjectValue()) ) {
			wo = ( Wo ) element.getObjectValue();
			result.setData( wo );
		} else {
			if( check ){
				try {
					appInfo = appInfoServiceAdv.getWithFlag( flag );
					if( appInfo == null ){
						check = false;
						Exception exception = new ExceptionAppInfoNotExists( flag );
						result.error( exception );
					}else {
						if( !appInfo.getAnonymousAble() ){
							check = false;
							Exception exception = new ExceptionAppInfoAccessDenied("栏目信息不允许匿名访问！");
							result.error( exception );
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAppInfoProcess( e, "根据指定flag查询应用栏目信息对象时发生异常。flag:" + flag );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				try {
					wo = Wo.copier.copy( appInfo );
					cache.put(new Element( cacheKey, wo ));
					result.setData( wo );
				} catch (Exception e) {
					Exception exception = new ExceptionAppInfoProcess( e, "将查询出来的应用栏目信息对象转换为可输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		return result;
	}
}