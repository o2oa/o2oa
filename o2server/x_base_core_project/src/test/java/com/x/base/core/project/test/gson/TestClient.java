package com.x.base.core.project.test.gson;

import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.project.gson.XGsonBuilder;

public class TestClient {

	public JsonElement mergeSection(JsonElement data, String[] paths, String key, JsonElement source) throws Exception {
		JsonObject data_part_object = this.navigateElseEmptyObject(data, paths).getAsJsonObject();
		JsonObject source_part_object = this.navigateElseEmptyObject(source, paths).getAsJsonObject();
		for (Entry<String, JsonElement> entry : source_part_object.entrySet()) {
			if (!StringUtils.equals(key, entry.getKey())) {
				data_part_object.add(entry.getKey(), entry.getValue());
			}
		}
		return data;
	}

	private JsonElement navigateElseEmptyObject(JsonElement jsonElement, String[] paths) throws Exception {
		if (paths.length == 0) {
			return jsonElement;
		}
		if (jsonElement.isJsonPrimitive() || jsonElement.isJsonNull()) {
			return new JsonObject();
		}
		if (jsonElement.isJsonArray()) {
			return navigateElseEmptyObject(jsonElement.getAsJsonArray().get(NumberUtils.toInt(paths[0])),
					ArrayUtils.remove(paths, 0));
		}
		return navigateElseEmptyObject(jsonElement.getAsJsonObject().get(paths[0]), ArrayUtils.remove(paths, 0));
	}

	@Test
	public void test2() throws Exception {
		String json1 = "{'level':'aaaa','city':{'area1':'hangzhou'},'cat':[{'tj':{'a1':true,'a2':false}},{'qt':111}]}";
		String json2 = "{'city':{'area2':'ningbo'},'cat':[{'tj':{'a2':true,'a3':'ggg'}},{'qt':222}]}";
		JsonElement data1 = XGsonBuilder.instance().fromJson(json1, JsonElement.class);
		JsonElement data2 = XGsonBuilder.instance().fromJson(json2, JsonElement.class);
		System.out.println(mergeSection(data1, new String[] { "cat", "0", "tj" }, "", data2));
	}

}
