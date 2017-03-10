package com.x.cms.assemble.control.jaxrs.view;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.factory.ViewFactory;
import com.x.cms.core.entity.element.View;

import net.sf.ehcache.Element;

public class ExcuteListByApp extends ExcuteBase {
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutView>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String appId ) throws Exception {
		ActionResult<List<WrapOutView>> result = new ActionResult<>();
		List<WrapOutView> wraps = null;
		String cacheKey = ApplicationCache.concreteCacheKey( "app", appId );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = (List<WrapOutView>) element.getObjectValue();
			result.setData(wraps);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
				Business business = new Business(emc);			
				//如判断用户是否有查看所有视图的权限，如果没权限不允许继续操作
				if (!business.viewEditAvailable( request, effectivePerson )) {
					throw new Exception("person{name:" + effectivePerson.getName() + "} 用户没有查询全部视图的权限！");
				}
				//如果有权限，继续操作
				ViewFactory viewFactory  = business.getViewFactory();
				List<String> ids = viewFactory.listByAppId( appId );//获取指定应用的所有视图列表
				List<View> viewList = viewFactory.list( ids );//查询ID IN ids 的所有视图信息列表
				wraps = WrapTools.view_wrapout_copier.copy( viewList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				Collections.sort( wraps );
				cache.put(new Element( cacheKey, wraps ));
				result.setData(wraps);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		
		return result;
	}
}