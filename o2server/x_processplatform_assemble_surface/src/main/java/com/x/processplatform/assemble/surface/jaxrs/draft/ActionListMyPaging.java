package com.x.processplatform.assemble.surface.jaxrs.draft;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.core.entity.content.Draft;

class ActionListMyPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = emc.fetchEqualDescPaging(Draft.class, Wo.copier, Draft.person_FIELDNAME,
					effectivePerson.getDistinguishedName(), page, size, JpaObject.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(
					emc.countEqual(Draft.class, Draft.person_FIELDNAME, effectivePerson.getDistinguishedName()));
			return result;
		}
	}

	public static class Wo extends Draft {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Draft, Wo> copier = WrapCopierFactory.wo(Draft.class, Wo.class,
				JpaObject.singularAttributeField(Draft.class, true, true), null);

	}

}
