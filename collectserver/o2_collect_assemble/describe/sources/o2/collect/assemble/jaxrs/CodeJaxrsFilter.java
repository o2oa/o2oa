package o2.collect.assemble.jaxrs;

import javax.servlet.annotation.WebFilter;

import o2.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/code/*", asyncSupported = true)
public class CodeJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
