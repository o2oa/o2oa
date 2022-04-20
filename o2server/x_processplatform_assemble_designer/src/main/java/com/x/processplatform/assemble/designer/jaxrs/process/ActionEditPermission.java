package com.x.processplatform.assemble.designer.jaxrs.process;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.MessageFactory;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionEditPermission extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = emc.find(id, Process.class);
			if (null == process) {
				throw new ExceptionProcessNotExisted(id);
			}
			Application application = emc.find(process.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(process.getApplication());
			}
			if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			emc.beginTransaction(Process.class);
			Wi.copier.copy(wi, process);
			process.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			process.setLastUpdateTime(new Date());
			emc.commit();
			cacheNotify();
			Wo wo = new Wo();
			wo.setId(process.getId());
			result.setData(wo);
			MessageFactory.process_update(process);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 3775499690533897285L;

	}

	public static class Wi extends Process {

		private static final long serialVersionUID = 8978725375761690192L;

		static WrapCopier<Wi, Process> copier = WrapCopierFactory.wi(Wi.class, Process.class,
				ListTools.toList(Process.startableIdentityList_FIELDNAME, Process.startableUnitList_FIELDNAME,
						Process.startableGroupList_FIELDNAME, Process.controllerList_FIELDNAME),
				null);

	}
}
