package com.x.organization.core.entity.test;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.organization.core.entity.Group;

public class TestClient {

	@Test
	public void test() throws Exception {
		String value1 = "aaaaaaaaaaaaaaaaaaaaa";
		String value2 = "aaaaaaaaaaaaaaaaaaaaavvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv";
		System.out.println(JpaObjectTools.withinDefinedLength(value1, Group.class, "id"));
		System.out.println(JpaObjectTools.withinDefinedLength(value2, Group.class, "id"));
		System.out.println(JpaObjectTools.withinDefinedLength(value1, Group.class, "persodnList"));
		System.out.println(JpaObjectTools.withinDefinedLength(value2, Group.class, "personList"));
	}

	@Test
	public void test1() {
		Field field = FieldUtils.getField(Attachment.class, "id");
		System.out.println(field);
	}
}
