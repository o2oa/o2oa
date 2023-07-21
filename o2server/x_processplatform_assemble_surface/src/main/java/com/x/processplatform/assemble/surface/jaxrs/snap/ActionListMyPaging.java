package com.x.processplatform.assemble.surface.jaxrs.snap;

import java.util.List;

import javax.persistence.criteria.Predicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Snap;

class ActionListMyPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Predicate p = this.myFilter(effectivePerson, business);
			ActionResult<List<Wo>> result = new ActionResult<>();
			result.setData(emc.fetchDescPaging(Snap.class, Wo.copier, p, page, size, JpaObject.sequence_FIELDNAME));
			result.setCount(emc.count(Snap.class, p));
			return result;
		}
	}

	public static class Wo extends RankWo {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Snap, Wo> copier = WrapCopierFactory.wo(Snap.class, Wo.class,
				JpaObject.singularAttributeField(Snap.class, true, true), ListTools.toList(Snap.properties_FIELDNAME));

	}

}
