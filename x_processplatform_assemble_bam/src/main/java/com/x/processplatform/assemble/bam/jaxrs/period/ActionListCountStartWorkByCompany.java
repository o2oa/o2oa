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
import com.x.processplatform.assemble.bam.stub.CompanyStub;

class ActionListCountStartWorkByCompany extends ActionListCountStartWork {

	ActionResult<WrapOutMap> execute(String applicationId, String processId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			Business business = new Business(emc);
			List<DateRange> os = this.listDateRange();
			WrapOutMap wrap = new WrapOutMap();
			for (DateRange o : os) {
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				List<WrapOutMap> list = new ArrayList<>();
				wrap.put(str, list);
				for (CompanyStub stub : this.listStub()) {
					WrapOutMap p = new WrapOutMap();
					p.put("name", stub.getName());
					p.put("value", stub.getValue());
					p.put("level", stub.getLevel());
					p.put("count", this.count(business, o, applicationId, processId, stub.getValue(),
							HttpAttribute.x_empty_symbol, HttpAttribute.x_empty_symbol));
					list.add(p);
				}
			}
			result.setData(wrap);
			return result;
		}
	}

	private List<CompanyStub> listStub() throws Exception {
		List<CompanyStub> list = new ArrayList<>();
		for (CompanyStub o : ThisApplication.period.getStartWorkCompanyStubs()) {
			list.add(o);
		}
		return list;
	}

}