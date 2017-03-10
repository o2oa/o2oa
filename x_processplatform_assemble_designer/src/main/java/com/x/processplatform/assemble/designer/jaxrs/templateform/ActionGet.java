package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.designer.wrapout.WrapOutTemplateForm;
import com.x.processplatform.core.entity.element.TemplateForm;

class ActionGet extends ActionBase {
	ActionResult<WrapOutTemplateForm> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutTemplateForm> result = new ActionResult<>();
			TemplateForm o = emc.find(id, TemplateForm.class, ExceptionWhen.not_found);
			WrapOutTemplateForm wrap = outCopier.copy(o);
			result.setData(wrap);
			return result;
		}
	}
}