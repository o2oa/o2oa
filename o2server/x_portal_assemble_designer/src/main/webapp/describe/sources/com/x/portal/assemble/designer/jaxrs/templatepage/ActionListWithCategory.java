package com.x.portal.assemble.designer.jaxrs.templatepage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.TemplatePage;

class ActionListWithCategory extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			String category = XGsonBuilder.extractString(jsonElement, TemplatePage.category_FIELDNAME);
			List<String> ids = business.templatePage().listEditableWithCategory(effectivePerson, category);
			List<Wo> wos = Wo.copier.copy(emc.list(TemplatePage.class, ids));
			wos = wos.stream().sorted(Comparator.comparing(Wo::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends TemplatePage {

		private static final long serialVersionUID = 4610930870555814195L;

		static WrapCopier<TemplatePage, Wo> copier = WrapCopierFactory.wo(TemplatePage.class, Wo.class,
				JpaObject.singularAttributeField(TemplatePage.class, true, true), null);

	}

}