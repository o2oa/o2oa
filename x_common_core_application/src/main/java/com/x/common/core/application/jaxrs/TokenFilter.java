package com.x.common.core.application.jaxrs;

import javax.servlet.Filter;

import com.x.common.core.application.AbstractThisApplication;

public abstract class TokenFilter implements Filter {
	protected String getTokenKey() {
		return AbstractThisApplication.center.getCipher();
	}
}
