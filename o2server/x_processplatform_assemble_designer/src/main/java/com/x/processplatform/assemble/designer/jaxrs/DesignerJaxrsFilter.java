package com.x.processplatform.assemble.designer.jaxrs;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/designer/*", asyncSupported = true)
public class DesignerJaxrsFilter extends ManagerUserJaxrsFilter {

}
