package com.x.cms.assemble.control.jaxrs.appdictitem;

import com.google.gson.JsonElement;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.element.AppDict;

@Wrap(AppDict.class)
public class WrapOutAppDictItem extends GsonPropertyObject {

	public WrapOutAppDictItem(AppDict o) throws Exception {
		o.copyTo(this);
	}

	private String id;
	private String application;
	private String name;
	private String alias;
	private String description;
	private JsonElement data;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}

}
