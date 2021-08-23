package com.x.processplatform.core.entity.element.wrap;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.ApplicationDict;

public class WrapApplicationDict extends ApplicationDict {

	private static final long serialVersionUID = 1354157015715480102L;

	public static final WrapCopier<ApplicationDict, WrapApplicationDict> outCopier = WrapCopierFactory
			.wo(ApplicationDict.class, WrapApplicationDict.class, null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapApplicationDict, ApplicationDict> inCopier = WrapCopierFactory
			.wi(WrapApplicationDict.class, ApplicationDict.class, null, JpaObject.FieldsUnmodifyExcludeId, false);

	@FieldDescribe("数据字典数据")
	private JsonElement data;

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}

}
