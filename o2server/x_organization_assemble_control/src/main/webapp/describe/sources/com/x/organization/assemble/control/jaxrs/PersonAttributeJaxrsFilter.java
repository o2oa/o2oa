package com.x.organization.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/personattribute/*", asyncSupported = true)
public class PersonAttributeJaxrsFilter extends ManagerUserJaxrsFilter {

}
