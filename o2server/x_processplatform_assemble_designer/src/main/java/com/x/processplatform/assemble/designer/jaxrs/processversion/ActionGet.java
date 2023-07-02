package com.x.processplatform.assemble.designer.jaxrs.processversion;

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

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			ProcessVersion processVersion = emc.find(id, ProcessVersion.class);
			if (null == processVersion) {
				throw new ExceptionEntityNotExist(id, ProcessVersion.class);
			}
			Process process = emc.find(processVersion.getProcess(), Process.class);
			if (null == process) {
				throw new ExceptionEntityNotExist(processVersion.getProcess(), Process.class);
			}
			Application application = emc.find(process.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(process.getApplication(), Application.class);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = Wo.copier.copy(processVersion);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends ProcessVersion {

		private static final long serialVersionUID = 1541438199059150837L;

		static WrapCopier<ProcessVersion, Wo> copier = WrapCopierFactory.wo(ProcessVersion.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
