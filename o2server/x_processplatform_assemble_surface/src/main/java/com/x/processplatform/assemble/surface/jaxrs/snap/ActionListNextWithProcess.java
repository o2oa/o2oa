package com.x.processplatform.assemble.surface.jaxrs.snap;

import java.util.List;

import javax.persistence.criteria.Predicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Snap;
import com.x.processplatform.core.entity.element.Process;

class ActionListNextWithProcess extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, String processFlag)
			throws Exception {
		Predicate p = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = business.process().pick(processFlag);
			if (null == process) {
				throw new ExceptionEntityNotExist(processFlag, Process.class);
			}
			p = this.myProcessFilter(effectivePerson, business, process);
		}
		return this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, DESC, p);
	}

	public static class Wo extends RankWo {

		private static final long serialVersionUID = 2988668146783909521L;

		static WrapCopier<Snap, Wo> copier = WrapCopierFactory.wo(Snap.class, Wo.class,
				JpaObject.singularAttributeField(Snap.class, true, true), ListTools.toList(Snap.properties_FIELDNAME));

	}
}
