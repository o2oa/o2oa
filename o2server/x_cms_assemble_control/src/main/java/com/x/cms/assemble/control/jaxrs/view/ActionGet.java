package com.x.cms.assemble.control.jaxrs.view;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.View;

import net.sf.ehcache.Element;

public class ActionGet extends BaseAction {
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String flag ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		String cacheKey = ApplicationCache.concreteCacheKey( flag );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wrap = (Wo) element.getObjectValue();
			result.setData(wrap);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				View view = business.getViewFactory().flag( flag );
				if ( null == view ) {
					throw new Exception("view{flag:" + flag + "} 信息不存在.");
				}
				wrap = Wo.copier.copy( view );
				//根据FormId补充FormName
				if(StringUtils.isNotEmpty( wrap.getFormId() )) {
					wrap.setFormName( formServiceAdv.getNameWithId( wrap.getFormId() ) );
				}
				cache.put(new Element( cacheKey, wrap ));
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}
	
	public static class Wo extends View {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		@FieldDescribe("绑定的表单名称.")
		private String formName = null;
		
		public static WrapCopier<View, Wo> copier = WrapCopierFactory.wo( View.class, Wo.class, null,JpaObject.FieldsInvisible);

		public String getFormName() {
			return formName;
		}

		public void setFormName(String formName) {
			this.formName = formName;
		}
	}
}