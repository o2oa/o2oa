package com.x.base.core.project.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;

public class ActionResponse extends ActionResult<JsonElement> {

	private static final long serialVersionUID = 6026668009499793257L;

	public <T> T getData(Class<T> clz) throws Exception {
		if (Objects.equals(this.type, Type.connectFatal) || Objects.equals(this.type, Type.error)) {
			throw new Exception(this.message);
		}
		if (null == data) {
			return null;
		}
		return gson.fromJson(gson.toJsonTree(data), clz);
	}

	public <T> List<T> getDataAsList(Class<T> clz) throws Exception {
		if (Objects.equals(this.type, Type.connectFatal) || Objects.equals(this.type, Type.error)) {
			throw new Exception(this.message);
		}
		List<T> list = new ArrayList<T>();
		if (null == data || data.isJsonNull()) {
			return list;
		}
		if (!data.isJsonArray()) {
			throw new Exception("data is not array.");
		}
		data.getAsJsonArray().forEach(o -> {
			list.add(gson.fromJson(o, clz));
		});
		return list;
	}

}
