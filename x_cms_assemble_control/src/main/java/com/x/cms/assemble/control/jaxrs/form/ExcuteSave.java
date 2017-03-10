package com.x.cms.assemble.control.jaxrs.form;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ExcuteSave extends ExcuteBase {
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInForm wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		Boolean check = true;
		
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				
				// 看看用户是否有权限进行应用信息新增操作
				if (!business.formEditAvailable(request, effectivePerson)) {
					throw new Exception("person{name:" + effectivePerson.getName() + "} 用户没有内容管理表单模板信息操作的权限！");
				}
				
				Form form = business.getFormFactory().get( wrapIn.getId() );
				if ( null == form ){
					form = new Form();
					WrapTools.form_wrapin_copier_in.copy(wrapIn, form);
					if ( wrapIn.getId() != null && wrapIn.getId().length() > 10 ) {
						form.setId(wrapIn.getId());
					}
					emc.beginTransaction( Form.class );
					emc.persist( form, CheckPersistType.all );
					emc.commit();
					
					logService.log( emc, effectivePerson.getName(), form.getName(), form.getAppId(), "", "", form.getId(), "FORM", "新增");
					
					wrap = new WrapOutId(form.getId());
					result.setData(wrap);
				}else{
					WrapTools.form_wrapin_copier_in.copy( wrapIn, form );
					emc.beginTransaction( Form.class );
					emc.check( form, CheckPersistType.all );
					emc.commit();
					
					logService.log( emc, effectivePerson.getName(), form.getName(), form.getAppId(), "", "", form.getId(), "FORM", "更新");
					
					wrap = new WrapOutId(form.getId());
					result.setData(wrap);
				}
				
				ApplicationCache.notify( Form.class );
				ApplicationCache.notify( View.class );
				ApplicationCache.notify( ViewFieldConfig.class );
				ApplicationCache.notify( ViewCategory.class );
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}
	
}