package com.x.processplatform.core.designer.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import com.x.base.core.project.tools.StringTools;
import com.x.organization.core.entity.Group;

public class TestClient {
	@Test
	public void test() throws Exception {
		Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5a-zA-Z0-9\\_\\(\\)\\-\\.]*$");
		Matcher matcher = pattern.matcher("aaa");
		System.out.println(matcher.find());
	}

	@Test
	public void test1() throws Exception {
		Group o = new Group();
		o.setId("s");
		List<String> list = new ArrayList<>();
		list.add("b");
		list.add("c");
		list.add("d");
		list.add("e");
		o.setPersonList(list);
		Field l = FieldUtils.getField(Group.class, "personList", true);
		Field s = FieldUtils.getField(Group.class, "id", true);
		StringTools.replaceFieldValue(o, l, "c", "o");
		StringTools.replaceFieldValue(o, s, "s", "z");
		System.out.println(FieldUtils.readField(l, o, true));
		System.out.println(FieldUtils.readField(s, o, true));
	}
}
