package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;

class ActionList extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<String> ids = this.listEditable(business, effectivePerson);
			List<Wo> wos = Wo.copier.copy(emc.list(Portal.class, ids));
			wos = wos.stream().sorted(Comparator.comparing(Wo::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Portal {

		private static final long serialVersionUID = -1369499996236744766L;

		static WrapCopier<Portal, Wo> copier = WrapCopierFactory.wo(Portal.class, Wo.class,
				JpaObject.singularAttributeField(Portal.class, true, false), null);
	}
}