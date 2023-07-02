package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateRange;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.assemble.bam.Business;

class ActionListCountExpiredWork extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionListCountExpiredWork.class);

	ActionResult<List<Wo>> execute(String applicationId, String processId, String unit, String person)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> list = new ArrayList<>();
			Business business = new Business(emc);
			List<DateRange> os = this.listDateRange();
			for (DateRange o : os) {
				Wo w = new Wo();
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				w.setName(str);
				w.setValue(str);
				w.setCount(this.countExpiredWork(business, o, applicationId, processId, unit, person));
				list.add(w);
			}
			result.setData(list);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("名称")
		private String name;
		@FieldDescribe("值")
		private String value;
		@FieldDescribe("数量")
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
	}

}