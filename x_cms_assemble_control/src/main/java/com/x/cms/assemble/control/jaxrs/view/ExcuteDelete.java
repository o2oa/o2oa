package com.x.cms.assemble.control.jaxrs.view;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ExcuteDelete extends ExcuteBase {
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			View view = business.getViewFactory().get(id);
			//查询视图关联的所有列配置
			List<String> fieldConfigIds = business.getViewFieldConfigFactory().listByViewId(id);
			List<ViewFieldConfig> fieldConfigs = business.getViewFieldConfigFactory().list(fieldConfigIds);
			//查询视图关联的所有分类关联配置
			List<String> viewCategoryIds = business.getViewCategoryFactory().listByViewId(id);
			List<ViewCategory> viewCategorys = business.getViewCategoryFactory().list( viewCategoryIds );
			
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, effectivePerson )) {
				throw new Exception("view{name:" + effectivePerson.getName() + "} 用户没有内容管理应用信息操作的权限！");
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
				logService.log( emc,  effectivePerson.getName(), view.getName(), view.getAppId(), "", "", view.getId(), "VIEW", "删除" );
			}
			
			wrap = new WrapOutId( view.getId() );
			result.setData( wrap );
			
			ApplicationCache.notify( View.class );
			ApplicationCache.notify( ViewFieldConfig.class );
			ApplicationCache.notify( ViewCategory.class );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}
	
}