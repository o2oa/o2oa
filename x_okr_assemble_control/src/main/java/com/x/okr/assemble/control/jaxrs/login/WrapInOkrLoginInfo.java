package com.x.okr.assemble.control.jaxrs.login;
import java.io.Serializable;

import com.x.base.core.gson.GsonPropertyObject;

public class WrapInOkrLoginInfo extends GsonPropertyObject implements Serializable{

	private static final long serialVersionUID = -5076990764713538973L;

	private String loginIdentity = null;

	public String getLoginIdentity() {
		return loginIdentity;
	}

	public void setLoginIdentity(String loginIdentity) {
		this.loginIdentity = loginIdentity;
	}
}
