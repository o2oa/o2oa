package com.x.cms.assemble.control.jaxrs.viewcategory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.ViewCategory;

public class ExcuteDelete extends ExcuteBase {
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			ViewCategory viewCategory = business.getViewCategoryFactory().get(id);
			if (null == viewCategory) {
				throw new Exception("view{id:" + id + "} 应用信息不存在.");
			}
			
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, effectivePerson )) {
				throw new Exception("view{name:" + effectivePerson.getName() + "} 用户没有内容管理应用信息操作的权限！");
			}
			
			//进行数据库持久化操作
			emc.beginTransaction( ViewCategory.class );
			emc.remove( viewCategory, CheckRemoveType.all );
			emc.commit();
			
			wrap = new WrapOutId( viewCategory.getId() );
			result.setData( wrap );
			ApplicationCache.notify( ViewCategory.class );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}
	
}