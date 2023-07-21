package com.x.processplatform.assemble.designer.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/form/*", asyncSupported = true)
public class FormJaxrsFilter extends ManagerUserJaxrsFilter {

}
