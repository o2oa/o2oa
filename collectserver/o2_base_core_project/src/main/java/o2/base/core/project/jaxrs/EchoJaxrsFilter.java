package o2.base.core.project.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/jaxrs/echo/*" })
public class EchoJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
