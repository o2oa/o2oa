package com.x.processplatform.core.entity.element;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class RouteProperties extends JsonProperties {

	private static final long serialVersionUID = -7792270726211126577L;

	@FieldDescribe("是否启用异步返回.默认为false")
	private Boolean asyncSupported = false;

	@FieldDescribe("选择优先路由时是否直接执行路由(一票否决),默认null.")
	private Boolean soleDirect;

	@FieldDescribe("默认选中的路由.")
	private Boolean defaultSelected;

	public Boolean getAsyncSupported() {
		return asyncSupported;
	}

	public void setAsyncSupported(Boolean asyncSupported) {
		this.asyncSupported = asyncSupported;
	}

	public Boolean getSoleDirect() {
		return BooleanUtils.isTrue(soleDirect);
	}

	public void setSoleDirect(Boolean soleDirect) {
		this.soleDirect = soleDirect;
	}

	public Boolean getDefaultSelected() {
		return defaultSelected;
	}

	public void setDefaultSelected(Boolean defaultSelected) {
		this.defaultSelected = defaultSelected;
	}

}
