package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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

class ActionListCountCompletedWorkByApplication extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionListCountCompletedWorkByApplication.class);

	ActionResult<Wo> execute(String unit, String person) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			List<DateRange> os = this.listDateRange();
			Wo wo = new Wo();
			for (DateRange o : os) {
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				List<WoApplication> list = new ArrayList<>();
				wo.put(str, list);
				for (ApplicationStub stub : this.listStub()) {
					WoApplication w = new WoApplication();
					w.setName(stub.getName());
					w.setValue(stub.getValue());
					w.setCategory(stub.getCategory());
					Long count = this.countCompletedWork(business, o, StandardJaxrsAction.EMPTY_SYMBOL,
							StandardJaxrsAction.EMPTY_SYMBOL, unit, person);
					w.setCount(count);
					Long duration = this.durationCompletedWork(business, o, stub.getValue(),
							StandardJaxrsAction.EMPTY_SYMBOL, unit, person);
					w.setDuration(duration);
					Long times = this.timesCompletedWork(business, o, stub.getValue(), StandardJaxrsAction.EMPTY_SYMBOL,
							unit, person);
					w.setTimes(times);
					list.add(w);
				}
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends LinkedHashMap<String, List<WoApplication>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1968898314231408776L;

	}

	public static class WoApplication extends GsonPropertyObject {
		private String name;
		private String value;
		private String category;
		private Long count;
		private Long duration;
		private Long times;

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

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
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

		public Long getTimes() {
			return times;
		}

		public void setTimes(Long times) {
			this.times = times;
		}
	}

	private List<ApplicationStub> listStub() throws Exception {
		List<ApplicationStub> list = new ArrayList<>();
		for (ApplicationStub o : ThisApplication.period.getCompletedWorkApplicationStubs()) {
			list.add(o);
		}
		return list;
	}
}