package com.x.portal.assemble.designer.jaxrs.templatepage;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapout.WrapOutTemplatePage;
import com.x.portal.core.entity.TemplatePage;

class ActionGet extends ActionBase {
	ActionResult<WrapOutTemplatePage> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutTemplatePage> result = new ActionResult<>();
			WrapOutTemplatePage wrap = null;
			TemplatePage templatePage = emc.find(id, TemplatePage.class);
			if (null == templatePage) {
				throw new TemplatePageNotExistedException(id);
			}
			if (!business.templatePage().editable(effectivePerson, templatePage)) {
				throw new TemplatePageInvisibleException(effectivePerson.getName(), templatePage.getName(), templatePage.getId());
			}
			wrap = outCopier.copy(templatePage);
			result.setData(wrap);
			return result;
		}
	}

}