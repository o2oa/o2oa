package com.x.attendance.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;


@WebFilter(urlPatterns = "/jaxrs/qywxstatistic/*", asyncSupported = true)
public class QywxStatisticJaxrsFilter extends ManagerUserJaxrsFilter {
}
