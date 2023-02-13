package com.x.base.core.entity.dataitem;

import com.x.base.core.entity.JpaObject;

public enum ItemType {
	/*
	 * JSONElement的类型，
	 * 2020年3月11日： Simon Wu
	 * o:JsonObject
	 * a:JsonArray
	 * p:JsonPrimitive 基本类型
	 * n:JsonNull
	 * j:JsonElement
	 * */
	o, a, p, n,j;
	public static final int length = JpaObject.length_1B;
}
