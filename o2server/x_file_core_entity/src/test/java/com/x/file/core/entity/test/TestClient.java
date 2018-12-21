package com.x.file.core.entity.test;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.file.core.entity.personal.Attachment;

public class TestClient {

	@Test
	public void test() throws Exception {
		String value1 = "aaaaaaaaaaaaaaaaaaaaa";
		String value2 = "aaaaaaaaaaaaaaaaaaaaavvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv";
		System.out.println(JpaObjectTools.withinDefinedLength(value1, Attachment.class, "id"));
		System.out.println(JpaObjectTools.withinDefinedLength(value2, Attachment.class, "id"));
	}

	@Test
	public void test1() {
		Field field = FieldUtils.getField(Attachment.class, "id");
		System.out.println(field);
	}
}
