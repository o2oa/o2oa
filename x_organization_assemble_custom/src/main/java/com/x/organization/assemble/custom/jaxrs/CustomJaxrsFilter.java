package com.x.organization.assemble.custom.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/custom/*" })
public class CustomJaxrsFilter extends ManagerUserJaxrsFilter {

}
