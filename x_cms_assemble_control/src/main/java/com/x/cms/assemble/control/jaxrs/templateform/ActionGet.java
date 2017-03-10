package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.core.entity.element.TemplateForm;

import net.sf.ehcache.Element;

class ActionGet extends ActionBase {
	ActionResult<WrapOutTemplateForm> execute( EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutTemplateForm> result = new ActionResult<>();
		WrapOutTemplateForm wrap = null;
		
		String cacheKey = ApplicationCache.concreteCacheKey( id );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wrap = ( WrapOutTemplateForm ) element.getObjectValue();
			result.setData(wrap);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				
				TemplateForm o = emc.find(id, TemplateForm.class, ExceptionWhen.not_found);
				wrap = outCopier.copy(o);
				result.setData(wrap);
			}
		}
		return result;
	}
}