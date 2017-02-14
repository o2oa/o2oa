package com.x.common.core.application.definition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.x.base.core.gson.GsonPropertyObject;

public class PasswordDefinition extends LoadableDefinition {

	public static final String RegularExpression_Script = "^\\((.+?)\\)$";

	public static PasswordDefinition INSTANCE;

	public static final String FILE_NAME = "passwordDefinition.json";

	private String key;

	private Integer passwordPeriod;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getPasswordPeriod() {
		return passwordPeriod;
	}

	public void setPasswordPeriod(Integer passwordPeriod) {
		this.passwordPeriod = passwordPeriod;
	}

	@Test
	public void test1() {
		Pattern pattern = Pattern.compile(RegularExpression_Script);
		Matcher matcher = pattern.matcher("(person.getName())");
		if (matcher.matches()) {
			System.out.println(matcher.group(1));
		}

	}

}