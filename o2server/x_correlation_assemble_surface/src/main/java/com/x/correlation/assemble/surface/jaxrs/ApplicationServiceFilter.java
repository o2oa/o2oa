package com.x.correlation.assemble.surface.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/service/*", asyncSupported = true)
public class ApplicationServiceFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
