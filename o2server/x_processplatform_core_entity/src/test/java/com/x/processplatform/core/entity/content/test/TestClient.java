package com.x.processplatform.core.entity.content.test;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.processplatform.core.entity.content.Data;

public class TestClient {

	@Test
	public void test() throws Exception {

		String value = FileUtils.readFileToString(new File("src/test/resources/test.json"), DefaultCharset.charset);

		Data data = XGsonBuilder.instance().fromJson(value, Data.class);

		Object o = data.find("$work.workId");

		System.out.println(o);

	}

	@Test
	public void test1() throws Exception {

		String value = FileUtils.readFileToString(new File("src/test/resources/test.json"), DefaultCharset.charset);

		Data data = XGsonBuilder.instance().fromJson(value, Data.class);

		Object o = data.find("arrays");

		System.out.println(o);

	}

}
