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

class ActionListCountExpiredWorkByApplication extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionListCountExpiredWorkByApplication.class);

	ActionResult<Wo> execute(String unit, String person) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			List<DateRange> os = this.listDateRange();
			for (DateRange o : os) {
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				List<WoApplication> list = new ArrayList<>();
				wo.put(str, list);
				for (ApplicationStub stub : this.listStub()) {
					WoApplication w = new WoApplication();
					w.setCategory(stub.getCategory());
					w.setName(stub.getName());
					w.setValue(stub.getValue());
					w.setCount(this.countExpiredWork(business, o, stub.getValue(), StandardJaxrsAction.EMPTY_SYMBOL,
							unit, person));
					list.add(w);
				}
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends LinkedHashMap<String, List<WoApplication>> {

		private static final long serialVersionUID = -128287103911021541L;

	}

	public static class WoApplication extends GsonPropertyObject {

		private String category;
		private String name;
		private String value;
		private Long count;

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

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

	}

	private List<ApplicationStub> listStub() throws Exception {
		List<ApplicationStub> list = new ArrayList<>();
		for (ApplicationStub o : ThisApplication.period.getExpiredWorkApplicationStubs()) {
			list.add(o);
		}
		return list;
	}

}