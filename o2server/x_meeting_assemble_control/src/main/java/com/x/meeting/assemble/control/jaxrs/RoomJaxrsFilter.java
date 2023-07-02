package com.x.meeting.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/room/*", asyncSupported = true)
public class RoomJaxrsFilter extends ManagerUserJaxrsFilter {

}
