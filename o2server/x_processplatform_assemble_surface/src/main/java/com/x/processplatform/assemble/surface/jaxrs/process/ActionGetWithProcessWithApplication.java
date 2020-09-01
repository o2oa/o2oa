package com.x.processplatform.assemble.surface.jaxrs.process;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ActionGetWithProcessWithApplication extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			Process process = business.process().pick(application, flag);
			if (null == process) {
				throw new ExceptionEntityNotExist(flag, Process.class);
			}
			if (StringUtils.isNotEmpty(process.getEdition()) && BooleanUtils.isFalse(process.getEditionEnable())) {
				process = business.process().pickEnabled(process.getApplication(), process.getEdition());
			}
			Wo wo = Wo.copier.copy(process);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Process {

		private static final long serialVersionUID = 1521228691441978462L;

		static WrapCopier<Process, Wo> copier = WrapCopierFactory.wo(Process.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}
}