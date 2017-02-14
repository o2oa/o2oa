package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.http.WrapOutMap;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.CompanyStub;
import com.x.processplatform.assemble.bam.stub.DepartmentStub;
import com.x.processplatform.assemble.bam.stub.PersonStub;

public class TimerOrganization extends ActionBase {

	public WrapOutMap execute(Business business) throws Exception {
		WrapOutMap wrap = new WrapOutMap();
		Date start = this.getStart();
		Date current = new Date();
		wrap.put("company", this.company(business, start, current));
		wrap.put("department", this.department(business, start, current));
		wrap.put("person", this.person(business, start, current));
		return wrap;
	}

	private List<WrapOutMap> company(Business business, Date start, Date current) throws Exception {
		List<WrapOutMap> list = new ArrayList<>();
		for (CompanyStub stub : ThisApplication.state.getCompanyStubs()) {
			Long count = business.task().count(start, stub);
			Long expiredCount = business.task().expiredCount(start, current, stub);
			Long duration = business.task().duration(start, current, stub);
			Long completedCount = business.taskCompleted().count(start, stub);
			Long completedExpiredCount = business.taskCompleted().expiredCount(start, stub);
			WrapOutMap wrap = new WrapOutMap();
			wrap.put("name", stub.getName());
			wrap.put("value", stub.getValue());
			wrap.put("count", count);
			wrap.put("expiredCount", expiredCount);
			wrap.put("duration", duration);
			wrap.put("completedCount", completedCount);
			wrap.put("completedExpiredCount", completedExpiredCount);
			list.add(wrap);
		}
		SortTools.desc(list, "count");
		return list;
	}

	private List<WrapOutMap> department(Business business, Date start, Date current) throws Exception {
		List<WrapOutMap> list = new ArrayList<>();
		for (DepartmentStub stub : ThisApplication.state.getDepartmentStubs()) {
			Long count = business.task().count(start, stub);
			Long expiredCount = business.task().expiredCount(start, current, stub);
			Long duration = business.task().duration(start, current, stub);
			Long completedCount = business.taskCompleted().count(start, stub);
			Long completedExpiredCount = business.taskCompleted().expiredCount(start, stub);
			WrapOutMap wrap = new WrapOutMap();
			wrap.put("name", stub.getName());
			wrap.put("value", stub.getValue());
			wrap.put("count", count);
			wrap.put("expiredCount", expiredCount);
			wrap.put("duration", duration);
			wrap.put("completedCount", completedCount);
			wrap.put("completedExpiredCount", completedExpiredCount);
			list.add(wrap);
		}
		SortTools.desc(list, "count");
		return list;
	}

	private List<WrapOutMap> person(Business business, Date start, Date current) throws Exception {
		List<WrapOutMap> list = new ArrayList<>();
		for (PersonStub stub : ThisApplication.state.getPersonStubs()) {
			Long count = business.task().count(start, stub);
			Long expiredCount = business.task().expiredCount(start, current, stub);
			Long duration = business.task().duration(start, current, stub);
			Long completedCount = business.taskCompleted().count(start, stub);
			Long completedExpiredCount = business.taskCompleted().expiredCount(start, stub);
			WrapOutMap wrap = new WrapOutMap();
			wrap.put("name", stub.getName());
			wrap.put("value", stub.getValue());
			wrap.put("count", count);
			wrap.put("expiredCount", expiredCount);
			wrap.put("duration", duration);
			wrap.put("completedCount", completedCount);
			wrap.put("completedExpiredCount", completedExpiredCount);
			list.add(wrap);
		}
		SortTools.desc(list, "count");
		return list;
	}

}