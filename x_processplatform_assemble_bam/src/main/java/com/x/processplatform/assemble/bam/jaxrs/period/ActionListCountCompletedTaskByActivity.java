package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.utils.DateRange;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.ActivityStub;
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.ProcessStub;

class ActionListCountCompletedTaskByActivity extends ActionListCountCompletedTask {

	ActionResult<WrapOutMap> execute(String applicatonId, String processId, String companyName, String departmentName,
			String personName) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			Business business = new Business(emc);
			List<DateRange> os = this.listDateRange();
			WrapOutMap wrap = new WrapOutMap();
			for (DateRange o : os) {
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				List<WrapOutMap> list = new ArrayList<>();
				wrap.put(str, list);
				for (ActivityStub stub : this.listStub(applicatonId, processId)) {
					WrapOutMap pair = new WrapOutMap();
					pair.put("name", stub.getName());
					pair.put("value", stub.getValue());
					pair.put("applicationCategory", stub.getApplicationCategory());
					pair.put("applicationName", stub.getApplicationName());
					pair.put("applicationValue", stub.getApplicationValue());
					pair.put("processName", stub.getProcessName());
					pair.put("processValue", stub.getProcessValue());
					Long count = this.count(business, o, applicatonId, processId, stub.getValue(), companyName,
							departmentName, personName);
					pair.put("count", count);
					Long duration = this.duration(business, o, applicatonId, processId, stub.getValue(), companyName,
							departmentName, personName);
					pair.put("duration", duration);
					list.add(pair);
				}
			}
			result.setData(wrap);
			return result;
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