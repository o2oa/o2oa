package com.x.meeting.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/floor/*", asyncSupported = true)
public class FloorJaxrsFilter extends ManagerUserJaxrsFilter {

}
