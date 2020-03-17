package com.x.component.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/status/*" }, asyncSupported = true)
public class StatusJaxrsFilter extends ManagerUserJaxrsFilter {

}
