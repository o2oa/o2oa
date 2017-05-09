package com.x.portal.assemble.designer.jaxrs.templatepage;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapout.WrapOutTemplatePage;
import com.x.portal.core.entity.TemplatePage;

class ActionListWithCategory extends ActionBase {
	ActionResult<List<WrapOutTemplatePage>> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<WrapOutTemplatePage>> result = new ActionResult<>();
			String category = XGsonBuilder.extractString(jsonElement, "category");
			List<String> ids = business.templatePage().listEditableWithCategory(effectivePerson, category);
			List<WrapOutTemplatePage> wraps = outCopier.copy(emc.list(TemplatePage.class, ids));
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}

}