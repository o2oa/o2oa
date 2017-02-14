package com.x.organization.assemble.express.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/servlet/icon/*")
public class IconJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
