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
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.ProcessStub;

class ActionListCountCompletedTaskByProcess extends ActionListCountCompletedTask {

	ActionResult<WrapOutMap> execute(String applicatonId, String companyName, String departmentName, String personName)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			Business business = new Business(emc);
			List<DateRange> os = this.listDateRange();
			WrapOutMap wrap = new WrapOutMap();
			for (DateRange o : os) {
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				List<WrapOutMap> list = new ArrayList<>();
				wrap.put(str, list);
				for (ProcessStub stub : this.listStub(applicatonId)) {
					WrapOutMap pair = new WrapOutMap();
					pair.put("name", stub.getName());
					pair.put("value", stub.getValue());
					pair.put("applicationCategory", stub.getApplicationCategory());
					pair.put("applicationName", stub.getApplicationName());
					pair.put("applicationValue", stub.getApplicationValue());
					Long count = this.count(business, o, applicatonId, stub.getValue(), HttpAttribute.x_empty_symbol,
							companyName, departmentName, personName);
					pair.put("count", count);
					Long duration = this.duration(business, o, applicatonId, stub.getValue(),
							HttpAttribute.x_empty_symbol, companyName, departmentName, personName);
					pair.put("duration", duration);
					list.add(pair);
				}
			}
			result.setData(wrap);
			return result;
		}
	}

	private List<ProcessStub> listStub(String applicationId) throws Exception {
		List<ProcessStub> list = new ArrayList<>();
		for (ApplicationStub o : ThisApplication.period.getCompletedTaskApplicationStubs()) {
			if (StringUtils.equals(o.getValue(), applicationId)
					|| StringUtils.equals(applicationId, HttpAttribute.x_empty_symbol)) {
				list.addAll(o.getProcessStubs());
			}
		}
		return list;
	}
}