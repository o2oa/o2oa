package com.x.cms.assemble.control.jaxrs.viewcategory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.ViewCategory;

public class ExcuteSave extends ExcuteBase {
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInViewCategory wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		ViewCategory viewCategory = null;
		Boolean check = true;
		BeanCopyTools<WrapInViewCategory, ViewCategory> copier = BeanCopyToolsBuilder.create( WrapInViewCategory.class, ViewCategory.class, null, WrapInViewCategory.Excludes );
		if(check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
				Business business = new Business(emc);
				//看看用户是否有权限进行应用信息新增操作
				if (!business.viewEditAvailable( request, effectivePerson )) {
					throw new Exception("person{name:" + effectivePerson.getName() + "} 用户没有内容管理视图分类关联信息信息操作的权限！");
				}
				viewCategory = business.getViewCategoryFactory().getByViewAndCategory( wrapIn.getViewId(), wrapIn.getCategoryId() );
				if( viewCategory == null ){
					viewCategory = new ViewCategory();
					copier.copy( wrapIn, viewCategory );
					emc.beginTransaction( ViewCategory.class );
					emc.persist( viewCategory, CheckPersistType.all );
					emc.commit();
				}else{
					copier.copy( wrapIn, viewCategory );
					emc.beginTransaction( ViewCategory.class );
					emc.check( viewCategory, CheckPersistType.all );
					emc.commit();
				}
				wrap = new WrapOutId( viewCategory.getId() );
				result.setData(wrap);
				ApplicationCache.notify( ViewCategory.class );
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}
	
}