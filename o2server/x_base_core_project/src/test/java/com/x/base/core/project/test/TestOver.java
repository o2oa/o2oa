package com.x.base.core.project.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class TestOver {

	public void exe(Integer i) {
		System.out.println("exe integer:" + i);
	}

	public void exe(Number n) {
		System.out.println("exe number:" + n);
	}

	public void exe(Double d) {
		System.out.println("exe double:" + d);
	}

	@Test
	public void test2() {
		String str = "aaa{},bbb{},ccc.";
		System.out.println(StringUtils.replaceEachRepeatedly(str, new String[] { "{}" }, new String[] { "111" }));
	}

	@Test
	public void test1() {
		System.out.println(com.x.base.core.entity.JpaObject.createId());
	}

	@Test
	public void test3() {
		String str = "com.x.processplatform.core.entity.log.Work";
		String aaa = "com.x.processplatform.core.entity.*";
		System.out.println(StringUtils.substringBeforeLast(aaa, "*"));
		System.out.println(StringUtils.startsWith(str, StringUtils.substringBeforeLast(aaa, "*")));
	}

	@Test
	public void test4() {
		Pattern test = Pattern.compile("^(\\S+)\\@(\\S+)\\@P$");
		String str = "杭州111@1124@P";
		Matcher matcher = test.matcher(str);
		if (matcher.find()) {
			System.out.println(matcher.group(0));
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
		}
	}

}
