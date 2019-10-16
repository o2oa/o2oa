package com.x.base.core.project.tools;

import java.util.Date;

import org.quartz.CronExpression;

public class CronTools {
	public static Date next(String expression, Date lastEndDate) throws Exception {
		CronExpression cron = new CronExpression(expression);
		return cron.getNextValidTimeAfter(lastEndDate == null ? DateTools.parse("2018-01-01 00:00:00") : lastEndDate);
	}
}
