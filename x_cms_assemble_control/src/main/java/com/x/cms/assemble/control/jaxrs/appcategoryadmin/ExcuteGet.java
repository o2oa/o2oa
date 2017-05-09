package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception.AppCategoryAdminIdEmptyException;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception.AppCategoryAdminNotExistsException;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception.AppCategoryAdminProcessException;
import com.x.cms.core.entity.AppCategoryAdmin;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutAppCategoryAdmin> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutAppCategoryAdmin> result = new ActionResult<>();
		AppCategoryAdmin appCategoryAdmin = null;
		WrapOutAppCategoryAdmin wrap = null;
		Boolean check = true;
		
		String cacheKey = ApplicationCache.concreteCacheKey( id );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wrap = ( WrapOutAppCategoryAdmin ) element.getObjectValue();
			result.setData(wrap);
		} else {
			if( check ){
				if( id == null || id.isEmpty() ){
					check = false;
					Exception exception = new AppCategoryAdminIdEmptyException();
					result.error( exception );
				}
			}
			
			if( check ){
				try {
					appCategoryAdmin = appCategoryAdminServiceAdv.get( id );
				} catch (Exception e) {
					check = false;
					Exception exception = new AppCategoryAdminProcessException( e, "根据ID查询应用栏目分类管理员配置信息时发生异常。ID:" + id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			if( check ){
				if( appCategoryAdmin != null ){
					try {
						wrap = WrapTools.appCategoryAdmin_wrapout_copier.copy( appCategoryAdmin );
						cache.put(new Element( cacheKey, wrap ));
						result.setData( wrap );
					} catch (Exception e) {
						Exception exception = new AppCategoryAdminProcessException( e, "系统将查询出来的应用栏目分类管理员信息转换为输出格式时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}else{
					Exception exception = new AppCategoryAdminNotExistsException( id );
					result.error( exception );
				}
			}
		}		
		return result;
	}

}