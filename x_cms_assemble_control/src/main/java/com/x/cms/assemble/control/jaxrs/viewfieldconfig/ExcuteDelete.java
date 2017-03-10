package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ExcuteDelete extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			ViewFieldConfig viewFieldConfig = business.getViewFieldConfigFactory().get(id);
			
			if ( null == viewFieldConfig ) {
				logger.warn("viewFieldConfig{id:" + id + "} 应用信息不存在.");
			}
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, effectivePerson )) {
				throw new Exception("viewFieldConfig{name:" + effectivePerson.getName() + "} 用户没有内容管理应用信息操作的权限！");
			}
			//查询视图信息
			View view = business.getViewFactory().get( viewFieldConfig.getViewId() );
			if( view == null ){
				throw new Exception("view{'id':'"+viewFieldConfig.getViewId()+"'}不存在, 无法继续进行查询操作！");
			}
			//进行数据库持久化操作
			emc.beginTransaction( ViewFieldConfig.class );
			emc.beginTransaction( View.class);
			if ( null != viewFieldConfig ) {
				emc.remove( viewFieldConfig, CheckRemoveType.all );
			}
			deleteFieldConfigIdFromFieldConfigList( view, id );
			emc.commit();
			
			//成功删除一个展示列配置信息信息
			logService.log( emc, effectivePerson.getName(), viewFieldConfig.getFieldName(), "", "", "", viewFieldConfig.getId(), "VIEWFIELDCONFIG", "删除" );
			
			wrap = new WrapOutId( viewFieldConfig.getId() );
			
			ApplicationCache.notify( ViewFieldConfig.class );
			result.setData( wrap );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}
	
	private void deleteFieldConfigIdFromFieldConfigList( View view, String viewFieldConfigId){
		if( view != null ){
			if( view.getFieldConfigList() == null ){
				view.setFieldConfigList(new ArrayList<String>());
			}
			//看看是否已经包含分类ID
			if( !view.getFieldConfigList().contains( viewFieldConfigId )){
				view.getFieldConfigList().remove( viewFieldConfigId );
			}else{
			}
		}else{
		}
	}
	
}