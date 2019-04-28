package com.x.program.center.test;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;
import org.quartz.CronExpression;

import com.x.base.core.project.tools.DateTools;

public class TestClient {

	@Test
	public void test() {
		String name = "张三丰";
		System.out.println(name.substring(0, 1));
		System.out.println(name.substring(1));
	}

	@Test
	public void test1() {
		String name = "张三丰";
		System.out.println(StringEscapeUtils.escapeJava(name));
	}

	@Test
	public void test2() throws ParseException {
		CronExpression cron = new CronExpression("1 30 1,12 * * ?");
		System.out.println(DateTools.format(cron.getNextValidTimeAfter(new Date())));
	}

}
