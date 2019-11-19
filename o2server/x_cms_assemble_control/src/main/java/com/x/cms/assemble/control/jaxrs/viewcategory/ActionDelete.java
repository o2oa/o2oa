package com.x.cms.assemble.control.jaxrs.viewcategory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;

public class ActionDelete extends BaseAction {
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			ViewCategory viewCategory = business.getViewCategoryFactory().get(id);
			if (null == viewCategory) {
				throw new Exception("view{id:" + id + "} 应用信息不存在.");
			}
			
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( effectivePerson )) {
				throw new Exception("view{name:" + effectivePerson.getDistinguishedName() + "} 用户没有内容管理应用信息操作的权限！");
			}
			
			//进行数据库持久化操作
			emc.beginTransaction( ViewCategory.class );
			emc.remove( viewCategory, CheckRemoveType.all );
			emc.commit();
			
			Wo wo = new Wo();
			wo.setId( viewCategory.getId() );
			result.setData(wo);
			
			ApplicationCache.notify( View.class );
			ApplicationCache.notify( ViewCategory.class );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}
	
	public static class Wo extends WoId {

	}
}