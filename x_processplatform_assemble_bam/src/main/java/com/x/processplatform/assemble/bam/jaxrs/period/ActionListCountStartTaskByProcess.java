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
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.ProcessStub;

class ActionListCountStartTaskByProcess extends ActionListCountStartTask {

	ActionResult<WrapOutMap> execute(String applicationId, String companyName, String departmentName, String personName)
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
				for (ProcessStub stub : this.listStub(applicationId)) {
					WrapOutMap p = new WrapOutMap();
					p.put("name", stub.getName());
					p.put("value", stub.getValue());
					p.put("applicationCategory", stub.getApplicationCategory());
					p.put("applicationName", stub.getApplicationName());
					p.put("applicationValue", stub.getApplicationValue());
					p.put("count", this.count(business, o, applicationId, stub.getValue(), StandardJaxrsAction.EMPTY_SYMBOL,
							companyName, departmentName, personName));
					list.add(p);
				}
			}
			result.setData(wrap);
			return result;
		}
	}

	private List<ProcessStub> listStub(String applicationId) throws Exception {
		List<ProcessStub> list = new ArrayList<>();
		for (ApplicationStub o : ThisApplication.period.getStartTaskApplicationStubs()) {
			if (StringUtils.equals(o.getValue(), applicationId)
					|| StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
				list.addAll(o.getProcessStubs());
			}
		}
		return list;
	}

}