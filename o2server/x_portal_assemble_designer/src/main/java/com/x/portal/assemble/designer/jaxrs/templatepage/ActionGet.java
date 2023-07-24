package com.x.portal.assemble.designer.jaxrs.templatepage;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.TemplatePage;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			TemplatePage templatePage = emc.find(id, TemplatePage.class);
			if (null == templatePage) {
				throw new TemplatePageNotExistedException(id);
			}
			if (!business.templatePage().editable(effectivePerson, templatePage)) {
				throw new TemplatePageInvisibleException(effectivePerson.getDistinguishedName(), templatePage.getName(),
						templatePage.getId());
			}
			Wo wo = Wo.copier.copy(templatePage);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends TemplatePage {

		private static final long serialVersionUID = -7592184343034018992L;

		static WrapCopier<TemplatePage, Wo> copier = WrapCopierFactory.wo(TemplatePage.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}