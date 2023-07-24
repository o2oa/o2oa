package com.x.meeting.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/servlet/attachment/*", asyncSupported = true)
public class AttachmentServletFilter extends ManagerUserJaxrsFilter {

}
