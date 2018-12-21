package com.x.cms.assemble.control;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Control extends GsonPropertyObject {
	private Boolean allowDelete;

	public Boolean getAllowDelete() {
		return allowDelete;
	}

	public void setAllowDelete(Boolean allowDelete) {
		this.allowDelete = allowDelete;
	}

}