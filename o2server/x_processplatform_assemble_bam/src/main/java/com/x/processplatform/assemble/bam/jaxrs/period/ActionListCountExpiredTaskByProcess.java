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
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.ProcessStub;

class ActionListCountExpiredTaskByProcess extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionListCountExpiredTaskByProcess.class);

	ActionResult<Wo> execute(String applicationId, String unit, String person) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			List<DateRange> os = this.listDateRange();
			Wo wo = new Wo();
			for (DateRange o : os) {
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				List<WoProcess> list = new ArrayList<>();
				wo.put(str, list);
				for (ProcessStub stub : this.listStub(applicationId)) {
					WoProcess w = new WoProcess();
					w.setName(stub.getName());
					w.setValue(stub.getValue());
					w.setApplicationCategory(stub.getApplicationCategory());
					w.setApplicationName(stub.getApplicationName());
					w.setApplicationValue(stub.getApplicationValue());
					w.setCount(this.countExpiredTask(business, o, applicationId, stub.getValue(),
							StandardJaxrsAction.EMPTY_SYMBOL, unit, person));
					list.add(w);
				}
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends LinkedHashMap<String, List<WoProcess>> {

		private static final long serialVersionUID = 2815996571659726120L;

	}

	public static class WoProcess extends GsonPropertyObject {

		private String name;
		private String value;
		private String applicationCategory;
		private String applicationName;
		private String applicationValue;
		private Long count;

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

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
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

	}

	private List<ProcessStub> listStub(String applicationId) throws Exception {
		List<ProcessStub> list = new ArrayList<>();
		for (ApplicationStub o : ThisApplication.period.getExpiredTaskApplicationStubs()) {
			if (StringUtils.equals(o.getValue(), applicationId)
					|| StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
				list.addAll(o.getProcessStubs());
			}
		}
		return list;
	}

}