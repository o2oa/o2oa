package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.jaxrs.appinfo.exception.AppInfoProcessException;

import net.sf.ehcache.Element;

public class ExcuteListWhatICanPublish extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListWhatICanPublish.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutAppInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		List<WrapOutAppInfo> wraps = null;
		Boolean isXAdmin = false;
		Boolean check = true;
		
		try {
			isXAdmin = effectivePerson.isManager();
		} catch (Exception e) {
			check = false;
			Exception exception = new UserManagerCheckException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey( effectivePerson.getName(), "publish", isXAdmin );
		Element element = cache.get( cacheKey );
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = ( List<WrapOutAppInfo> ) element.getObjectValue();
			result.setData(wraps);
		} else {
			if( check ){
				if ( isXAdmin ) { //如果用户管理系统管理，则获取所有的栏目和分类信息
					try{
						wraps = getAllAppInfoWithCategory();
					}catch( Exception e ){
						check = false;
						Exception exception = new AppInfoProcessException( e, "系统在根据用户权限查询所有可见的分类信息时发生异常。Name:" + effectivePerson.getName() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}else{
					try{
						wraps = getPublishAbleAppInfoByPermission( effectivePerson.getName() );
					}catch( Exception e ){
						check = false;
						Exception exception = new AppInfoProcessException( e, "系统在根据用户权限查询所有可见的分类信息时发生异常。Name:" + effectivePerson.getName() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			if( check ){
				cache.put(new Element( cacheKey, wraps ));
				result.setData(wraps);
			}
		}
		return result;
	}
}