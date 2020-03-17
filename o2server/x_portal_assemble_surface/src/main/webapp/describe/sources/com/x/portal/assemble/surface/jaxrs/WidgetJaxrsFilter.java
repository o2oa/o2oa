package com.x.portal.assemble.surface.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/widget/*", asyncSupported = true)
public class WidgetJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
