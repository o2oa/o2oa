package test.com.x.processplatform.core.entity;

import java.util.Arrays;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.processplatform.core.entity.content.Data;

public class TestClient {
	public static void main(String[] args) {

		Data data = new Data();

		data.put("a", Arrays.asList("bb", "c"));
		
		System.out.println(XGsonBuilder.toJson(data));

		data.replaceContent("{\"d\":\"aaa\"}");
		System.out.println(XGsonBuilder.toJson(data));
	}

}
