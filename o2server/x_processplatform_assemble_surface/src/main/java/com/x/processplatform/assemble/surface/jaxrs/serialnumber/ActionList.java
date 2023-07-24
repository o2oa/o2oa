package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.element.Application;

class ActionList extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EqualsTerms equals = new EqualsTerms();
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			equals.put("application", application.getId());
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, null)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<String> ids = business.serialNumber().listWithApplication(application);
			List<Wo> wos = Wo.copier.copy(emc.list(SerialNumber.class, ids));
			for (Wo wo : wos) {
				wo.setProcessName(business.process().pick(wo.getProcess()).getName());
			}
			wos = wos.stream()
					.sorted(Comparator.comparing(Wo::getProcessName, Comparator.nullsLast(String::compareTo))
							.thenComparing(Wo::getProcess, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
		}
		return result;
	}

	public static class Wo extends SerialNumber {

		private static final long serialVersionUID = -8477113306530730090L;
		static WrapCopier<SerialNumber, Wo> copier = WrapCopierFactory.wo(SerialNumber.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("流程名称")
		private String processName;

		public String getProcessName() {
			return processName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}
	}

}
