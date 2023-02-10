package com.x.attendance.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;


@WebFilter(urlPatterns = "/jaxrs/workplacev2/*", asyncSupported = true)
public class WorkPlaceJaxrsFilter extends ManagerUserJaxrsFilter {
}
