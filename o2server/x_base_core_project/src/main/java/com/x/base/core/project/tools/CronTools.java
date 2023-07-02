package com.x.base.core.project.tools;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

public class CronTools {

	private CronTools() {
		// nothing
	}

	public static Date next(String expression, Date lastEndDate) throws Exception {
		CronExpression cron = new CronExpression(expression);
		return cron.getNextValidTimeAfter(lastEndDate == null ? DateTools.parse("2018-01-01 00:00:00") : lastEndDate);
	}

	public static boolean available(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		return CronExpression.isValidExpression(str);
	}
}
