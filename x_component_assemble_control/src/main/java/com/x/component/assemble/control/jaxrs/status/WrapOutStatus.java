package com.x.component.assemble.control.jaxrs.status;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.component.assemble.control.jaxrs.wrapout.WrapOutComponent;

@Wrap
public class WrapOutStatus extends GsonPropertyObject {
	private List<WrapOutComponent> allowList;
	private List<WrapOutComponent> denyList;

	public List<WrapOutComponent> getAllowList() {
		return allowList;
	}

	public void setAllowList(List<WrapOutComponent> allowList) {
		this.allowList = allowList;
	}

	public List<WrapOutComponent> getDenyList() {
		return denyList;
	}

	public void setDenyList(List<WrapOutComponent> denyList) {
		this.denyList = denyList;
	}
}
