package com.x.cms.assemble.control.jaxrs.form;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.core.entity.element.Form;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {
	
	protected ActionResult<WrapOutForm> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutForm> result = new ActionResult<>();
		WrapOutForm wrap = null;
		
		String cacheKey = ApplicationCache.concreteCacheKey( id );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wrap = (WrapOutForm) element.getObjectValue();
			result.setData(wrap);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Form form = business.getFormFactory().get(id);
				if ( null == form ) {
					throw new Exception("form{id:" + id + "} 信息不存在.");
				}
				wrap = new WrapOutForm();
				WrapTools.form_wrapout_copier.copy(form, wrap);
				cache.put(new Element( cacheKey, wrap ));
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return result;
	}
	
}