package com.x.component.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/component/*" }, asyncSupported = true)
public class ComponentJaxrsFilter extends ManagerUserJaxrsFilter {

}