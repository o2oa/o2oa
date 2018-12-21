package com.x.meeting.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/openmeeting/*", asyncSupported = true)
public class OpenMeetingJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
