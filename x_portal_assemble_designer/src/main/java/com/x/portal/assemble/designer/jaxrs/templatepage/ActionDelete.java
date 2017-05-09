package com.x.portal.assemble.designer.jaxrs.templatepage;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.TemplatePage;

class ActionDelete extends ActionBase {

	ActionResult<WrapOutBoolean> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			TemplatePage o = emc.find(id, TemplatePage.class);
			if (null == o) {
				throw new TemplatePageNotExistedException(id);
			}
			if (!business.templatePage().checkPermission(effectivePerson, o)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			emc.beginTransaction(TemplatePage.class);
			emc.remove(o, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(TemplatePage.class);
			result.setData(WrapOutBoolean.trueInstance());
			return result;
		}
	}
}