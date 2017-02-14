package com.x.processplatform.assemble.designer.wrapout;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Application;

@Wrap(Application.class)
public class WrapOutApplicationCategory extends GsonPropertyObject {

	private String applicationCategory;
	private Long count;

	public String getApplicationCategory() {
		return applicationCategory;
	}

	public void setApplicationCategory(String applicationCategory) {
		this.applicationCategory = applicationCategory;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
