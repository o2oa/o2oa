package com.x.organization.assemble.personal.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/icon/*", asyncSupported = true)
public class IconJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
