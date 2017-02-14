package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpAttribute;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.utils.DateRange;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.ActivityStub;
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.ProcessStub;

class ActionListCountExpiredTaskByActivity extends ActionListCountExpiredTask {

	ActionResult<WrapOutMap> execute(String applicationId, String processId, String companyName, String departmentName,
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
				for (ActivityStub stub : this.listStub(applicationId, processId)) {
					WrapOutMap p = new WrapOutMap();
					p.put("name", stub.getName());
					p.put("value", stub.getValue());
					p.put("applicationCategory", stub.getApplicationCategory());
					p.put("applicationName", stub.getApplicationName());
					p.put("applicationValue", stub.getApplicationValue());
					p.put("processName", stub.getProcessName());
					p.put("processValue", stub.getProcessValue());
					p.put("count", this.count(business, o, applicationId, processId, stub.getValue(), companyName,
							departmentName, personName));
					list.add(p);
				}
			}
			result.setData(wrap);
			return result;
		}
	}

	private List<ActivityStub> listStub(String applicationId, String processId) throws Exception {
		List<ActivityStub> list = new ArrayList<>();
		for (ApplicationStub a : ThisApplication.period.getExpiredTaskApplicationStubs()) {
			if (StringUtils.equals(a.getValue(), applicationId)
					|| StringUtils.equals(applicationId, HttpAttribute.x_empty_symbol)) {
				for (ProcessStub p : a.getProcessStubs()) {
					if (StringUtils.equals(p.getValue(), processId)
							|| StringUtils.equals(processId, HttpAttribute.x_empty_symbol)) {
						list.addAll(p.getActivityStubs());
					}
				}
			}
		}
		return list;
	}

}