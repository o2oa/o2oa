package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateRange;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.ActivityStub;
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.ProcessStub;

class ActionListCountCompletedTaskByActivity extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListCountCompletedTaskByActivity.class);
	
	ActionResult<Wo> execute(String applicatonId, String processId, String unit, String person) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			List<DateRange> os = this.listDateRange();
			Wo wo = new Wo();
			for (DateRange o : os) {
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				List<WoActivity> list = new ArrayList<>();
				wo.put(str, list);
				for (ActivityStub stub : this.listStub(applicatonId, processId)) {
					WoActivity w = new WoActivity();
					w.setName(stub.getName());
					w.setValue(stub.getValue());
					w.setApplicationCategory(stub.getApplicationCategory());
					w.setApplicationName(stub.getApplicationName());
					w.setApplicationValue(stub.getApplicationValue());
					w.setProcessName(stub.getProcessName());
					w.setProcessValue(stub.getProcessValue());
					Long count = this.countCompletedTask(business, o, applicatonId, processId, stub.getValue(), unit,
							person);
					w.setCount(count);
					Long duration = this.durationCompletedTask(business, o, applicatonId, processId, stub.getValue(),
							unit, person);
					w.setDuration(duration);
					list.add(w);
				}
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends LinkedHashMap<String, List<WoActivity>> {

		private static final long serialVersionUID = -786616496326467659L;

	}

	public static class WoActivity extends GsonPropertyObject {
		private String name;
		private String value;
		private String applicationCategory;
		private String applicationName;
		private String applicationValue;
		private String processName;
		private String processValue;
		private Long count;
		private Long duration;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getApplicationCategory() {
			return applicationCategory;
		}

		public void setApplicationCategory(String applicationCategory) {
			this.applicationCategory = applicationCategory;
		}

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

		public String getApplicationValue() {
			return applicationValue;
		}

		public void setApplicationValue(String applicationValue) {
			this.applicationValue = applicationValue;
		}

		public String getProcessName() {
			return processName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}

		public String getProcessValue() {
			return processValue;
		}

		public void setProcessValue(String processValue) {
			this.processValue = processValue;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

		public Long getDuration() {
			return duration;
		}

		public void setDuration(Long duration) {
			this.duration = duration;
		}

	}

	private List<ActivityStub> listStub(String applicationId, String processId) throws Exception {
		List<ActivityStub> list = new ArrayList<>();
		for (ApplicationStub a : ThisApplication.period.getCompletedTaskApplicationStubs()) {
			if (StringUtils.equals(a.getValue(), applicationId)
					|| StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
				for (ProcessStub p : a.getProcessStubs()) {
					if (StringUtils.equals(p.getValue(), processId)
							|| StringUtils.equals(processId, StandardJaxrsAction.EMPTY_SYMBOL)) {
						list.addAll(p.getActivityStubs());
					}
				}
			}
		}
		return list;
	}
}