package com.x.program.center.jaxrs.schedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Application;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.ScheduleLocalRequest;
import com.x.program.center.ThisApplication;

class ActionListScheduleLocal extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListScheduleLocal.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		for (Entry<String, CopyOnWriteArrayList<Application>> entry : ThisApplication.context().applications()
				.entrySet()) {
			for (Application application : entry.getValue()) {
				for (ScheduleLocalRequest request : application.getScheduleLocalRequestList()) {
					Wo wo = Wo.copier.copy(request);
					wo.setApplication(application.getClassName());
					wo.setNode(application.getNode());
					wos.add(wo);
				}
			}
		}
		wos = wos.stream().sorted(
				Comparator.comparing(Wo::getApplication).thenComparing(Wo::getClassName).thenComparing(Wo::getNode))
				.collect(Collectors.toList());
		result.setData(wos);
		return result;
	}

	public static class Wo extends ScheduleLocalRequest {

		private String application;

		private String node;

		static WrapCopier<ScheduleLocalRequest, Wo> copier = WrapCopierFactory.wo(ScheduleLocalRequest.class, Wo.class,
				null, JpaObject.FieldsInvisible);

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getNode() {
			return node;
		}

		public void setNode(String node) {
			this.node = node;
		}

	}
}