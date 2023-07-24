package com.x.organization.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/identity/*", asyncSupported = true)
public class IdentityJaxrsFilter extends ManagerUserJaxrsFilter {

}
