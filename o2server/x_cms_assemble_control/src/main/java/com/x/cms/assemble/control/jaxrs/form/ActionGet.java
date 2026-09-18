package com.x.cms.assemble.control.jaxrs.form;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.Form;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.BooleanUtils;

public class ActionGet extends BaseAction {

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wo = (Wo)optional.get();
			result.setData( wo );
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Form form = business.getFormFactory().get(id);
				if ( null == form ) {
					throw new ExceptionFormNotExist(id);
				}
				AppInfo appInfo = emc.find(form.getAppId(), AppInfo.class);
				if(appInfo == null){
					throw new ExceptionAppInfoNotExist(form.getAppId());
				}
				if( effectivePerson.isAnonymous() && BooleanUtils.isNotTrue(appInfo.getAllowAnonymousAccessDoc())) {
					throw new ExceptionAccessDenied(effectivePerson);
				}
				wo = new Wo();
				Wo.copier.copy( form, wo );
				CacheManager.put(cacheCategory, cacheKey, wo );
				result.setData( wo );
			}
		}
		return result;
	}

	public static class Wo extends Form {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<String>();

		public static final WrapCopier<Form, Wo> copier = WrapCopierFactory.wo(Form.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}
