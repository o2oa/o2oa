package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.core.entity.element.TemplateForm;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			TemplateForm o = emc.find(id, TemplateForm.class);
			if (null == o) {
				throw new ExceptionTemplateFormNotExist(id);
			}
			Wo wo = Wo.copier.copy(o);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends TemplateForm {

		private static final long serialVersionUID = 2475165883507548650L;

		static WrapCopier<TemplateForm, Wo> copier = WrapCopierFactory.wo(TemplateForm.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}