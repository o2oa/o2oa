package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;


@WebFilter(urlPatterns = "/jaxrs/shift/*", asyncSupported = true)
public class ShiftJaxrsFilter extends ManagerUserJaxrsFilter {
}
