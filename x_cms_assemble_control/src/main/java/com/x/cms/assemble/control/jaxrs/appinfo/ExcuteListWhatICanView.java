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

public class ExcuteListWhatICanView extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListWhatICanView.class );
	
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
		
		String cacheKey = ApplicationCache.concreteCacheKey( effectivePerson.getName(), "view", isXAdmin );
		Element element = cache.get(cacheKey);
		
		if (( null != element ) && ( null != element.getObjectValue() ) ) {
			wraps = ( List<WrapOutAppInfo> ) element.getObjectValue();
			result.setData(wraps);
		} else {
			if( check ){
				if ( isXAdmin ) {
					try{
						wraps = getAllAppInfoWithCategory();
					}catch( Exception e ){
						check = false;
						Exception exception = new AppInfoProcessException( e, "系统查询所有可见的分类信息时发生异常[管理员]。Name:" + effectivePerson.getName() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}else{
					try{
						wraps = getViewAbleAppInfoByPermission( effectivePerson.getName() );
					}catch( Exception e ){
						check = false;
						Exception exception = new AppInfoProcessException( e, "系统在根据用户权限查询所有可见的分类信息时发生异常。Name:" + effectivePerson.getName() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
		}
		result.setData(wraps);
		return result;
	}
	
}