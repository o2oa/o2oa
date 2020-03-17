package com.x.cms.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { 
		"/jaxrs/image/encode/*", 
		"/jaxrs/anonymous/*", 
		"/servlet/image/encode/*", 
		"/servlet/image/resize/*"
		}, asyncSupported = true )
public class CmsAnonyJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}