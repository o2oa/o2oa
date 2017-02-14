package com.x.component.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/component/*" })
public class ComponentJaxrsFilter extends ManagerUserJaxrsFilter {

}