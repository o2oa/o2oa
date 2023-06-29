package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.element.Application;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			SerialNumber o = emc.find(id, SerialNumber.class);
			if (null == o) {
				throw new ExceptionSerialNumberNotExist(id);
			}
			Application application = business.application().pick(o.getApplication());
			if (null == application) {
				throw new ExceptionApplicationNotExist(o.getApplication());
			}
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, null)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = Wo.copier.copy(o);
			wo.setProcessName(business.process().pick(wo.getProcess()).getName());
			result.setData(wo);
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
