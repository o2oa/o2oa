package com.x.organization.assemble.personal.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/servlet/icon/*" })
public class IconServletFilter extends ManagerUserJaxrsFilter {

}
