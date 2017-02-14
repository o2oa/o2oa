package com.x.processplatform.core.processing.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import com.x.processplatform.core.entity.content.Work;

public class TestClient {
	@Test
	public void test() throws Exception {
//		Request request = Request
//				.Post("http://xa.ray.local:8080/x_processplatform_service_processing/jaxrs/identity/list");
//		Gson gson = XGsonBuilder.instance();
//		List<String> data = new ArrayList<>();
//		data.add("张三");
//		data.add("张三");
//		if (null != data) {
//			request.bodyString(gson.toJson(data), ContentType.APPLICATION_JSON);
//		}
//		String str = request.execute().returnContent().asString(Charset.forName("UTF-8"));
//		System.out.println(str);

	}

	@Test
	public void test1() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("work", new Work());
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("nashorn");
		Object o = engine.eval("(function aaa(){ return true;})()");
		System.out.println(o.getClass());
		// ScriptHelper helper = new ScriptHelper(work, null, null);
		// helper.eval("print(this.work.getId())");

	}

	@Test
	public void test2() throws Exception {
		Integer x = 5;
		Integer y = 5;
		System.out.println(Objects.equals(x, 5));
	}

	@Test
	public void test3() throws Exception {
		System.out.println(FilenameUtils.getBaseName("D:/ssss/ddd/aaaa"));
	}

}
