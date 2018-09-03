package o2.collect.assemble.jaxrs;

import javax.servlet.annotation.WebFilter;

import o2.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/applog/*", asyncSupported = true)
public class AppLogJaxrsFilter extends CipherManagerJaxrsFilter {

}
