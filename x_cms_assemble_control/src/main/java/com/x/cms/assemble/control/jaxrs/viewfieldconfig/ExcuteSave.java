package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ExcuteSave extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInViewFieldConfig wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		ViewFieldConfig viewFieldConfig = null;
		View view = null;
		WrapOutId wrap = null;
		Boolean check = true;
		
		if( wrapIn.getViewId() == null || wrapIn.getViewId().isEmpty() ){
			check = false;
			Exception exception = new WrapInViewIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		if( check ){
			//先看看视图信息是否存在，如果不存在
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);				
				//看看用户是否有权限进行应用信息新增操作
				if (!business.viewEditAvailable( request, effectivePerson )) {
					check = false;
					Exception exception = new NoPermissionException( effectivePerson.getName() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Throwable th) {
				check = false;
				Exception exception = new ViewQueryByIdEmptyException( th, wrapIn.getViewId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			//先看看视图信息是否存在，如果不存在
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);				
				view = business.getViewFactory().get( wrapIn.getId() );
				if( view == null ){
					check = false;
					Exception exception = new ViewNotExistsException( wrapIn.getViewId() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
				
			} catch (Throwable th) {
				check = false;
				Exception exception = new ViewQueryByIdEmptyException( th, wrapIn.getViewId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				//获取到当前用户信息		
				Business business = new Business(emc);				
				view = business.getViewFactory().get( wrapIn.getId() );
				viewFieldConfig = business.getViewFieldConfigFactory().get( wrapIn.getId() );
				
				if( viewFieldConfig == null ){//新增
					viewFieldConfig = new ViewFieldConfig();					
					WrapTools.viewFieldConfig_wrapin_copier.copy( wrapIn, viewFieldConfig );
					//如果JSON给过来的ID不为空，那么使用用户传入的ID
					if( wrapIn.getId().length() == viewFieldConfig.getId().length() ){
						viewFieldConfig.setId( wrapIn.getId() );
					}
					emc.beginTransaction( ViewFieldConfig.class );
					emc.beginTransaction( View.class);
					emc.persist( viewFieldConfig, CheckPersistType.all );
					
					addFieldConfigIdToFieldConfigList( view, viewFieldConfig.getId() );
					
					emc.commit();
					logService.log( emc,  effectivePerson.getName(), viewFieldConfig.getFieldName(), "", "", "", viewFieldConfig.getId(), "VIEWFIELDCONFIG", "新增" );
				}else{
					//更新
					WrapTools.viewFieldConfig_wrapin_copier.copy( wrapIn, viewFieldConfig );
					emc.beginTransaction( ViewFieldConfig.class );
					emc.beginTransaction( View.class);
					emc.check( viewFieldConfig, CheckPersistType.all );
					addFieldConfigIdToFieldConfigList( view, viewFieldConfig.getId() );
					emc.commit();
					
					logService.log( emc,  effectivePerson.getName(), viewFieldConfig.getFieldName(), "", "", "", viewFieldConfig.getId(), "VIEWFIELDCONFIG", "更新" );
				}
				
				wrap = new WrapOutId( viewFieldConfig.getId() );
				result.setData(wrap);
				
				ApplicationCache.notify( ViewFieldConfig.class );
				ApplicationCache.notify( View.class );
				
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}
	
	private void addFieldConfigIdToFieldConfigList( View view, String viewFieldConfigId){
		if( view != null ){
			if( view.getFieldConfigList() == null ){
				view.setFieldConfigList( new ArrayList<String>() );
			}
			//看看是否已经包含配置ID
			if( !view.getFieldConfigList().contains( viewFieldConfigId )){
				view.getFieldConfigList().add( viewFieldConfigId );
			}else{
			}
		}else{
		}
	}
	
}