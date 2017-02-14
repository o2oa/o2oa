package com.x.bbs.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { 
		"/jaxrs/image/encode/*", "/servlet/image/encode/*", "/jaxrs/setting/*"
		})
public class BBSAnonyJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {
}
