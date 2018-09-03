package o2.collect.assemble.jaxrs;

import javax.servlet.annotation.WebFilter;

import o2.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/authentication/*", asyncSupported = true)
public class AuthenticationJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
