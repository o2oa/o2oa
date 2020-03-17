package com.x.cms.assemble.control.jaxrs.viewcategory;

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
import com.x.cms.core.entity.element.ViewCategory;

public class ActionSave extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		ViewCategory viewCategory = null;
		Wi wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if(check) {
			if( StringUtils.isNotEmpty( effectivePerson.getDistinguishedName() )) {
				wrapIn.setEditor( effectivePerson.getDistinguishedName() );
			}else {
				wrapIn.setEditor( effectivePerson.getName() );
			}			
		}
		
		if(check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
				Business business = new Business(emc);
				//看看用户是否有权限进行应用信息新增操作
				if (!business.viewEditAvailable( effectivePerson )) {
					throw new Exception("person{name:" + effectivePerson.getDistinguishedName() + "} 用户没有内容管理视图分类关联信息信息操作的权限！");
				}
				viewCategory = business.getViewCategoryFactory().getByViewAndCategory( wrapIn.getViewId(), wrapIn.getCategoryId() );
				if( viewCategory == null ){
					viewCategory = Wi.copier.copy( wrapIn );
					emc.beginTransaction( ViewCategory.class );
					emc.persist( viewCategory, CheckPersistType.all );
					emc.commit();
				}else{
					wrapIn.copyTo(viewCategory, JpaObject.FieldsUnmodify );
					emc.beginTransaction( ViewCategory.class );
					emc.check( viewCategory, CheckPersistType.all );
					emc.commit();
				}
				Wo wo = new Wo();
				wo.setId( viewCategory.getId() );
				result.setData(wo);
				ApplicationCache.notify( View.class );
				ApplicationCache.notify( ViewCategory.class );
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}
	
	public static class Wi extends ViewCategory{
		
	  private static final long serialVersionUID = -5076990764713538973L;
	  
	  public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
	  
	  public static WrapCopier<Wi, ViewCategory> copier = WrapCopierFactory.wi( Wi.class, ViewCategory.class, null, JpaObject.FieldsUnmodify );
	  
	}
	
	public static class Wo extends WoId {

	}
}