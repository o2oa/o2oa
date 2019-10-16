package com.x.program.center.jaxrs.schedule;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.ScheduleRequest;
import com.x.program.center.ThisApplication;

class ActionFire extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionFire.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Application application = ThisApplication.context().applications().get(wi.getApplication(), wi.getNode());
			ScheduleRequest request = null;

			for (ScheduleRequest o : application.getScheduleRequestList()) {
				if (StringUtils.equalsIgnoreCase(wi.getClassName(), o.getClassName())) {
					request = o;
					break;
				}
			}
			this.fire(effectivePerson, application, request);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private void fire(EffectivePerson effectivePerson, Application application, ScheduleRequest request)
			throws Exception {
		try {
			String url = application.getUrlRoot()
					+ Applications.joinQueryUri("fireschedule", "classname", request.getClassName());
			CipherConnectionAction.get(effectivePerson.getDebugger(), url);
			request.setLastStartTime(new Date());
			logger.info("fire schedule className: {}, application: {}, node: {}.", request.getClassName(),
					application.getClassName(), application.getNode());
		} catch (Exception e) {
			throw new ExceptionFire(e, request.getClassName(), application.getClassName(), application.getNode());
		}
	}

	public static class Wi extends GsonPropertyObject {

		private String node;
		private String application;
		private String className;

		public String getNode() {
			return node;
		}

		public void setNode(String node) {
			this.node = node;
		}

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

	}

	public static class Wo extends WrapBoolean {
	}

}
