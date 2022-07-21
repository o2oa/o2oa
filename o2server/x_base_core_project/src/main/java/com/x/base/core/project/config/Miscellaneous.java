package com.x.base.core.project.config;

import com.x.base.core.project.annotation.FieldDescribe;

public class Miscellaneous extends ConfigObject {

	private static final long serialVersionUID = 4393280516414081348L;

	private static final Boolean DEFAULT_WEBSOCKETENABLE = true;

	public static Miscellaneous defaultInstance() {
		Miscellaneous o = new Miscellaneous();
		o.webSocketEnable = DEFAULT_WEBSOCKETENABLE;
		return new Miscellaneous();
	}

	@FieldDescribe("是否启用webSocket链接.")
	private Boolean webSocketEnable;

	public Boolean getWebSocketEnable() {
		return null == this.webSocketEnable ? DEFAULT_WEBSOCKETENABLE : this.webSocketEnable;
	}

}
