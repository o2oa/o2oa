package com.x.cms.assemble.control.jaxrs.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.Form;

public class ActionGetWithAppInfo extends BaseAction {

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String appFlag, String formFlag ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), formFlag, appFlag );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wo = (Wo) optional.get();
			result.setData( wo );
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				AppInfo appInfo = business.getAppInfoFactory().flag( appFlag );
				if ( null == appInfo ) {
					throw new Exception("appInfo{flag: '"+appFlag+"' not exists!");
				}
				Form form = business.getFormFactory().getWithAppInfo( appInfo.getId(), formFlag );
				if ( null == form ) {
					throw new Exception("form{app: '"+appFlag+"',form:'" + formFlag + "'} not exists!");
				}
				wo = new Wo();
				Wo.copier.copy( form, wo );
				CacheManager.put(cacheCategory, cacheKey, wo );
				result.setData( wo );
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
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
