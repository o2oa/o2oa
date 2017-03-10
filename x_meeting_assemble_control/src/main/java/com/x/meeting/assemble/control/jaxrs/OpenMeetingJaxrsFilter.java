package com.x.meeting.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/openmeeting/*" })
public class OpenMeetingJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
