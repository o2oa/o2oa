package com.x.cms.assemble.control.jaxrs.form;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.Form;

import net.sf.ehcache.Element;

public class ActionGetWithAppInfo extends BaseAction {
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String appFlag, String formFlag ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		
		String cacheKey = ApplicationCache.concreteCacheKey( "getWithAppInfo", formFlag, appFlag );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wo = (Wo) element.getObjectValue();
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
				cache.put(new Element( cacheKey, wo ));
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
		
		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<Form, Wo> copier = WrapCopierFactory.wo(Form.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}