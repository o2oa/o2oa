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

class ActionListCountCompletedTask extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListCountCompletedTask.class);

	ActionResult<List<Wo>> execute(String applicationId, String processId, String activityId, String unit,
			String person) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> list = new ArrayList<>();
			Business business = new Business(emc);
			List<DateRange> os = this.listDateRange();
			for (DateRange o : os) {
				Wo wo = new Wo();
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				wo.setName(str);
				wo.setValue(str);
				Long count = this.countCompletedTask(business, o, applicationId, processId, activityId, unit,
						person);
				wo.setCount(count);
				Long duration = this.durationCompletedTask(business, o, applicationId, processId, activityId, unit,
						person);
				wo.setDuration(duration);
				list.add(wo);
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

		@FieldDescribe("总时长")
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

}