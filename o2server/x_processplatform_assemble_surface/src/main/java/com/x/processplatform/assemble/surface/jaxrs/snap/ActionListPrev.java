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

class ActionListPrev extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {
		Predicate p = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			p = myFilter(effectivePerson, business);
		}
		return this.standardListPrev(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, DESC, p);
	}

	public static class Wo extends RankWo {
		private static final long serialVersionUID = 6502735971303449356L;

		static WrapCopier<Snap, Wo> copier = WrapCopierFactory.wo(Snap.class, Wo.class,
				JpaObject.singularAttributeField(Snap.class, true, true), ListTools.toList(Snap.properties_FIELDNAME));
	}

}
