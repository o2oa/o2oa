package com.x.meeting.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/meeting/*" })
public class MeetingJaxrsFilter extends ManagerUserJaxrsFilter {

}
