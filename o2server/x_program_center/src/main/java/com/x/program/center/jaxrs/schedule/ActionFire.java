package com.x.program.center.jaxrs.schedule;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionFire.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Application application = ThisApplication.context().applications().get(wi.getApplication(), wi.getNode());
			Optional<ScheduleRequest> optional = application.getScheduleRequestList().stream()
					.filter(o -> StringUtils.equalsIgnoreCase(wi.getClassName(), o.getClassName())).findFirst();

			if (optional.isPresent()) {
				this.fire(effectivePerson, application, optional.get());
				wo.setValue(true);
			} else {
				wo.setValue(false);
			}
			result.setData(wo);
			return result;
		}
	}

	private void fire(EffectivePerson effectivePerson, Application application, ScheduleRequest request)
			throws Exception {
		try {
			String url = application.getUrlJaxrsRoot()
					+ Applications.joinQueryUri("fireschedule", "classname", request.getClassName());
			CipherConnectionAction.get(effectivePerson.getDebugger(), url);
			request.setLastStartTime(new Date());
			LOGGER.info("fire schedule className: {}, application: {}, node: {}.", request.getClassName(),
					application.getClassName(), application.getNode());
		} catch (Exception e) {
			throw new ExceptionFire(e, request.getClassName(), application.getClassName(), application.getNode());
		}
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 5415760454523560472L;
		@FieldDescribe("节点")
		private String node;
		@FieldDescribe("应用")
		private String application;
		@FieldDescribe("任务类")
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
