package com.x.processplatform.assemble.surface.jaxrs.snap;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Snap;

class ActionManageListPrev extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (BooleanUtils.isNotTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "",""))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		return this.standardListPrev(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, DESC, null);
	}

	public static class Wo extends RankWo {

		private static final long serialVersionUID = 7593097665520980060L;

		static WrapCopier<Snap, Wo> copier = WrapCopierFactory.wo(Snap.class, Wo.class,
				JpaObject.singularAttributeField(Snap.class, true, true), null);

	}

}
