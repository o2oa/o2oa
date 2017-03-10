package com.x.cms.assemble.control.jaxrs.viewcategory;

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
import com.x.cms.assemble.control.factory.ViewCategoryFactory;
import com.x.cms.core.entity.element.ViewCategory;

import net.sf.ehcache.Element;

public class ExcuteListByCategory extends ExcuteBase {
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutViewCategory>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String categoryId ) throws Exception {
		ActionResult<List<WrapOutViewCategory>> result = new ActionResult<>();
		List<WrapOutViewCategory> wraps = null;
		String cacheKey = ApplicationCache.concreteCacheKey( "category", categoryId );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = (List<WrapOutViewCategory>) element.getObjectValue();
			result.setData(wraps);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
				Business business = new Business(emc);			
				//如判断用户是否有查看所有视图分类关联信息的权限，如果没权限不允许继续操作
				if (!business.viewEditAvailable( request, effectivePerson )) {
					throw new Exception("person{name:" + effectivePerson.getName() + "} 用户没有查询全部视图分类关联信息的权限！");
				}			
				//如果有权限，继续操作
				ViewCategoryFactory viewCategoryFactory  = business.getViewCategoryFactory();
				List<String> ids = viewCategoryFactory.listByCategoryId( categoryId );//获取指定应用的所有视图分类关联信息列表
				List<ViewCategory> viewList = viewCategoryFactory.list( ids );//查询ID IN ids 的所有视图分类关联信息信息列表
				wraps = WrapTools.viewCategory_wrapout_copier.copy( viewList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
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