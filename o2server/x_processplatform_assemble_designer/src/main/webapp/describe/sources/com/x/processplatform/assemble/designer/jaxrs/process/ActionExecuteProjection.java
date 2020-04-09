package com.x.processplatform.assemble.designer.jaxrs.process;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.ThisApplication;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionExecuteProjection extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Process process = emc.flag(flag, Process.class);
			if (null == process) {
				throw new ExceptionEntityNotExist(flag, Process.class);
			}
			Application application = emc.flag(process.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(process.getApplication(), Application.class);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			Wo wo = new Wo();
			if (StringUtils.isNotEmpty(process.getProjection()) && XGsonBuilder.isJson(process.getProjection())) {
				if (!ThisApplication.projectionExecuteQueue.contains(process.getId())) {
					ThisApplication.projectionExecuteQueue.send(process.getId());
					wo.setValue(true);
				} else {
					throw new ExceptionAlreadyAddQueue();
				}
			} else {
				wo.setValue(false);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

	}

	public static class Wo extends WrapBoolean {

	}

}