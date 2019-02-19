package com.x.organization.assemble.personal.jaxrs.definition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.Definition;

import net.sf.ehcache.Element;

class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<String> execute(EffectivePerson effectivePerson, String name) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<String> result = new ActionResult<>();
			String cacheKey = name;
			Element element = cache.get(cacheKey);
			String wo = "";
			if (null != element) {
				wo = (String) element.getObjectValue();
			} else {
				Definition o = emc.flag(name, Definition.class);
				if (null != o) {
					wo = o.getData();
					cache.put(new Element(cacheKey, wo));
				}
			}
			result.setData(wo);
			return result;
		}
	}
}
