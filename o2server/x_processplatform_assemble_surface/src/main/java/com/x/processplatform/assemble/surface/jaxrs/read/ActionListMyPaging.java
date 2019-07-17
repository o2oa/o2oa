package com.x.processplatform.assemble.surface.jaxrs.read;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.core.entity.content.Read;

class ActionListMyPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer pageSize) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = emc.fetchEqualDescPaging(Read.class, Wo.copier, Read.person_FIELDNAME,
					effectivePerson.getDistinguishedName(), page, pageSize, JpaObject.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.countEqual(Read.class, Read.person_FIELDNAME, effectivePerson.getDistinguishedName()));
			this.setPage(result, page, pageSize);
			return result;
		}
	}

	public static class Wo extends Read {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Read, Wo> copier = WrapCopierFactory.wo(Read.class, Wo.class,
				JpaObject.singularAttributeField(Read.class, true, true), null);

	}

}
