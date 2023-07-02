package com.x.base.core.project.http;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapOutId extends GsonPropertyObject {

	public WrapOutId() {
	}

	public WrapOutId(String id) throws Exception {
		this.id = id;
	}

	private String id;

	public String getId() {
		return id;
	}

	public static Type collectionType = new TypeToken<ArrayList<WrapOutId>>() {
	}.getType();

}
