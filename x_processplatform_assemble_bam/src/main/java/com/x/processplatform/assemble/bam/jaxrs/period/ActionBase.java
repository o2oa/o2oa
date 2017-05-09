package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.utils.DateRange;
import com.x.base.core.utils.DateTools;

class ActionBase extends StandardJaxrsAction {

	List<DateRange> listDateRange() throws Exception {
		List<DateRange> list = new ArrayList<>();
		Date now = new Date();
		for (int i = -1; i >= -12; i--) {
			Date start = DateTools.floorMonth(now, i);
			Date end = DateTools.ceilMonth(now, i);
			DateRange range = new DateRange(start, end);
			list.add(range);
		}
		return list;
	}
	
	DateRange getDateRange() throws Exception {
		Date now = new Date();
		Date start = DateTools.floorMonth(now, -12);
		Date end = DateTools.ceilMonth(now, -1);
		DateRange range = new DateRange(start, end);
		return range;
	}

}
