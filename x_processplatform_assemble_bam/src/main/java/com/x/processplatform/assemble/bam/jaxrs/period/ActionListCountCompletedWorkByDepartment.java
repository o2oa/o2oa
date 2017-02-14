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
import com.x.processplatform.assemble.bam.stub.CompanyStub;
import com.x.processplatform.assemble.bam.stub.DepartmentStub;

class ActionListCountCompletedWorkByDepartment extends ActionListCountCompletedWork {

	ActionResult<WrapOutMap> execute(String applicationId, String processId, String companyName) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			Business business = new Business(emc);
			List<DateRange> os = this.listDateRange();
			WrapOutMap wrap = new WrapOutMap();
			for (DateRange o : os) {
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				List<WrapOutMap> list = new ArrayList<>();
				wrap.put(str, list);
				for (DepartmentStub stub : this.listStub(companyName)) {
					WrapOutMap pair = new WrapOutMap();
					pair.put("name", stub.getName());
					pair.put("value", stub.getValue());
					pair.put("level", stub.getLevel());
					pair.put("companyName", stub.getCompanyName());
					pair.put("companyValue", stub.getCompanyValue());
					pair.put("companyLevel", stub.getCompanyLevel());
					Long count = this.count(business, o, applicationId, processId, companyName, stub.getValue(),
							HttpAttribute.x_empty_symbol);
					pair.put("count", count);
					Long duration = this.duration(business, o, applicationId, processId, companyName, stub.getValue(),
							HttpAttribute.x_empty_symbol);
					pair.put("duration", duration);
					Long times = this.times(business, o, applicationId, processId, companyName, stub.getValue(),
							HttpAttribute.x_empty_symbol);
					pair.put("times", times);
					list.add(pair);
				}
			}
			result.setData(wrap);
			return result;
		}
	}

	private List<DepartmentStub> listStub(String companyName) throws Exception {
		List<DepartmentStub> list = new ArrayList<>();
		for (CompanyStub o : ThisApplication.period.getCompletedWorkCompanyStubs()) {
			if (StringUtils.equals(o.getValue(), companyName)
					|| StringUtils.equals(companyName, HttpAttribute.x_empty_symbol)) {
				list.addAll(o.getDepartmentStubs());
			}
		}
		return list;
	}
}