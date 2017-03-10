package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.core.entity.AppCategoryPermission;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {
	
	protected ActionResult<WrapOutAppCategoryPermission> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutAppCategoryPermission> result = new ActionResult<>();
		WrapOutAppCategoryPermission wrap = null;
		
		String cacheKey = ApplicationCache.concreteCacheKey( id );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wrap = ( WrapOutAppCategoryPermission ) element.getObjectValue();
			result.setData(wrap);
		} else {
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				Business business = new Business(emc);
				AppCategoryPermission AppCategoryPermission = business.getAppCategoryPermissionFactory().get(id);
				if ( null == AppCategoryPermission ) {
					throw new Exception("AppCategoryPermission{id:" + id + "} 信息不存在.");
				}
				//如果信息存在，则需要向客户端返回信息，先将查询出来的JPA对象COPY到一个普通JAVA对象里，再进行返回
				wrap = new WrapOutAppCategoryPermission();
				WrapTools.appCategoryPermission_wrapout_copier.copy(AppCategoryPermission, wrap);
				cache.put(new Element( cacheKey, wrap ));
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return result;
	}

}