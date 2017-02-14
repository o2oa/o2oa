package com.x.cms.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { 
		"/jaxrs/image/encode/*", "/servlet/image/encode/*"
		})
public class CmsAnonyJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}