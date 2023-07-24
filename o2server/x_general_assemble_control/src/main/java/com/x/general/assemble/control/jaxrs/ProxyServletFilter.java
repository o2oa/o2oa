package com.x.general.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/servlet/proxy/*", asyncSupported = true)
public class ProxyServletFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
