package com.x.processplatform.assemble.designer.jaxrs.processversion;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.ProcessVersion;

class ActionListWithProcess extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String processId) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			ActionResult<List<Wo>> result = new ActionResult<>();

			Business business = new Business(emc);
			Process process = emc.find(processId, Process.class);

			if (null == process) {
				throw new ExceptionEntityNotExist(processId, Process.class);
			}

			Application application = emc.find(process.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(process.getApplication(), Application.class);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			List<Wo> wos = emc.fetchEqual(ProcessVersion.class, Wo.copier, ProcessVersion.process_FIELDNAME,
					process.getId());

			result.setData(wos);
			return result;

		}
	}

	public static class Wo extends ProcessVersion {

		private static final long serialVersionUID = -2398096870126935605L;
		static WrapCopier<ProcessVersion, Wo> copier = WrapCopierFactory.wo(ProcessVersion.class, Wo.class,
				JpaObject.singularAttributeField(ProcessVersion.class, true, true), null);

	}
}