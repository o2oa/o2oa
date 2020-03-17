package com.x.cms.assemble.control.jaxrs.view;

import java.util.List;

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
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ActionDelete extends BaseAction {
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			View view = business.getViewFactory().get(id);
			//查询视图关联的所有列配置
			List<String> fieldConfigIds = business.getViewFieldConfigFactory().listByViewId(id);
			List<ViewFieldConfig> fieldConfigs = emc.list( ViewFieldConfig.class, fieldConfigIds);
			//查询视图关联的所有分类关联配置
			List<String> viewCategoryIds = business.getViewCategoryFactory().listByViewId(id);
			List<ViewCategory> viewCategorys = emc.list( ViewCategory.class, viewCategoryIds );
			
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( effectivePerson )) {
				throw new Exception("view{name:" + effectivePerson.getDistinguishedName() + "} 用户没有内容管理应用信息操作的权限！");
			}
			//进行数据库持久化操作
			emc.beginTransaction( View.class );
			emc.beginTransaction( ViewFieldConfig.class );
			emc.beginTransaction( ViewCategory.class );
			
			//删除所有的viewFieldConfig
			if( fieldConfigs != null && fieldConfigs.size() > 0 ){
				for( ViewFieldConfig viewFieldConfig : fieldConfigs ){
					emc.remove( viewFieldConfig, CheckRemoveType.all );
				}
			}
			if( viewCategorys != null && viewCategorys.size() > 0){
				for( ViewCategory viewCategory : viewCategorys ){
					emc.remove( viewCategory, CheckRemoveType.all );
				}
			}
			if( view != null ){
				emc.remove( view, CheckRemoveType.all );
			}
			emc.commit();
			
			if( view != null ){
				logService.log( emc,  effectivePerson.getDistinguishedName(), view.getName(), view.getAppId(), "", "", view.getId(), "VIEW", "删除" );
			}
			
			Wo wo = new Wo();
			wo.setId( view.getId() );
			result.setData( wo );
			
			ApplicationCache.notify( View.class );
			ApplicationCache.notify( ViewFieldConfig.class );
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