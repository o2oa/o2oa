package com.x.base.core.project.jaxrs;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class TokenFilter implements Filter {

	protected void options(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(204);
	}
}
