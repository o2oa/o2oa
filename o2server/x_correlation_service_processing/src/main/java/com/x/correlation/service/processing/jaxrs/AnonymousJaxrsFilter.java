package com.x.correlation.service.processing.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/anonymous/*", asyncSupported = true)
public class AnonymousJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
