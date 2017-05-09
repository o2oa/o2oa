package com.x.base.core.application.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/clockschedule/*", asyncSupported = true)
public class ClockScheduleJaxrsFilter extends CipherManagerJaxrsFilter {

}
