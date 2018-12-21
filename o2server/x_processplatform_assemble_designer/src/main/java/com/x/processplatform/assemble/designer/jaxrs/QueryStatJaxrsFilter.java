package com.x.processplatform.assemble.designer.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/querystat/*", asyncSupported = true)
public class QueryStatJaxrsFilter extends ManagerUserJaxrsFilter {

}
