package com.x.processplatform.service.processing.processor.manual;

import org.junit.Test;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.ListTools;

public class TestClient {

	@Test
	public void test() {

		TaskIdentities taskIdentities = new TaskIdentities(ListTools.toList("a", "b", "c"));

		taskIdentities.addIdentity("d");

		System.out.println(XGsonBuilder.toJson(taskIdentities));

		taskIdentities.removeIdentity("c");

		System.out.println(XGsonBuilder.toJson(taskIdentities));
	}

}
