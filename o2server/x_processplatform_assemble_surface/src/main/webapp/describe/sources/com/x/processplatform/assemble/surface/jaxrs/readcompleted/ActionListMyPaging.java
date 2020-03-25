package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.core.entity.content.ReadCompleted;

class ActionListMyPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = emc.fetchEqualDescPaging(ReadCompleted.class, Wo.copier, ReadCompleted.person_FIELDNAME,
					effectivePerson.getDistinguishedName(), page, size, JpaObject.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.countEqual(ReadCompleted.class, ReadCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName()));
			return result;
		}
	}

	public static class Wo extends ReadCompleted {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<ReadCompleted, Wo> copier = WrapCopierFactory.wo(ReadCompleted.class, Wo.class,
				JpaObject.singularAttributeField(ReadCompleted.class, true, true), null);

	}

}
