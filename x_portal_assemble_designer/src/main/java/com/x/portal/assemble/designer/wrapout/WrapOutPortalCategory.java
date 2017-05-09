package com.x.portal.assemble.designer.wrapout;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.portal.core.entity.Portal;

@Wrap(Portal.class)
public class WrapOutPortalCategory extends GsonPropertyObject {

	private String protalCategory;
	private Long count;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getProtalCategory() {
		return protalCategory;
	}

	public void setProtalCategory(String protalCategory) {
		this.protalCategory = protalCategory;
	}
}
