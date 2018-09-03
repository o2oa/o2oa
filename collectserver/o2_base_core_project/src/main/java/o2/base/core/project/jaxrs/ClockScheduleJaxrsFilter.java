package o2.base.core.project.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "/jaxrs/clockschedule/*", asyncSupported = true)
public class ClockScheduleJaxrsFilter extends CipherManagerJaxrsFilter {

}
