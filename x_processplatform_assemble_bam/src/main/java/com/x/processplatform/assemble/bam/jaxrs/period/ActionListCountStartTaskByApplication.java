package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.ArrayList;
import java.util.List;

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

class ActionListCountStartTaskByApplication extends ActionListCountStartTask {

	ActionResult<WrapOutMap> execute(String companyName, String departmentName, String personName) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			Business business = new Business(emc);
			List<DateRange> os = this.listDateRange();
			WrapOutMap wrap = new WrapOutMap();
			for (DateRange o : os) {
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				List<WrapOutMap> list = new ArrayList<>();
				wrap.put(str, list);
				for (ApplicationStub stub : this.listStub()) {
					WrapOutMap p = new WrapOutMap();
					p.put("category", stub.getCategory());
					p.put("name", stub.getName());
					p.put("value", stub.getValue());
					p.put("count", this.count(business, o, stub.getValue(), HttpAttribute.x_empty_symbol,
							HttpAttribute.x_empty_symbol, companyName, departmentName, personName));
					list.add(p);
				}
			}
			result.setData(wrap);
			return result;
		}
	}

	private List<ApplicationStub> listStub() throws Exception {
		List<ApplicationStub> list = new ArrayList<>();
		for (ApplicationStub o : ThisApplication.period.getStartTaskApplicationStubs()) {
			list.add(o);
		}
		return list;
	}

}