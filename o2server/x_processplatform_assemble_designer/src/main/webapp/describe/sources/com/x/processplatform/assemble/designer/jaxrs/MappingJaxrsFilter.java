package com.x.processplatform.assemble.designer.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/mapping/*", asyncSupported = true)
public class MappingJaxrsFilter extends ManagerUserJaxrsFilter {

}
