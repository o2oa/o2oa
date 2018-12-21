package com.x.collaboration.core.message.test.dialog;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.x.base.core.project.gson.XGsonBuilder;

public class TestClient {
	@Test
	public void test() throws Exception {
		NotificationFormatOut format = new NotificationFormatOut();
		FileUtils.writeStringToFile(new File("NotificationFormatOut.json"), XGsonBuilder.toJson(format));
	}

	@Test
	public void test1() throws Exception {
		DialogFormatOut format = new DialogFormatOut();
		FileUtils.writeStringToFile(new File("DialogFormatOut.json"), XGsonBuilder.toJson(format));
	}
}
