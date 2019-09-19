package com.x.query.assemble.designer.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;

public class TestClient {
	@Test
	public void insert() throws Exception {

		String address = "http://127.0.0.1:20020/x_query_assemble_designer/jaxrs/table/dd/row";
		for (int j = 0; j < 200; j++) {
			List<JsonElement> list = new ArrayList<>();
			for (int i = 0; i < 200; i++) {
				String value = Objects.toString(j * 100 + i);
				Map<String, Object> map = new HashMap<>();
				map.put("sf1", "sf1" + value);
				List<String> os = new ArrayList<>();
				os.add("lsf1" + value);
				map.put("lsf1", os);
				map.put("slf1", "slf1"
						+ "你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好");
				list.add(XGsonBuilder.instance().toJsonTree(map));
			}
			JsonElement jsonElement = HttpConnection.postAsObject(address, null, XGsonBuilder.toJson(list),
					JsonElement.class);
			System.out.println(jsonElement);
		}

	}

	@Test
	public void test() throws Exception {

		String name1 = "d:/aaa/bbb";
		String name2 = "d:\\aaa\\bbb";

		System.out.println(FilenameUtils.equalsNormalized(name1, name2));

	}
}
