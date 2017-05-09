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
import com.x.processplatform.assemble.bam.stub.CompanyStub;
import com.x.processplatform.assemble.bam.stub.DepartmentStub;

class ActionListCountExpiredWorkByDepartment extends ActionListCountExpiredWork {

	ActionResult<WrapOutMap> execute(String applicationId, String processId, String companyName) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			Business business = new Business(emc);
			WrapOutMap wrap = new WrapOutMap();
			List<DateRange> os = this.listDateRange();
			for (DateRange o : os) {
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				List<WrapOutMap> list = new ArrayList<>();
				wrap.put(str, list);
				for (DepartmentStub stub : this.listStub(companyName)) {
					WrapOutMap p = new WrapOutMap();
					p.put("name", stub.getName());
					p.put("value", stub.getValue());
					p.put("level", stub.getLevel());
					p.put("companyName", stub.getCompanyName());
					p.put("companyValue", stub.getCompanyValue());
					p.put("companyLevel", stub.getCompanyLevel());
					p.put("count", this.count(business, o, applicationId, processId, companyName, stub.getValue(),
							StandardJaxrsAction.EMPTY_SYMBOL));
					list.add(p);
				}
			}
			result.setData(wrap);
			return result;
		}
	}

	private List<DepartmentStub> listStub(String companyName) throws Exception {
		List<DepartmentStub> list = new ArrayList<>();
		for (CompanyStub o : ThisApplication.period.getExpiredWorkCompanyStubs()) {
			if (StringUtils.equals(o.getValue(), companyName)
					|| StringUtils.equals(companyName, StandardJaxrsAction.EMPTY_SYMBOL)) {
				list.addAll(o.getDepartmentStubs());
			}
		}
		return list;
	}

}