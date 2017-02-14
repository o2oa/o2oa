package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.Calendar;
import java.util.Date;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;

public class ActionBase extends StandardJaxrsAction {

	protected Date getStart() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		return cal.getTime();
	}

}
