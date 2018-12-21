package com.x.processplatform.assemble.surface.jaxrs.worklog;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.WorkLog;

class ActionListWithJob extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String job) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			Long count = business.review().countWithPersonWithJob(effectivePerson.getDistinguishedName(), job);
			if (count > 0) {
				List<WorkLog> os = business.workLog().listWithJobObject(job);
				os = business.workLog().sort(os);
				wos = Wo.copier.copy(os);
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends WorkLog {

		private static final long serialVersionUID = -7666329770246726197L;

		static WrapCopier<WorkLog, Wo> copier = WrapCopierFactory.wo(WorkLog.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}