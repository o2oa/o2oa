package com.x.bbs.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { 
		"/jaxrs/image/encode/*", 
		"/servlet/image/encode/*", 
		"/jaxrs/setting/*",
		"/jaxrs/uuid/*"
		}, asyncSupported = true)
public class BBSAnonyJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {
}
