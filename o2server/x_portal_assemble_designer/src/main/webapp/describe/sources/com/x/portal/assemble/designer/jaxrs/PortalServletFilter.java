package com.x.portal.assemble.designer.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/servlet/portal/*", asyncSupported = true)
public class PortalServletFilter extends CipherManagerUserJaxrsFilter {

}
