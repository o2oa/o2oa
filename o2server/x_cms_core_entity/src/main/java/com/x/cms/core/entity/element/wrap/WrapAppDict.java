package com.x.cms.core.entity.element.wrap;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.cms.core.entity.element.AppDict;

public class WrapAppDict extends AppDict {

	private static final long serialVersionUID = 1354157015715480102L;

	public static WrapCopier<AppDict, WrapAppDict> outCopier = WrapCopierFactory
			.wo(AppDict.class, WrapAppDict.class, null, JpaObject.FieldsInvisible);

	public static WrapCopier<WrapAppDict, AppDict> inCopier = WrapCopierFactory
			.wi(WrapAppDict.class, AppDict.class, null, JpaObject.FieldsUnmodifyExcludeId);

	@FieldDescribe("数据字典数据")
	private JsonElement data;

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}

}
