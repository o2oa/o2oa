package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.Date;

import com.x.base.core.http.WrapOutMap;
import com.x.processplatform.assemble.bam.Business;

public class TimerRunning extends ActionBase {

	public WrapOutMap execute(Business business) throws Exception {
		WrapOutMap wrap = new WrapOutMap();
		Date start = this.getStart();
		Date current = new Date();
		wrap.put("task", business.task().durationWithPeriodCount(start, current));
		wrap.put("work", business.work().durationWithPeriodCount(start, current));
		return wrap;
	}
}