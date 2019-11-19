package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ExceptionWrapInConvert;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ActionSave extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		ViewFieldConfig viewFieldConfig = null;
		View view = null;
		Wi wi = null;
		Wo wrap = null;
		Boolean check = true;
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
			if( id != null && !id.isEmpty() ){
				wi.setId( id );
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			if( StringUtils.isEmpty(wi.getViewId()) ){
				check = false;
				Exception exception = new ExceptionWrapInViewIdEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			//先看看视图信息是否存在，如果不存在
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);				
				//看看用户是否有权限进行应用信息新增操作
				if (!business.viewEditAvailable( effectivePerson )) {
					check = false;
					Exception exception = new ExceptionNoPermission( effectivePerson.getDistinguishedName() );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionViewQueryByIdEmpty( e, wi.getViewId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			//先看看视图信息是否存在，如果不存在
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				Business business = new Business(emc);				
				view = business.getViewFactory().get( wi.getViewId() );
				if( view == null ){
					check = false;
					Exception exception = new ExceptionViewNotExists( wi.getViewId() );
					result.error( exception );
				}
				
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionViewQueryByIdEmpty( e, wi.getViewId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				//获取到当前用户信息		
				Business business = new Business(emc);				
				view = business.getViewFactory().get( wi.getId() );
				viewFieldConfig = business.getViewFieldConfigFactory().get( wi.getId() );
				
				if( viewFieldConfig == null ){//新增
					viewFieldConfig = Wi.copier.copy( wi );
					//如果JSON给过来的ID不为空，那么使用用户传入的ID
					if( wi.getId() != null && !wi.getId().isEmpty() ){
						viewFieldConfig.setId( wi.getId() );
					}
					emc.beginTransaction( ViewFieldConfig.class );
					emc.beginTransaction( View.class);
					emc.persist( viewFieldConfig, CheckPersistType.all );
					
					addFieldConfigIdToFieldConfigList( view, viewFieldConfig.getId() );
					
					emc.commit();
					logService.log( emc,  effectivePerson.getDistinguishedName(), viewFieldConfig.getFieldName(), "", "", "", viewFieldConfig.getId(), "VIEWFIELDCONFIG", "新增" );
				}else{
					//更新
					wi.copyTo( viewFieldConfig, JpaObject.FieldsUnmodify  );
					
					emc.beginTransaction( ViewFieldConfig.class );
					emc.beginTransaction( View.class);
					emc.check( viewFieldConfig, CheckPersistType.all );
					addFieldConfigIdToFieldConfigList( view, viewFieldConfig.getId() );
					emc.commit();
					
					logService.log( emc,  effectivePerson.getDistinguishedName(), viewFieldConfig.getFieldName(), "", "", "", viewFieldConfig.getId(), "VIEWFIELDCONFIG", "更新" );
				}
				wrap = new Wo();
				wrap.setId( viewFieldConfig.getId() );
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
		}
	}
	
	public static class Wi extends ViewFieldConfig{
		
	  private static final long serialVersionUID = -5076990764713538973L;
	  
	  public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
	  
	  public static WrapCopier<Wi, ViewFieldConfig> copier = WrapCopierFactory.wi( Wi.class, ViewFieldConfig.class, null, JpaObject.FieldsUnmodify );
	  
	}
	
	public static class Wo extends WoId {

	}
}